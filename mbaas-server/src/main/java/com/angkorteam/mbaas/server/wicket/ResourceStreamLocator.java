package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.flow.FlowPage;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.core.util.resource.locator.IResourceNameIterator;
import org.apache.wicket.core.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by socheat on 5/28/16.
 */
public class ResourceStreamLocator implements IResourceStreamLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStreamLocator.class);

    private final IResourceStreamLocator streamLocator;

    public ResourceStreamLocator(IResourceStreamLocator streamLocator) {
        this.streamLocator = streamLocator;
    }

    @Override
    public IResourceStream locate(Class<?> clazz, String path) {
        String pageId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        LOGGER.info("locate : clazz {} path {}", clazz.getName(), path == null ? "" : path);
        IResourceStream stream = this.streamLocator.locate(clazz, path);
        return stream;
    }

    @Override
    public IResourceStream locate(Class<?> clazz, String path, String style, String variation, Locale locale, String extension, boolean strict) {
        LOGGER.info("locate : clazz {} path {} style {} variation {} locale {} extension {} strict {}", clazz.getName(), path == null ? "" : path, style == null ? "" : style, variation == null ? "" : variation, locale == null ? "" : locale.getDisplayName(), extension == null ? "" : extension, strict);
        if (FlowPage.class.getName().replaceAll("\\.", "/").equals(path)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(style);
            String html = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.HTML + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", String.class, variation);
            File temp = new File(FileUtils.getTempDirectory(), path + "_" + variation + "_" + style + "_" + locale.toString() + ".html");
            if (!temp.exists()) {
                temp.getParentFile().mkdirs();
                try {
                    FileUtils.write(temp, html);
                } catch (IOException e) {
                }
            }
        }
        IResourceStream stream = this.streamLocator.locate(clazz, path, style, variation, locale, extension, strict);
        return stream;
    }

    @Override
    public IResourceNameIterator newResourceNameIterator(String path, Locale locale, String style, String variation, String extension, boolean strict) {
        String pageId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        LOGGER.info("newResourceNameIterator : path {} locale {} style {} variation {} extension {} strict {}", path == null ? "" : path, locale == null ? "" : locale.getDisplayName(), style == null ? "" : style, variation == null ? "" : variation, extension == null ? "" : extension, strict);
        IResourceNameIterator resourceName = this.streamLocator.newResourceNameIterator(path, locale, style, variation, extension, strict);
        return resourceName;
    }
}

package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.FilePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.FileProvider;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
public class FileBrowsePage extends MBaaSPage {

    @Override
    public String getPageUUID() {
        return FileBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        FileProvider provider = new FileProvider();

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("fileId"), "fileId", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("name"), "name", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Integer, Model.of("length"), "length", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("mime"), "mime", this::getModelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 17);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", FileBrowsePage.class);
        layout.add(refreshLink);
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget ajaxRequestTarget) {
        DSLContext context = Spring.getBean(DSLContext.class);
        System system = Spring.getBean(System.class);
        FileTable fileTable = Tables.FILE.as("fileTable");
        String fileId = (String) object.get("fileId");
        FilePojo fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(fileId)).fetchOneInto(FilePojo.class);
        Configuration configuration = system.getConfiguration();
        if ("Delete".equals(link)) {
            String repo = configuration.getString(Configuration.RESOURCE_REPO);
            String path = fileRecord.getPath();
            String name = fileRecord.getName();
            File file = new File(repo, path + "/" + name);
            FileUtils.deleteQuietly(file);
            context.delete(fileTable).where(fileTable.FILE_ID.eq(fileId)).execute();
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("fileId", fileId);
            setResponsePage(FileModifyPage.class, parameters);
            return;
        }
        if ("View".equals(link)) {
            StringBuffer address = new StringBuffer();
            address.append(getHttpAddress()).append("/api/resource").append(fileRecord.getPath()).append("/").append(fileRecord.getName());
            RedirectPage page = new RedirectPage(address);
            setResponsePage(page);
            return;
        }
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("View", Model.of("View"));
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Boolean clickable(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        if ("View".equals(link)) {
            return true;
        }
        return false;
    }

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        if ("Edit".equals(link)) {
            return ItemCss.PRIMARY;
        }
        if ("View".equals(link)) {
            return ItemCss.PRIMARY;
        }
        return ItemCss.NONE;
    }

    private Object getModelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }

}

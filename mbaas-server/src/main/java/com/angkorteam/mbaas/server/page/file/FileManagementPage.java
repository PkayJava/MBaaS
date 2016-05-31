package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.FileProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/file/management")
public class FileManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "File Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        FileProvider provider = new FileProvider(getSession().getApplicationCode());

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("fileId", this), "fileId", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("applicationUser", this), "applicationUser", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("length", this), "length", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("mime", this), "mime", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "View", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 17);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", FileManagementPage.class);
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String fileId = (String) object.get("fileId");
        if ("Delete".equals(link)) {
            Map<String, Object> fileRecord = null;
            fileRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.FILE + " WHERE " + Jdbc.File.FILE_ID + " = ?", fileId);
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            String repo = configuration.getString(Constants.RESOURCE_REPO);
            String path = (String) fileRecord.get(Jdbc.File.PATH);
            String name = (String) fileRecord.get(Jdbc.File.NAME);
            File file = new File(repo + "/" + getSession().getApplicationCode() + "/file" + path + "/" + name);
            FileUtils.deleteQuietly(file);
            jdbcTemplate.update("DELETE FROM " + Jdbc.FILE + " WHERE " + Jdbc.File.FILE_ID + " = ?", fileId);
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("fileId", fileId);
            setResponsePage(FileModifyPage.class, parameters);
            return;
        }
        if ("View".equals(link)) {
            Map<String, Object> fileRecord = null;
            fileRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.FILE + " WHERE " + Jdbc.File.FILE_ID + " = ?", fileId);
            StringBuffer address = new StringBuffer();
            address.append(getHttpAddress()).append("/api/resource/").append(getSession().getApplicationCode()).append("/file").append(fileRecord.get(Jdbc.File.PATH)).append("/").append(fileRecord.get(Jdbc.File.NAME));
            RedirectPage page = new RedirectPage(address);
            setResponsePage(page);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
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

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Edit".equals(link)) {
            return true;
        }
        if ("View".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("View".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}

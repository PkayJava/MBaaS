package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.event.TableEvent;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.FilePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.FileProvider;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
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
public class FileBrowsePage extends MBaaSPage implements TableEvent {

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
        columns.add(new TextFilteredColumn(String.class, Model.of("fileId"), "fileId", this, provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("name"), "name", provider));
        columns.add(new TextFilteredColumn(Integer.class, Model.of("length"), "length", provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("mime"), "mime", provider));
        columns.add(new ActionFilteredColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "View", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 17);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", FileBrowsePage.class);
        layout.add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
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

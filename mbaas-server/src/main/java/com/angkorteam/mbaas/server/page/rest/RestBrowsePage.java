package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.event.TableEvent;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.RestProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/3/16.
 */
public class RestBrowsePage extends MBaaSPage implements TableEvent {

    @Override
    public String getPageUUID() {
        return RestBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        RestProvider provider = new RestProvider();
        provider.selectField("restId", String.class);
        provider.selectField("system", Boolean.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredColumn(String.class, Model.of("name"), "name", this, provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("path"), "path", provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("method"), "method", provider));
        columns.add(new ActionFilteredColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", RestCreatePage.class);
        layout.add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", RestBrowsePage.class);
        layout.add(refreshLink);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String restId = (String) object.get("restId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("restId", restId);
            setResponsePage(RestModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            RestTable restTable = Tables.REST.as("restTable");
            context.delete(restTable).where(restTable.REST_ID.eq(restId)).execute();
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    private boolean hasAccess(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Go Live".equals(link) || "Stage Preview".equals(link)) {
            boolean modified = (boolean) object.get("modified");
            return modified;
        }
        return false;
    }
}

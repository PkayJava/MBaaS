package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.RestProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/3/16.
 */
public class RestBrowsePage extends MBaaSPage {

    private DataTable<Map<String, Object>, String> dataTable;

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
        provider.setSort("name", SortOrder.ASCENDING);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("name"), "name", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("method"), "method", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("path"), "path", this::getModelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        this.dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        this.dataTable.addTopToolbar(new FilterToolbar(this.dataTable, filterForm));
        filterForm.add(this.dataTable);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", RestCreatePage.class);
        layout.add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", RestBrowsePage.class);
        layout.add(refreshLink);
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Object getModelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget target) {
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
            target.add(this.dataTable);
            return;
        }
    }

    private Boolean clickable(String link, Map<String, Object> object) {
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

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Edit".equals(link)) {
            return ItemCss.PRIMARY;
        }
        if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        return ItemCss.NONE;
    }

}

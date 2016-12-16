package com.angkorteam.mbaas.server.page.menuitem;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.MenuItemProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 10/24/16.
 */
public class MenuItemBrowsePage extends MBaaSPage {

    private DataTable<Map<String, Object>, String> dataTable;

    @Override
    public String getPageUUID() {
        return MenuItemBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        MenuItemProvider provider = new MenuItemProvider();
        provider.selectField("menuItemId", String.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("title"), "title", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("icon"), "icon", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("page"), "page", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("menu"), "menu", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("section"), "section", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", this::modelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", MenuItemBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", MenuItemCreatePage.class);
        layout.add(createLink);
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget target) {
        if ("Edit".equals(link)) {
            String menuItemId = (String) object.get("menuItemId");
            PageParameters parameters = new PageParameters();
            parameters.add("menuItemId", menuItemId);
            setResponsePage(MenuItemModifyPage.class, parameters);
        } else if ("Delete".equals(link)) {
            JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
            String menuItemId = (String) object.get("menuItemId");
            jdbcTemplate.update("DELETE FROM menu_item WHERE menu_item_id = ?", menuItemId);
            target.add(this.dataTable);
        }
    }

    private Boolean clickable(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("Edit".equals(link) || "Delete".equals(link)) {
            return !system;
        }
        return false;
    }

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Edit".equals(link)) {
            return ItemCss.INFO;
        } else if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        return ItemCss.NONE;

    }

    private Object modelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }
}

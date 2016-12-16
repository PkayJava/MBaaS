package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.UserProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
 * Created by socheat on 10/24/16.
 */
public class UserBrowsePage extends MBaaSPage {

    private DataTable<Map<String, Object>, String> dataTable;

    @Override
    public String getPageUUID() {
        return UserBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        UserProvider provider = new UserProvider();
        provider.selectField("userId", String.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("fullName"), "fullName", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("login"), "login", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("roleName"), "roleName", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("status"), "status", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", this::modelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        this.dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        this.dataTable.addTopToolbar(new FilterToolbar(this.dataTable, filterForm));
        filterForm.add(this.dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", UserBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", UserCreatePage.class);
        layout.add(createLink);
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Reset PWD", Model.of("Reset PWD"));
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Object modelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget target) {
        String userId = (String) object.get("userId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserModifyPage.class, parameters);
        }
        if ("Delete".equals(link)) {
            UserTable userTable = Tables.USER.as("userTable");
            DSLContext context = Spring.getBean(DSLContext.class);
            context.delete(userTable).where(userTable.USER_ID.eq(userId)).execute();
            target.add(this.dataTable);
        }
        if ("Reset PWD".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserPasswordPage.class, parameters);
        }
    }

    private Boolean clickable(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("Edit".equals(link)) {
            return !system;
        }
        if ("Delete".equals(link)) {
            return !system;
        }
        if ("Reset PWD".equals(link)) {
            return true;
        }
        return false;
    }

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Reset PWD".equals(link) || "Edit".equals(link)) {
            return ItemCss.INFO;
        }
        if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        return ItemCss.NONE;
    }
}

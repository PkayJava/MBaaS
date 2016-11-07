package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.event.TableEvent;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.UserProvider;
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
 * Created by socheat on 10/24/16.
 */
public class UserBrowsePage extends MBaaSPage implements TableEvent {

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
        columns.add(new TextFilteredColumn(String.class, Model.of("fullName"), "fullName", this, provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("login"), "login", this, provider));
        columns.add(new TextFilteredColumn(String.class, Model.of("roleName"), "roleName", this, provider));
        columns.add(new TextFilteredColumn(Boolean.class, Model.of("status"), "status", provider));
        columns.add(new TextFilteredColumn(Boolean.class, Model.of("system"), "system", provider));
        columns.add(new ActionFilteredColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Reset PWD", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", UserBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", UserCreatePage.class);
        layout.add(createLink);
    }

    @Override
    public String onCSSLink(String s, Map<String, Object> map) {
        if ("Reset PWD".equals(s) || "Edit".equals(s)) {
            return "btn-xs btn-info";
        }
        if ("Delete".equals(s)) {
            return "btn-xs btn-danger";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String s, Map<String, Object> map) {
        String userId = (String) map.get("userId");
        if ("Edit".equals(s)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserModifyPage.class, parameters);
        }
        if ("Delete".equals(s)) {
            UserTable userTable = Tables.USER.as("userTable");
            DSLContext context = Spring.getBean(DSLContext.class);
            context.delete(userTable).where(userTable.USER_ID.eq(userId)).execute();
        }
        if ("Reset PWD".equals(s)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserPasswordPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String s, Map<String, Object> map) {
        Boolean system = (Boolean) map.get("system");
        if ("Edit".equals(s)) {
            if (system) {
                return false;
            }
            return true;
        }
        if ("Delete".equals(s)) {
            if (system) {
                return false;
            }
            return true;
        }
        if ("Reset PWD".equals(s)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String s, Map<String, Object> map) {
        return true;
    }

}

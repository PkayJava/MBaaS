package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.StaticCommon;
import com.angkorteam.mbaas.server.provider.UserProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/management")
public class UserManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        UserProvider provider = new UserProvider(getSession().getApplicationCode());
        provider.selectField(String.class, "applicationUserId");
        provider.selectField(Boolean.class, "system");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("fullName", this), "fullName", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("roleName", this), "roleName", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("status", this), "status", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Change PWD", "Suspend", "Activate"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", UserManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public String getPageHeader() {
        return "User Management";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String applicationUserId = (String) object.get("applicationUserId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Suspend".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.STATUS + " = ? WHERE " + Jdbc.User.USER_ID + " = ?", UserStatusEnum.Suspended.getLiteral(), applicationUserId);
            return;
        }
        if ("Activate".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.STATUS + " = ? WHERE " + Jdbc.User.USER_ID + " = ?", UserStatusEnum.Active.getLiteral(), applicationUserId);
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("applicationUserId", applicationUserId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
        if ("Change PWD".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("applicationUserId", applicationUserId);
            setResponsePage(UserPasswordModifyPage.class, parameters);
            return;
        }
        if ("login".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("applicationUserId", applicationUserId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return StaticCommon.hasAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return StaticCommon.hasAccess(link, object);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        return StaticCommon.onCSSLink(link, object);
    }
}

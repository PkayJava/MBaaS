package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.provider.MBaaSUserProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation({"mbaas.system"})
@Mount("/mbaas/user/management")
public class UserManagementPage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();

        MBaaSUserProvider provider = new MBaaSUserProvider();
        provider.selectField(String.class, "mbaasUserId");
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
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        String mbaasUserId = (String) object.get("mbaasUserId");
        if ("Suspend".equals(link)) {
            context.update(userTable).set(userTable.STATUS, UserStatusEnum.Suspended.getLiteral()).where(userTable.MBAAS_USER_ID.eq(mbaasUserId)).execute();
            return;
        }
        if ("Activate".equals(link)) {
            context.update(userTable).set(userTable.STATUS, UserStatusEnum.Active.getLiteral()).where(userTable.MBAAS_USER_ID.eq(mbaasUserId)).execute();
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("mbaasUserId", mbaasUserId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
        if ("Change PWD".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("mbaasUserId", mbaasUserId);
            setResponsePage(UserPasswordModifyPage.class, parameters);
            return;
        }
        if ("login".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("mbaasUserId", mbaasUserId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("login".equals(link)) {
            return !system;
        }
        if ("Edit".equals(link)) {
            return !system;
        }
        if ("Change PWD".equals(link)) {
            return true;
        }
        if ("Suspend".equals(link)) {
            return !system;
        }
        if ("Activate".equals(link)) {
            return !system;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        String status = (String) object.get("status");
        Boolean system = (Boolean) object.get("system");
        if ("Suspend".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Activate".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Active.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Edit".equals(link)) {
            if (system) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Suspend".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Activate".equals(link)) {
            return "btn-xs btn-warning";
        }
        if ("Change PWD".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}

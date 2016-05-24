package com.angkorteam.mbaas.server.page.client;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.ClientProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/7/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/client/management")
public class ClientManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Client Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ClientProvider provider = new ClientProvider(getSession().getApplicationCode());
        provider.selectField(String.class, "applicationUserId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("clientId", this), "clientId", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("clientSecret", this), "clientSecret", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("applicationUser", this), "applicationUser", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Revoke", "Grant", "Deny", "Edit"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String clientId = (String) object.get("clientId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("clientId", clientId);
            setResponsePage(ClientModifyPage.class, parameters);
        }
        if ("Grant".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.CLIENT + " SET " + Jdbc.Client.SECURITY + " = ? WHERE " + Jdbc.Client.CLIENT_ID + " = ?", SecurityEnum.Granted.getLiteral(), clientId);
            return;
        }
        if ("Deny".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.CLIENT + " SET " + Jdbc.Client.SECURITY + " = ? WHERE " + Jdbc.Client.CLIENT_ID + " = ?", SecurityEnum.Denied.getLiteral(), clientId);
            return;
        }
        if ("Revoke".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.CLIENT + " SET " + Jdbc.Client.CLIENT_SECRET + " = ? WHERE " + Jdbc.Client.CLIENT_ID + " = ?", UUID.randomUUID().toString(), clientId);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    protected boolean isAccess(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Grant".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Denied.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Deny".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Granted.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Revoke".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Grant".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Deny".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Revoke".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }
}

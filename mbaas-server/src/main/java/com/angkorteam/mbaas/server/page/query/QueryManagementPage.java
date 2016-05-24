package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.QueryProvider;
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
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/query/management")
public class QueryManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Query Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        QueryProvider provider = new QueryProvider(getSession().getApplicationCode());
        provider.selectField(String.class, "applicationUserId");
        provider.selectField(String.class, "queryId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("applicationUser", this), "applicationUser", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Edit", "Delete", "Parameter"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", QueryManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Grant".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Deny".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Parameter".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Role Privacy".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("User Privacy".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String queryId = (String) object.get("queryId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            jdbcTemplate.update("DELETE FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.QUERY_ID + " = ?", queryId);
            jdbcTemplate.update("DELETE FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", queryId);
            return;
        }
        if ("Grant".equals(link)) {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ? AND " + Jdbc.QueryParameter.TYPE + " IS NULL", int.class, queryId);
            if (count > 0) {
                PageParameters parameters = new PageParameters();
                parameters.add("queryId", queryId);
                parameters.add("granted", true);
                setResponsePage(QueryParameterModifyPage.class, parameters);
            } else {
                jdbcTemplate.update("UPDATE " + Jdbc.QUERY + " SET " + Jdbc.Query.SECURITY + " = ? WHERE " + Jdbc.Query.QUERY_ID + " = ?", SecurityEnum.Granted.getLiteral(), queryId);
            }
            return;
        }
        if ("Deny".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.QUERY + " SET " + Jdbc.Query.SECURITY + " = ? WHERE " + Jdbc.Query.QUERY_ID + " = ?", SecurityEnum.Denied.getLiteral(), queryId);
            return;
        }
        if ("Parameter".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryParameterModifyPage.class, parameters);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    protected boolean isAccess(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String queryId = (String) object.get("queryId");
        String security = (String) object.get("security");
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Grant".equals(link)) {
            if (SecurityEnum.Denied.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Deny".equals(link)) {
            if (SecurityEnum.Granted.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Parameter".equals(link)) {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", int.class, queryId);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }
}

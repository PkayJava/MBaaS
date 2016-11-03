//package com.angkorteam.mbaas.server.page.query;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.QueryParameterTable;
//import com.angkorteam.mbaas.plain.enums.SecurityEnum;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.Spring;
//import com.angkorteam.mbaas.server.function.RestoreFunction;
//import com.angkorteam.mbaas.server.page.MBaaSPage;
//import com.angkorteam.mbaas.server.provider.QueryProvider;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
//import org.apache.wicket.markup.html.link.BookmarkablePageLink;
//import org.apache.wicket.model.Model;
//import org.apache.wicket.request.mapper.parameter.PageParameters;
//import org.jooq.DSLContext;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 3/10/16.
// */
//public class QueryManagementPage extends MBaaSPage implements ActionFilteredJooqColumn.Event {
//
//    @Override
//    public String getPageUUID() {
//        return QueryManagementPage.class.getName();
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        QueryProvider provider = new QueryProvider();
//        provider.selectField(String.class, "queryId");
//
//        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
//        add(filterForm);
//
//        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
//        columns.add(new TextFilteredJooqColumn(String.class, Model.of("name"), "name", this, provider));
//        columns.add(new TextFilteredJooqColumn(String.class, Model.of("name"), "description", provider));
//        columns.add(new DateTimeFilteredJooqColumn(Model.of("name"), "dateCreated", provider));
//        columns.add(new TextFilteredJooqColumn(String.class, Model.of("name"), "security", provider));
//        columns.add(new ActionFilteredJooqColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Grant", "Deny", "Edit", "Delete", "Parameter"));
//
//        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
//        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
//        filterForm.add(dataTable);
//
//        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", QueryManagementPage.class);
//        add(refreshLink);
//    }
//
//    @Override
//    public String onCSSLink(String link, Map<String, Object> object) {
//        if ("Edit".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("Delete".equals(link)) {
//            return "btn-xs btn-danger";
//        }
//        if ("Grant".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("Deny".equals(link)) {
//            return "btn-xs btn-danger";
//        }
//        if ("Parameter".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("Role Privacy".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("User Privacy".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        return "";
//    }
//
//    @Override
//    public void onClickEventLink(String link, Map<String, Object> object) {
//        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
//        String queryId = (String) object.get("queryId");
//        if ("Edit".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("queryId", queryId);
//            setResponsePage(QueryModifyPage.class, parameters);
//            return;
//        }
//        if ("Delete".equals(link)) {
//            Application application = ApplicationUtils.getApplication();
//            RestoreFunction.backup(application.getJdbcGson(), getApplicationJdbcTemplate(), Jdbc.QUERY, queryId);
//            List<String> queryParameterIds = jdbcTemplate.queryForList("SELECT " + Jdbc.QueryParameter.QUERY_PARAMETER_ID + " FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", String.class, queryId);
//            for (String queryParameterId : queryParameterIds) {
//                RestoreFunction.backup(application.getJdbcGson(), getApplicationJdbcTemplate(), Jdbc.QUERY_PARAMETER, queryParameterId);
//            }
//            jdbcTemplate.update("DELETE FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.QUERY_ID + " = ?", queryId);
//            jdbcTemplate.update("DELETE FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", queryId);
//            return;
//        }
//        if ("Grant".equals(link)) {
//            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ? AND " + Jdbc.QueryParameter.TYPE + " IS NULL", int.class, queryId);
//            if (count > 0) {
//                PageParameters parameters = new PageParameters();
//                parameters.add("queryId", queryId);
//                parameters.add("granted", true);
//                setResponsePage(QueryParameterModifyPage.class, parameters);
//            } else {
//                jdbcTemplate.update("UPDATE " + Jdbc.QUERY + " SET " + Jdbc.Query.SECURITY + " = ? WHERE " + Jdbc.Query.QUERY_ID + " = ?", SecurityEnum.Granted.getLiteral(), queryId);
//            }
//            return;
//        }
//        if ("Deny".equals(link)) {
//            jdbcTemplate.update("UPDATE " + Jdbc.QUERY + " SET " + Jdbc.Query.SECURITY + " = ? WHERE " + Jdbc.Query.QUERY_ID + " = ?", SecurityEnum.Denied.getLiteral(), queryId);
//            return;
//        }
//        if ("Parameter".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("queryId", queryId);
//            setResponsePage(QueryParameterModifyPage.class, parameters);
//            return;
//        }
//    }
//
//    @Override
//    public boolean isClickableEventLink(String link, Map<String, Object> object) {
//        return isAccess(link, object);
//    }
//
//    protected boolean isAccess(String link, Map<String, Object> object) {
//        DSLContext context = Spring.getBean(DSLContext.class);
//        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");
//        String queryId = (String) object.get("queryId");
//        String security = (String) object.get("security");
//        if ("Edit".equals(link)) {
//            return true;
//        }
//        if ("Delete".equals(link)) {
//            return true;
//        }
//        if ("Grant".equals(link)) {
//            if (SecurityEnum.Denied.getLiteral().equals(security)) {
//                return true;
//            }
//        }
//        if ("Deny".equals(link)) {
//            if (SecurityEnum.Granted.getLiteral().equals(security)) {
//                return true;
//            }
//        }
//        if ("Parameter".equals(link)) {
//            int count = context.selectCount().from(queryParameterTable).where(queryParameterTable.QUERY_ID.eq(queryId)).fetchOneInto(int.class);
//            if (count > 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
//        return isAccess(link, object);
//    }
//}

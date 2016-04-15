package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.provider.QueryProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/query/management")
public class QueryManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Query Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        StringBuffer address = new StringBuffer();
        address.append(getHttpAddress()).append("/api/query/execute/");

        QueryProvider provider = new QueryProvider(address.toString());
        provider.selectField(String.class, "queryId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Role Privacy", "User Privacy", "Edit", "Delete", "Parameter"));

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
        DSLContext context = getDSLContext();
        String queryId = (String) object.get("queryId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            context.delete(Tables.QUERY).where(Tables.QUERY.QUERY_ID.eq(queryId)).execute();
            context.delete(Tables.QUERY_PARAMETER).where(Tables.QUERY_PARAMETER.QUERY_ID.eq(queryId)).execute();
            return;
        }
        if ("Grant".equals(link)) {
            int count = context.selectCount().from(Tables.QUERY_PARAMETER).where(Tables.QUERY_PARAMETER.QUERY_ID.eq(queryId)).and(Tables.QUERY_PARAMETER.TYPE.isNull()).fetchOneInto(int.class);
            if (count > 0) {
                PageParameters parameters = new PageParameters();
                parameters.add("queryId", queryId);
                parameters.add("granted", true);
                setResponsePage(QueryParameterModifyPage.class, parameters);
            } else {
                context.update(Tables.QUERY).set(Tables.QUERY.SECURITY, SecurityEnum.Granted.getLiteral()).where(Tables.QUERY.QUERY_ID.eq(queryId)).execute();
            }
            return;
        }
        if ("Deny".equals(link)) {
            context.update(Tables.QUERY).set(Tables.QUERY.SECURITY, SecurityEnum.Denied.getLiteral()).where(Tables.QUERY.QUERY_ID.eq(queryId)).execute();
            return;
        }
        if ("Parameter".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryParameterModifyPage.class, parameters);
            return;
        }
        if ("Role Privacy".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryRolePrivacyManagementPage.class, parameters);
        }
        if ("User Privacy".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryUserPrivacyManagementPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return isEnable(link, object);
    }

    protected boolean isEnable(String link, Map<String, Object> object) {
        DSLContext context = getDSLContext();
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
            int count = context.selectCount().from(Tables.QUERY_PARAMETER).where(Tables.QUERY_PARAMETER.QUERY_ID.eq(queryId)).fetchOneInto(int.class);
            if (count > 0) {
                return true;
            }
        }
        if ("Role Privacy".equals(link)) {
            return true;
        }
        if ("User Privacy".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return isEnable(link, object);
    }
}

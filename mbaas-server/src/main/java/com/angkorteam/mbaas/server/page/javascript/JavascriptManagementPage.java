package com.angkorteam.mbaas.server.page.javascript;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.provider.JavascriptProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
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
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/javascript/management")
public class JavascriptManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Javascript Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        JavascriptProvider provider = null;
        if (getSession().isAdministrator()) {
            provider = new JavascriptProvider();
        } else {
            provider = new JavascriptProvider(getSession().getUserId());
        }

        provider.selectField(String.class, "javascriptId");
        provider.selectField(String.class, "ownerUserId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("path", this), "path", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
        if (getSession().isAdministrator()) {
            columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("ownerUser", this), "ownerUser", provider));
        }
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", JavascriptManagementPage.class, getPageParameters());
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
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String javascriptId = (String) object.get("javascriptId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("javascriptId", javascriptId);
            setResponsePage(JavascriptModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            context.delete(Tables.JAVASCRIPT).where(Tables.JAVASCRIPT.JAVASCRIPT_ID.eq(javascriptId)).execute();
            return;
        }
        if ("Grant".equals(link)) {
            DSLContext context = getDSLContext();
            context.update(Tables.JAVASCRIPT).set(Tables.JAVASCRIPT.SECURITY, SecurityEnum.Granted.getLiteral()).where(Tables.JAVASCRIPT.JAVASCRIPT_ID.eq(javascriptId)).execute();
            return;
        }
        if ("Deny".equals(link)) {
            DSLContext context = getDSLContext();
            context.update(Tables.JAVASCRIPT).set(Tables.JAVASCRIPT.SECURITY, SecurityEnum.Denied.getLiteral()).where(Tables.JAVASCRIPT.JAVASCRIPT_ID.eq(javascriptId)).execute();
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
        String ownerUserId = (String) object.get("ownerUserId");
        if ("Edit".equals(link)) {
            if (getSession().isAdministrator()) {
                return true;
            } else {
                if (getSession().getUserId().equals(ownerUserId)) {
                    return true;
                }
            }
        }
        if ("Delete".equals(link)) {
            if (getSession().isAdministrator()) {
                return true;
            } else {
                if (getSession().getUserId().equals(ownerUserId)) {
                    return true;
                }
            }
        }
        if ("Grant".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Denied.getLiteral().equals(security)) {
                if (getSession().isAdministrator()) {
                    return true;
                } else {
                    if (getSession().getUserId().equals(ownerUserId)) {
                        return true;
                    }
                }
            }
        }
        if ("Deny".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Granted.getLiteral().equals(security)) {
                if (getSession().isAdministrator()) {
                    return true;
                } else {
                    if (getSession().getUserId().equals(ownerUserId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.page.client.ClientManagementPage;
import com.angkorteam.mbaas.server.page.push.PushSendPage;
import com.angkorteam.mbaas.server.provider.ApplicationProvider;
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
 * Created by socheat on 3/7/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/application/management")
public class ApplicationManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Application Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ApplicationProvider provider = null;
        if (getSession().isAdministrator()) {
            provider = new ApplicationProvider();
        } else {
            provider = new ApplicationProvider(getSession().getUserId());
        }

        provider.selectField(String.class, "applicationId");
        provider.selectField(String.class, "pushApplicationId");
        provider.selectField(String.class, "pushMasterSecret");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", this, provider));
        if (getSession().isAdministrator()) {
            columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("ownerUser", this), "ownerUser", provider));
        }
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("client", this), "client", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Push", "Client", "Edit"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", ApplicationManagementPage.class);
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            String applicationId = (String) object.get("applicationId");
            PageParameters parameters = new PageParameters();
            parameters.add("applicationId", applicationId);
            setResponsePage(ApplicationModifyPage.class, parameters);
            return;
        }
        if ("Client".equals(link)) {
            String applicationId = (String) object.get("applicationId");
            PageParameters parameters = new PageParameters();
            parameters.add("applicationId", applicationId);
            setResponsePage(ClientManagementPage.class, parameters);
            return;
        }
        if ("Grant".equals(link)) {
            String applicationId = (String) object.get("applicationId");
            DSLContext context = getDSLContext();
            context.update(Tables.APPLICATION).set(Tables.APPLICATION.SECURITY, SecurityEnum.Granted.getLiteral()).where(Tables.APPLICATION.APPLICATION_ID.eq(applicationId)).execute();
            return;
        }
        if ("Deny".equals(link)) {
            String applicationId = (String) object.get("applicationId");
            DSLContext context = getDSLContext();
            context.update(Tables.APPLICATION).set(Tables.APPLICATION.SECURITY, SecurityEnum.Denied.getLiteral()).where(Tables.APPLICATION.APPLICATION_ID.eq(applicationId)).execute();
            return;
        }
        if ("Push".equals(link)) {
            String applicationId = (String) object.get("applicationId");
            PageParameters parameters = new PageParameters();
            parameters.add("applicationId", applicationId);
            setResponsePage(PushSendPage.class, parameters);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Client".equals(link)) {
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
        if ("Push".equals(link)) {
            String pushApplicationId = (String) object.get("pushApplicationId");
            String pushMasterSecret = (String) object.get("pushMasterSecret");
            if (pushMasterSecret != null && !"".equals(pushMasterSecret) && pushApplicationId != null && !"".equals(pushApplicationId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Client".equals(link)) {
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
        if ("Push".equals(link)) {
            String pushApplicationId = (String) object.get("pushApplicationId");
            String pushMasterSecret = (String) object.get("pushMasterSecret");
            if (pushMasterSecret != null && !"".equals(pushMasterSecret) && pushApplicationId != null && !"".equals(pushApplicationId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Client".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Grant".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Push".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Deny".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }
}

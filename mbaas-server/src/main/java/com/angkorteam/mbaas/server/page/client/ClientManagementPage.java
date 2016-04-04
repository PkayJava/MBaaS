package com.angkorteam.mbaas.server.page.client;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.provider.ClientProvider;
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
import java.util.UUID;

/**
 * Created by socheat on 3/7/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/client/management")
public class ClientManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String applicationId;

    @Override
    public String getPageHeader() {
        return "Client Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.applicationId = getPageParameters().get("applicationId").toString();

        ClientProvider provider = new ClientProvider(this.applicationId);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("clientId", this), "clientId", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("clientSecret", this), "clientSecret", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("ownerUser", this), "ownerUser", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Revoke", "Grant", "Deny", "Edit"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);


        PageParameters parameters = new PageParameters();
        parameters.add("applicationId", applicationId);
        BookmarkablePageLink<Void> newClientLink = new BookmarkablePageLink<>("newClientLink", ClientCreatePage.class, parameters);
        add(newClientLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", ClientManagementPage.class, parameters);
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("applicationId", this.applicationId);
            parameters.add("clientId", object.get("clientId"));
            setResponsePage(ClientModifyPage.class, parameters);
        }
        if ("Grant".equals(link)) {
            String clientId = (String) object.get("clientId");
            DSLContext context = getDSLContext();
            context.update(Tables.CLIENT).set(Tables.CLIENT.SECURITY, SecurityEnum.Granted.getLiteral()).where(Tables.CLIENT.CLIENT_ID.eq(clientId)).execute();
            return;
        }
        if ("Deny".equals(link)) {
            String clientId = (String) object.get("clientId");
            DSLContext context = getDSLContext();
            context.update(Tables.CLIENT).set(Tables.CLIENT.SECURITY, SecurityEnum.Denied.getLiteral()).where(Tables.CLIENT.CLIENT_ID.eq(clientId)).execute();
            return;
        }
        if ("Revoke".equals(link)) {
            String clientId = (String) object.get("clientId");
            DSLContext context = getDSLContext();
            context.update(Tables.CLIENT).set(Tables.CLIENT.CLIENT_SECRET, UUID.randomUUID().toString()).where(Tables.CLIENT.CLIENT_ID.eq(clientId)).execute();
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
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
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
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

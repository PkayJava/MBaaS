package com.angkorteam.mbaas.server.page.session;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.server.provider.MobileProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/14/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/session/mobile")
public class SessionMobilePage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Mobile Session";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        MobileProvider provider = new MobileProvider();
        provider.selectField(Boolean.class, "mobileId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("mobileId", this), "mobileId", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("application", this), "application", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("client", this), "client", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateSeen", this), "dateSeen", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("clientIp", this), "clientIp", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("userAgent", this), "userAgent", this, provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", SessionMobilePage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            String mobileId = (String) object.get("mobileId");
            DSLContext context = getDSLContext();
            context.delete(Tables.MOBILE).where(Tables.MOBILE.MOBILE_ID.eq(mobileId)).execute();
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

}

package com.angkorteam.mbaas.server.page.socket;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.SocketProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 7/16/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/socket/management")
public class SocketManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SocketProvider provider = new SocketProvider(getSession().getApplicationCode());
        provider.selectField(String.class, "socketId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("fullName", this), "fullName", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("sessionId", this), "sessionId", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("resourceName", this), "resourceName", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("pageKey", this), "pageKey", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Message"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", SocketManagementPage.class);
        add(refreshLink);
    }

    @Override
    public String getPageHeader() {
        return "Socket Management";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Message".equals(link)) {
            String socketId = (String) object.get("socketId");
            PageParameters parameters = new PageParameters();
            parameters.add("socketId", socketId);
            setResponsePage(PushMessagePage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return true;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return true;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Message".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}

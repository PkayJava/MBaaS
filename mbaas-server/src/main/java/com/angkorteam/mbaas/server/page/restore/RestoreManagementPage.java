package com.angkorteam.mbaas.server.page.restore;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.function.RestoreFunction;
import com.angkorteam.mbaas.server.page.role.RoleManagementPage;
import com.angkorteam.mbaas.server.provider.RestoreProvider;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 7/11/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/restore/management")
public class RestoreManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    public RestoreManagementPage() {
    }

    @Override
    public String getPageHeader() {
        return "Restore Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        RestoreProvider provider = new RestoreProvider(getSession().getApplicationCode());

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("restoreId", this), "restoreId", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("tableName", this), "tableName", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Restore"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 16);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", RoleManagementPage.class);
        add(refreshLink);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> model) {
        return "btn-xs btn-danger";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> model) {
        if ("Restore".equals(link)) {
            String restoreId = (String) model.get("restoreId");
            Application application = ApplicationUtils.getApplication();
            RestoreFunction.restore(application.getGson(), getApplicationJdbcTemplate(), restoreId);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> model) {
        return true;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> model) {
        return true;
    }
}

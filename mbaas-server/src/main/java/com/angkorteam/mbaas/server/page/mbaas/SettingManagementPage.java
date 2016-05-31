package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SettingTable;
import com.angkorteam.mbaas.server.provider.SettingProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
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
@AuthorizeInstantiation("mbaas.system")
@Mount("/mbaas/setting/management")
public class SettingManagementPage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Setting Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SettingProvider provider = new SettingProvider();
        provider.selectField(Boolean.class, "system");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("key", this), "key", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("value", this), "value", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", SettingManagementPage.class, getPageParameters());
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
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            String key = (String) object.get("key");
            PageParameters parameters = new PageParameters();
            parameters.add("settingId", key);
            setResponsePage(SettingModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            String key = (String) object.get("key");
            DSLContext context = getDSLContext();
            SettingTable settingTable = Tables.SETTING.as("settingTable");
            context.delete(settingTable).where(settingTable.SETTING_ID.eq(key)).execute();
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
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
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        return false;
    }
}

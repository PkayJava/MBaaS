package com.angkorteam.mbaas.server.page.menu;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.job.JobManagementPage;
import com.angkorteam.mbaas.server.provider.MenuProvider;
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
 * Created by socheat on 5/29/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/menu/management")
public class MenuManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Menu Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        MenuProvider provider = new MenuProvider(getSession().getApplicationCode());

        provider.selectField(String.class, "menuId");
        provider.selectField(String.class, "userId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("title", this), "title", this, provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", MenuManagementPage.class);
        add(refreshLink);
    }


    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String menuId = (String) object.get("menuId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("menuId", object.get("menuId"));
            setResponsePage(MenuModifyPage.class, parameters);
        }
        if ("Delete".equals(link)) {
            jdbcTemplate.update("DELETE FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.MENU_ID + " = ?", menuId);
            return;
        }
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
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    private boolean hasAccess(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }
}

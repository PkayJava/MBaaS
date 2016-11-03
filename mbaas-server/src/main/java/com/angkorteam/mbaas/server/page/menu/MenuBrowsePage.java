package com.angkorteam.mbaas.server.page.menu;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage;
import com.angkorteam.mbaas.server.page.layout.LayoutCreatePage;
import com.angkorteam.mbaas.server.page.layout.LayoutModifyPage;
import com.angkorteam.mbaas.server.provider.LayoutProvider;
import com.angkorteam.mbaas.server.provider.MenuProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 10/24/16.
 */
public class MenuBrowsePage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        MenuProvider provider = new MenuProvider();
        provider.selectField(String.class, "menuId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("title"), "title", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("icon"), "icon", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("parent"), "parent", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("section"), "section", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, Model.of("system"), "system", provider));
        columns.add(new ActionFilteredJooqColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Edit"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", MenuBrowsePage.class);
        add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", MenuCreatePage.class);
        add(createLink);
    }

    @Override
    public String onCSSLink(String s, Map<String, Object> map) {
        if ("Edit".equals(s)) {
            return "btn-xs btn-info";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String s, Map<String, Object> map) {
        String menuId = (String) map.get("menuId");
        PageParameters parameters = new PageParameters();
        parameters.add("menuId", menuId);
        setResponsePage(MenuModifyPage.class, parameters);
    }

    @Override
    public boolean isClickableEventLink(String s, Map<String, Object> map) {
        Boolean system = (Boolean) map.get("system");
        if ("Edit".equals(s)) {
            if (system) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String s, Map<String, Object> map) {
        return true;
    }

    @Override
    public String getPageUUID() {
        return MenuBrowsePage.class.getName();
    }

}

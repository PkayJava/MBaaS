package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.query.QueryManagementPage;
import com.angkorteam.mbaas.server.provider.EnumValueProvider;
import com.angkorteam.mbaas.server.wicket.*;
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
 * Created by socheat on 8/5/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/enum/value/management")
public class EnumValueManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String enumId;
    private String format;
    private String type;

    @Override
    public String getPageHeader() {
        return "Enum Value Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.enumId = getPageParameters().get("enumId").toString("");
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> enumRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " = ?", this.enumId);
        this.format = (String) enumRecord.get(Jdbc.Enum.FORMAT);
        this.type = (String) enumRecord.get(Jdbc.Enum.TYPE);

        EnumValueProvider provider = new EnumValueProvider(getSession().getApplicationCode(), this.enumId);
        provider.selectField(String.class, "enumItemId");
        provider.selectField(String.class, "enumId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("type", this), "type", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("value", this), "value", provider));
        if (this.format != null && !"".equals(this.format)) {
            columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("format", this), "format", provider));
        }
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("enumId", this.enumId);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", EnumValueCreatePage.class, parameters);
        add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", EnumValueManagementPage.class, parameters);
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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String enumItemId = (String) object.get("enumItemId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("enumItemId", enumItemId);
            parameters.add("enumId", this.enumId);
            setResponsePage(EnumValueModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            jdbcTemplate.update("DELETE FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ITEM_ID + " = ?", enumItemId);
            return;
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
}

package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.JsonFieldProvider;
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
@Mount("/body/field/management")
public class BodyFieldManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String jsonId;

    @Override
    public String getPageHeader() {
        return "Body Field Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.jsonId = getPageParameters().get("jsonId").toString("");
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> jsonRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", this.jsonId);

        JsonFieldProvider provider = new JsonFieldProvider(getSession().getApplicationCode(), this.jsonId);
        provider.selectField(String.class, "jsonFieldId");
        provider.selectField(String.class, "jsonId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", this, provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", BodyFieldCreatePage.class, parameters);
        add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", BodyFieldManagementPage.class, parameters);
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
        String jsonFieldId = (String) object.get("jsonFieldId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("jsonFieldId", jsonFieldId);
            parameters.add("jsonId", this.jsonId);
            setResponsePage(BodyFieldModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            jdbcTemplate.update("DELETE FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_FIELD_ID + " = ?", jsonFieldId);
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

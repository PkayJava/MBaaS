//package com.angkorteam.mbaas.server.page.rest;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.provider.JsonProvider;
//import com.angkorteam.mbaas.server.wicket.JooqUtils;
//import com.angkorteam.mbaas.server.wicket.MasterPage;
//import com.angkorteam.mbaas.server.wicket.Mount;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
//import org.apache.wicket.markup.html.link.BookmarkablePageLink;
//import org.apache.wicket.request.mapper.parameter.PageParameters;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 8/3/16.
// */
//@AuthorizeInstantiation({"administrator"})
//@Mount("/body/management")
//public class BodyManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {
//
//    @Override
//    public String getPageHeader() {
//        return "Body Management";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//
//        JsonProvider provider = new JsonProvider(getSession().getApplicationCode());
//        provider.selectField(String.class, "jsonId");
//
//        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
//        add(filterForm);
//
//        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
//        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
//        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
//        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Field", "Edit", "Delete"));
//
//        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
//        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
//        filterForm.add(dataTable);
//
//        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", BodyManagementPage.class);
//        add(refreshLink);
//    }
//
//    @Override
//    public String onCSSLink(String link, Map<String, Object> object) {
//        if ("Field".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("Edit".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        if ("Delete".equals(link)) {
//            return "btn-xs btn-danger";
//        }
//        return "";
//    }
//
//    @Override
//    public void onClickEventLink(String link, Map<String, Object> object) {
//        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
//        String jsonId = (String) object.get("jsonId");
//        if ("Edit".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("jsonId", jsonId);
//            setResponsePage(BodyModifyPage.class, parameters);
//            return;
//        }
//        if ("Field".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("jsonId", jsonId);
//            setResponsePage(BodyFieldManagementPage.class, parameters);
//            return;
//        }
//        if ("Delete".equals(link)) {
//            jdbcTemplate.update("DELETE FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", jsonId);
//            jdbcTemplate.update("DELETE FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
//            return;
//        }
//    }
//
//    @Override
//    public boolean isClickableEventLink(String link, Map<String, Object> object) {
//        return isAccess(link, object);
//    }
//
//    protected boolean isAccess(String link, Map<String, Object> object) {
//        return true;
//    }
//
//    @Override
//    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
//        return isAccess(link, object);
//    }
//}

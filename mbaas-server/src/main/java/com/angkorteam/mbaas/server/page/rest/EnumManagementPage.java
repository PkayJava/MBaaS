//package com.angkorteam.mbaas.server.page.rest;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.provider.EnumProvider;
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
//@Mount("/enum/management")
//public class EnumManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {
//
//    @Override
//    public String getPageHeader() {
//        return "Enum Management";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        EnumProvider provider = new EnumProvider(getSession().getApplicationCode());
//        provider.selectField(String.class, "enumId");
//
//        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
//        add(filterForm);
//
//        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
//        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
//        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", provider));
//        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("type", this), "type", provider));
//        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Value", "Edit", "Delete"));
//
//        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
//        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
//        filterForm.add(dataTable);
//
//        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", EnumManagementPage.class);
//        add(refreshLink);
//    }
//
//    @Override
//    public String onCSSLink(String link, Map<String, Object> object) {
//        if ("Value".equals(link)) {
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
//        String enumId = (String) object.get("enumId");
//        if ("Edit".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("enumId", enumId);
//            setResponsePage(EnumModifyPage.class, parameters);
//            return;
//        }
//        if ("Value".equals(link)) {
//            PageParameters parameters = new PageParameters();
//            parameters.add("enumId", enumId);
//            setResponsePage(EnumValueManagementPage.class, parameters);
//            return;
//        }
//        if ("Delete".equals(link)) {
//            jdbcTemplate.update("DELETE FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " = ?", enumId);
//            jdbcTemplate.update("DELETE FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ID + " = ?", enumId);
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

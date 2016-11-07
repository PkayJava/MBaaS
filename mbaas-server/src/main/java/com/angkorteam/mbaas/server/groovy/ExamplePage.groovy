//package com.angkorteam.mbaas.server.groovy
//
//import com.angkorteam.framework.extension.share.provider.IDataSourceProvider
//import com.angkorteam.framework.extension.share.provider.JdbcProvider
//import com.angkorteam.framework.extension.spring.FromBuilder
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJdbcColumn
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button
//import com.angkorteam.framework.extension.wicket.markup.html.form.Form
//import com.angkorteam.mbaas.server.Spring
//import com.angkorteam.mbaas.server.page.CmsPage
//import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm
//import org.apache.wicket.markup.html.border.Border
//import org.apache.wicket.model.Model
//
//class ExamplePage extends CmsPage implements ActionFilteredJooqColumn.Event {
//
//    @Override
//    protected void doInitialize(Border layout) {
//        add(layout)
//        def fromBuilder = new FromBuilder("attribute a")
//        def provider = new JdbcProvider(Spring.getBean(IDataSourceProvider.class), fromBuilder.toSQL())
//        provider.boardField("a.attribute_id", "unique", String.class);
//        provider.boardField("a.name", "name", String.class);
//
//        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
//        layout.add(filterForm);
//
//        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
//
//        columns.add(new TextFilteredJdbcColumn(String.class, Model.of("unique"), "unique", this, provider));
//        columns.add(new TextFilteredJdbcColumn(String.class, Model.of("name"), "name", this, provider));
//        columns.add(new ActionFilteredJooqColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Edit"));
//
//        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
//        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
//        filterForm.add(dataTable);
//
////        def form = new Form("form");
////        layout.add(from);
////
////        def button = new Button("button");
////        from.add(button);
//    }
//
//    @Override
//    public final String getPageUUID() {
//        // DO NOT MODIFIED
//        return "303b713a-003d-4de9-b511-c083895a6742"
//    }
//
//    @Override
//    String onCSSLink(String link, Map<String, Object> object) {
//        if ("Edit".equals(link)) {
//            return "btn-xs btn-info";
//        }
//        return ""
//    }
//
//    @Override
//    void onClickEventLink(String link, Map<String, Object> object) {
//
//    }
//
//    @Override
//    boolean isClickableEventLink(String link, Map<String, Object> object) {
//        if ("Edit".equals(link)) {
//            return true;
//        }
//        return false
//    }
//
//    @Override
//    boolean isVisibleEventLink(String link, Map<String, Object> object) {
//        return true
//    }
//}
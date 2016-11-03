//package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.TextLinkPanel;
//import org.apache.wicket.Component;
//import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
//import org.apache.wicket.markup.repeater.Item;
//import org.apache.wicket.model.IModel;
//
//import java.util.Map;
//
///**
// * Created by socheat on 6/11/16.
// */
//public class NashornTextLinkColumn extends TextFilteredPropertyColumn<Map<String, Object>, Map<String, String>, String> {
//
//    private String tableId;
//
//    private String script;
//
//    private Factory factory;
//
//    private Map<String, Object> pageModel;
//
//    private Disk disk;
//
//    public NashornTextLinkColumn(IModel<String> headerModel, String columnName, String tableId) {
//        super(headerModel, columnName, columnName);
//        this.tableId = tableId;
//    }
//
//    @Override
//    public Component getFilter(String componentId, FilterForm<?> form) {
//        IModel<Map<String, String>> filterModel = this.getFilterModel(form);
//        TextFilter<Map<String, String>> filter = new TextFilter<>(componentId, filterModel, form);
//        return filter;
//    }
//
//    @Override
//    public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> itemModel) {
//        TextLinkPanel object = new TextLinkPanel(componentId, this.tableId, getPropertyExpression(), itemModel.getObject());
//        object.setDisk(this.disk);
//        object.setPageModel(this.pageModel);
//        object.setScript(this.script);
//        object.setFactory(this.factory);
//        cellItem.add(object);
//    }
//
//    public Factory getFactory() {
//        return factory;
//    }
//
//    public void setFactory(Factory factory) {
//        this.factory = factory;
//    }
//
//    public String getScript() {
//        return script;
//    }
//
//    public void setScript(String script) {
//        this.script = script;
//    }
//
//    public Disk getDisk() {
//        return disk;
//    }
//
//    public void setDisk(Disk disk) {
//        this.disk = disk;
//    }
//
//    public Map<String, Object> getPageModel() {
//        return pageModel;
//    }
//
//    public void setPageModel(Map<String, Object> pageModel) {
//        this.pageModel = pageModel;
//    }
//}

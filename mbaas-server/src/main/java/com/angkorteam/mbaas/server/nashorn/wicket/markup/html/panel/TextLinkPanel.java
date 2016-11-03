//package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel;
//
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLinkColumn;
//import org.apache.wicket.MarkupContainer;
//import org.apache.wicket.markup.IMarkupResourceStreamProvider;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.model.PropertyModel;
//import org.apache.wicket.util.resource.IResourceStream;
//import org.apache.wicket.util.resource.StringResourceStream;
//
//import java.util.Map;
//
///**
// * Created by socheat on 6/18/16.
// */
//public class TextLinkPanel extends Panel implements IMarkupResourceStreamProvider {
//
//    private Map<String, Object> itemModel;
//
//    private Map<String, Object> pageModel;
//
//    private String columnName;
//
//    private NashornLinkColumn link;
//
//    private String tableId;
//
//    private Label text;
//
//    private String script;
//
//    private Factory factory;
//
//    private Disk disk;
//
//    public TextLinkPanel(String id, String tableId, String columnName, Map<String, Object> itemModel) {
//        super(id);
//        this.tableId = tableId;
//        this.columnName = columnName;
//        this.itemModel = itemModel;
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//        this.link = new NashornLinkColumn("link", this.tableId + "_" + this.columnName, this.pageModel, this.itemModel);
//        this.link.setScript(this.script);
//        this.link.setFactory(this.factory);
//        this.link.setDisk(this.disk);
//        add(this.link);
//        this.text = new Label("text", new PropertyModel<>(this.itemModel, this.columnName));
//        this.link.add(this.text);
//    }
//
//    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
//        return new StringResourceStream("<wicket:panel><a wicket:id='link'><wicket:container wicket:id='text'/></a></wicket:panel>");
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
//    public Factory getFactory() {
//        return factory;
//    }
//
//    public void setFactory(Factory factory) {
//        this.factory = factory;
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

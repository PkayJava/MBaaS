//package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLink;
//import org.apache.wicket.AttributeModifier;
//import org.apache.wicket.MarkupContainer;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
//import org.apache.wicket.markup.IMarkupResourceStreamProvider;
//import org.apache.wicket.markup.html.WebMarkupContainer;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.repeater.RepeatingView;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.util.resource.IResourceStream;
//import org.apache.wicket.util.resource.StringResourceStream;
//
//import java.util.Map;
//
///**
// * Created by socheat on 6/27/16.
// */
//public class NashornGoAndClearFilter extends GoAndClearFilter implements IMarkupResourceStreamProvider {
//
//    private Map<String, String> actions;
//
//    private String script;
//
//    private Factory factory;
//
//    private Disk disk;
//
//    private String tableId;
//
//    private String columnName;
//
//    private Map<String, Object> pageModel;
//
//    public NashornGoAndClearFilter(String id, String tableId, String columnName, FilterForm<?> form, IModel<String> goModel, IModel<String> clearModel, Map<String, String> actions, Map<String, Object> pageModel) {
//        super(id, form, goModel, clearModel);
//        this.tableId = tableId;
//        this.columnName = columnName;
//        this.pageModel = pageModel;
//        this.actions = actions;
//    }
//
//    @Override
//    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
//        return new StringResourceStream("<wicket:panel xmlns:wicket='http://wicket.apache.org'><input type='submit' value='go' wicket:id='go' class='btn btn-default btn-sm'/> <input type='submit' value='clear' wicket:id='clear' class='btn btn-default btn-sm'/> <wicket:container wicket:id='links'><a wicket:id='link'><wicket:container wicket:id='text'/></a> </wicket:container></wicket:panel>");
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//        RepeatingView links = new RepeatingView("links");
//        add(links);
//        for (Map.Entry<String, String> action : this.actions.entrySet()) {
//            WebMarkupContainer container = new WebMarkupContainer(links.newChildId());
//            links.add(container);
//            NashornLink link = new NashornLink("link", this.tableId + "_head_" + this.columnName + "_" + action.getKey());
//            link.setPageModel(this.pageModel);
//            link.setScript(this.script);
//            link.setFactory(this.factory);
//            link.setDisk(this.disk);
//            container.add(link);
//            link.add(AttributeModifier.replace("class", action.getValue()));
//            Label text = new Label("text", action.getKey());
//            link.add(text);
//        }
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
//}

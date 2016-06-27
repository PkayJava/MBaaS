package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel;

import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLinkColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import java.util.Map;

/**
 * Created by socheat on 6/22/16.
 */
public class ActionPanel extends Panel implements IMarkupResourceStreamProvider {

    private Map<String, Object> pageModel;

    private IModel<Map<String, Object>> itemModel;

    private Map<String, String> action;

    private String columnName;

    private String tableId;

    private String script;

    private Factory factory;

    private Disk disk;

    public ActionPanel(String id, String tableId, String columnName, Map<String, String> action, Map<String, Object> pageModel, IModel<Map<String, Object>> itemModel) {
        super(id);
        this.tableId = tableId;
        this.action = action;
        this.columnName = columnName;
        this.itemModel = itemModel;
        this.pageModel = pageModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        RepeatingView links = new RepeatingView("links");
        add(links);
        for (Map.Entry<String, String> action : this.action.entrySet()) {
            WebMarkupContainer container = new WebMarkupContainer(links.newChildId());
            links.add(container);
            NashornLinkColumn link = new NashornLinkColumn("link", this.tableId + "_" + this.columnName + "_" + action.getKey(), this.pageModel, this.itemModel);
            link.setScript(this.script);
            link.setFactory(this.factory);
            link.setDisk(this.disk);
            container.add(link);
            link.add(AttributeModifier.replace("class", action.getValue()));
            Label text = new Label("text", action.getKey());
            link.add(text);
        }
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream("<wicket:panel><wicket:container wicket:id='links'><a wicket:id='link'><wicket:container wicket:id='text'/></a> </wicket:container></wicket:panel>");
    }
}

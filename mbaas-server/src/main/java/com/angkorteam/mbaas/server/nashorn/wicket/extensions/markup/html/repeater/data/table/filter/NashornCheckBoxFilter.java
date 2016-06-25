package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter;

import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornButton;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import java.util.Map;

/**
 * Created by socheatkhauv on 6/25/16.
 */
public class NashornCheckBoxFilter extends Panel implements IMarkupResourceStreamProvider {

    private String tableId;

    private String columnName;

    private String script;

    private Disk disk;

    private Factory factory;

    private Map<String, Object> checks;

    private Map<String, String> actions;

    public NashornCheckBoxFilter(String id, String tableId, String columnName, Map<String, Object> checks, Map<String, String> actions) {
        super(id);
        this.columnName = columnName;
        this.checks = checks;
        this.tableId = tableId;
        this.actions = actions;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        RepeatingView buttons = new RepeatingView("buttons");
        add(buttons);
        for (Map.Entry<String, String> action : this.actions.entrySet()) {
            WebMarkupContainer container = new WebMarkupContainer(buttons.newChildId());
            buttons.add(container);
            NashornButton button = new NashornButton("button", this.tableId + "_" + this.columnName + "_" + action.getKey());
            button.setPageModel(this.checks);
            button.setScript(this.script);
            button.setFactory(this.factory);
            button.setDefaultFormProcessing(true);
            button.setDisk(this.disk);
            container.add(button);
            button.add(AttributeModifier.replace("class", action.getValue()));
            Label buttonLabel = new Label("buttonLabel", action.getKey());
            button.add(buttonLabel);
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer markupContainer, Class<?> aClass) {
        return new StringResourceStream("<wicket:panel><wicket:container wicket:id='buttons'><button type='submit' wicket:id='button'><wicket:container wicket:id='buttonLabel'/> </button></wicket:container></wicket:panel>");
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

}

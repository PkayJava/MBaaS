package com.angkorteam.mbaas.server.template;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class BooleanPanel extends Panel {

    private final String name;

    private final Map<String, Object> fields;

    public BooleanPanel(String id, String name, Map<String, Object> modelObject) {
        super(id);
        this.name = name;
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", this.name);
        this.add(label);
        Label inlineLabel = new Label("inlineLabel", this.name);
        this.add(inlineLabel);
        CheckBox field = new CheckBox("field", new PropertyModel<>(this.fields, this.name));
        field.setLabel(Model.of(this.name));
        this.add(field);
    }
}

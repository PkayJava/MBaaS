package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class DoublePanel extends Panel {

    private final String name;

    private final Map<String, Object> fields;

    public DoublePanel(String id, String name, Map<String, Object> modelObject) {
        super(id);
        this.name = name;
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", this.name);
        this.add(label);
        TextField<Double> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
        field.add(RangeValidator.range(Double.MIN_VALUE, Double.MAX_VALUE));
        field.setType(Double.class);
        field.setLabel(Model.of(name));
        TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
        this.add(field);
        this.add(feedback);
    }
}

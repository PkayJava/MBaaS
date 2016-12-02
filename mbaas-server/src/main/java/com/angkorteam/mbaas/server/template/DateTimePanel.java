package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class DateTimePanel extends Panel {
    private final String name;

    private final Map<String, Object> fields;

    public DateTimePanel(String id, String name, Map<String, Object> modelObject) {
        super(id);
        this.name = name;
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", this.name);
        this.add(label);
        DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, this.name), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern());
        field.setType(Date.class);
        this.fields.put(this.name, new Date());
        field.setLabel(Model.of(name));
        TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
        this.add(field);
        this.add(feedback);
    }
}

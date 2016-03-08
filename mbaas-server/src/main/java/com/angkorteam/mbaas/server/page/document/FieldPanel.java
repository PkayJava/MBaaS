package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class FieldPanel extends Panel {

    private AttributePojo attribute;

    private Map<String, Object> fields;

    public FieldPanel(String id, AttributePojo attribute, Map<String, Object> modelObject) {
        super(id);
        this.attribute = attribute;
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (attribute.getJavaType().equals(String.class.getName())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<String> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(String.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), (Page) getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(Integer.class.getName())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Integer> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Integer.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), (Page) getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(Date.class.getName())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, attribute.getName()), "yyyy-MM-dd");
            field.setType(Date.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), (Page) getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        }
    }
}

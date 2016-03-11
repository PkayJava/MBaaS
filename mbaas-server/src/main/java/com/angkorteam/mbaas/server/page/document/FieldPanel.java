package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
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
        if (attribute.getJavaType().equals(TypeEnum.Boolean.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Boolean> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Boolean.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Byte.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Byte> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Byte.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Short.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Short> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Short.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Integer.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Integer> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Integer.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Long.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Long.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Float.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Float.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Double.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Double.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Character.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<Character> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(Character.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.String.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            TextField<String> field = new TextField<>("field", new PropertyModel<>(this.fields, attribute.getName()));
            field.setType(String.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Time.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, attribute.getName()), "hh:mm:ss");
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.Date.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, attribute.getName()), "yyyy-MM-dd");
            field.setType(Date.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (attribute.getJavaType().equals(TypeEnum.DateTime.getLiteral())) {
            Label label = new Label("label", attribute.getName());
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, attribute.getName()), "yyyy-MM-dd hh:mm:ss");
            field.setType(Date.class);
            field.setLabel(JooqUtils.lookup(attribute.getName(), getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        }
    }
}

package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryParameterPojo;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
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
public class TextFieldPanel extends Panel {

    private String type;

    private String name;

    private Map<String, Object> fields;

    public TextFieldPanel(String id, AttributePojo attribute, Map<String, Object> modelObject) {
        super(id);
        this.type = attribute.getJavaType();
        this.name = attribute.getName();
        this.fields = modelObject;
    }

    public TextFieldPanel(String id, QueryParameterPojo queryParameter, Map<String, Object> modelObject) {
        super(id);
        this.type = queryParameter.getType();
        this.name = queryParameter.getName();
        this.fields = modelObject;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (this.type.equals(AttributeTypeEnum.Boolean.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Boolean> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Boolean.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Byte.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Byte> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Byte.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Short.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Short> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Short.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Integer.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Integer> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Integer.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Long.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Long.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Float.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Float.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Double.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Long> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Double.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Character.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<Character> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(Character.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.String.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            TextField<String> field = new TextField<>("field", new PropertyModel<>(this.fields, this.name));
            field.setType(String.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Time.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, this.name), "hh:mm:ss");
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.Date.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, this.name), "yyyy-MM-dd");
            field.setType(Date.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        } else if (this.type.equals(AttributeTypeEnum.DateTime.getLiteral())) {
            Label label = new Label("label", this.name);
            this.add(label);
            DateTextField field = DateTextField.forDatePattern("field", new PropertyModel<>(this.fields, this.name), "yyyy-MM-dd hh:mm:ss");
            field.setType(Date.class);
            field.setLabel(JooqUtils.lookup(this.name, getPage()));
            TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
            this.add(field);
            this.add(feedback);
        }
    }
}

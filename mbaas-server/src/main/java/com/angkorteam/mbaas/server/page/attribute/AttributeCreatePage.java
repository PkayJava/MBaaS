package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeCreatePage extends Page {

    private String collectionId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String javaType;
    private TextField<String> javaTypeField;
    private TextFeedbackPanel javaTypeFeedback;

    private String nullable;
    private DropDownChoice<String> nullableField;
    private TextFeedbackPanel nullableFeedback;

    private String exposed;
    private DropDownChoice<String> exposedField;
    private TextFeedbackPanel exposedFeedback;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.javaTypeField = new TextField<>("javaTypeField", new PropertyModel<>(this, "javaType"));
        this.form.add(this.javaTypeField);
        this.javaTypeFeedback = new TextFeedbackPanel("javaTypeFeedback", this.javaTypeField);
        this.form.add(javaTypeFeedback);

        this.nullableField = new DropDownChoice<>("nullableField", new PropertyModel<>(this, "nullable"), Arrays.asList("Yes", "No"));
        this.form.add(this.nullableField);
        this.nullableFeedback = new TextFeedbackPanel("nullableFeedback", this.nullableField);
        this.form.add(this.nullableFeedback);

        this.exposedField = new DropDownChoice<>("exposedField", new PropertyModel<>(this, "exposed"));
        this.form.add(this.exposedField);
        this.exposedFeedback = new TextFeedbackPanel("exposedFeedback", this.exposedField);
        this.form.add(this.exposedFeedback);


    }
}

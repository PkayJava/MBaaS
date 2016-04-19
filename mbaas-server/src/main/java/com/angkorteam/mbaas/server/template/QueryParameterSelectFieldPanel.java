package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryParameterPojo;
import com.angkorteam.mbaas.server.validator.SubTypeValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class QueryParameterSelectFieldPanel extends Panel {

    private String name;

    private Map<String, String> fields;

    private List<String> types;

    private List<String> subTypes;

    private Form<Void> form;

    public QueryParameterSelectFieldPanel(String id, Form<Void> form, QueryParameterPojo queryParameter, List<String> types, List<String> subTypes, Map<String, String> modelObject) {
        super(id);
        this.form = form;
        this.name = queryParameter.getName();
        this.fields = modelObject;
        this.types = types;
        this.subTypes = subTypes;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label typeReturnLabel = new Label("typeReturnLabel", this.name);
        this.add(typeReturnLabel);
        DropDownChoice<String> typeReturnField = new DropDownChoice<>("typeReturnField", new PropertyModel<>(this.fields, this.name), this.types);
        typeReturnField.setRequired(true);
        typeReturnField.setLabel(JooqUtils.lookup(this.name, getPage()));
        this.add(typeReturnField);
        TextFeedbackPanel typeReturnFeedback = new TextFeedbackPanel("typeReturnFeedback", typeReturnField);
        this.add(typeReturnFeedback);

        Label subTypeReturnLabel = new Label("subTypeReturnLabel", this.name + "'s sub type");
        this.add(subTypeReturnLabel);
        DropDownChoice<String> subTypeReturnField = new DropDownChoice<>("subTypeReturnField", new PropertyModel<>(this.fields, this.name + "SubType"), this.subTypes);
        subTypeReturnField.setLabel(JooqUtils.lookup(this.name + "SubType", getPage()));
        this.add(subTypeReturnField);
        TextFeedbackPanel subTypeReturnFeedback = new TextFeedbackPanel("subTypeReturnFeedback", subTypeReturnField);
        this.add(subTypeReturnFeedback);

        this.form.add(new SubTypeValidator(typeReturnField, subTypeReturnField));
    }
}

package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryParameterPojo;
import com.angkorteam.mbaas.server.validator.QueryParameterSubTypeValidator;
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
        Label labelType = new Label("labelType", this.name);
        this.add(labelType);
        DropDownChoice<String> fieldType = new DropDownChoice<>("fieldType", new PropertyModel<>(this.fields, this.name), this.types);
        fieldType.setRequired(true);
        fieldType.setLabel(JooqUtils.lookup(this.name, getPage()));
        TextFeedbackPanel feedbackType = new TextFeedbackPanel("feedbackType", fieldType);
        this.add(fieldType);
        this.add(feedbackType);

        Label labelSubType = new Label("labelSubType", this.name + "'s sub type");
        this.add(labelSubType);
        DropDownChoice<String> fieldSubType = new DropDownChoice<>("fieldSubType", new PropertyModel<>(this.fields, this.name + "SubType"), this.subTypes);
        fieldSubType.setLabel(JooqUtils.lookup(this.name + "SubType", getPage()));
        TextFeedbackPanel feedbackSubType = new TextFeedbackPanel("feedbackSubType", fieldSubType);
        this.add(fieldSubType);
        this.add(feedbackSubType);

        this.form.add(new QueryParameterSubTypeValidator(fieldType, fieldSubType));
    }
}

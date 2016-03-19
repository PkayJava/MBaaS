package com.angkorteam.mbaas.server.template;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryParameterPojo;
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
public class SelectFieldPanel extends Panel {

    private String type;

    private String name;

    private Map<String, String> fields;

    private List<String> types;

    public SelectFieldPanel(String id, QueryParameterPojo queryParameter, List<String> types, Map<String, String> modelObject) {
        super(id);
        this.type = queryParameter.getType();
        this.name = queryParameter.getName();
        this.fields = modelObject;
        this.types = types;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", this.name);
        this.add(label);
        DropDownChoice<String> field = new DropDownChoice<>("field", new PropertyModel<>(this.fields, this.name), this.types);
        field.setRequired(true);
        field.setLabel(JooqUtils.lookup(this.name, getPage()));
        TextFeedbackPanel feedback = new TextFeedbackPanel("feedback", field);
        this.add(field);
        this.add(feedback);
    }
}

package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.EnumNameValidator;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/enum/create")
public class EnumCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String type;
    private DropDownChoice<String> typeField;
    private TextFeedbackPanel typeFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Enum";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        this.add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new EnumNameValidator(getSession().getApplicationCode()));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        List<String> types = Arrays.asList(TypeEnum.Boolean.getLiteral(), TypeEnum.Long.getLiteral(), TypeEnum.Double.getLiteral(), TypeEnum.String.getLiteral(), TypeEnum.Time.getLiteral(), TypeEnum.Date.getLiteral(), TypeEnum.DateTime.getLiteral(), TypeEnum.Character.getLiteral());
        this.typeField = new DropDownChoice<>("typeField", new PropertyModel<>(this, "type"), types);
        this.typeField.setRequired(true);
        this.form.add(this.typeField);
        this.typeFeedback = new TextFeedbackPanel("typeFeedback", this.typeField);
        this.form.add(this.typeFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Session session = getSession();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.ENUM);
        jdbcInsert.usingColumns(Jdbc.Enum.NAME, Jdbc.Enum.TYPE, Jdbc.Enum.FORMAT, Jdbc.Enum.DESCRIPTION, Jdbc.Enum.ENUM_ID);
        Map<String, Object> fields = new HashMap<>();
        String format = "";
        if (TypeEnum.Time.getLiteral().equals(this.type)) {
            format = DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern();
        } else if (TypeEnum.Date.getLiteral().equals(this.type)) {
            format = DateFormatUtils.ISO_DATE_FORMAT.getPattern();
        } else if (TypeEnum.DateTime.getLiteral().equals(this.type)) {
            format = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
        }
        fields.put(Jdbc.Enum.ENUM_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.Enum.DESCRIPTION, this.description);
        fields.put(Jdbc.Enum.FORMAT, format);
        fields.put(Jdbc.Enum.TYPE, this.type);
        fields.put(Jdbc.Enum.NAME, this.name);
        jdbcInsert.execute(fields);
        setResponsePage(EnumManagementPage.class);
    }

}

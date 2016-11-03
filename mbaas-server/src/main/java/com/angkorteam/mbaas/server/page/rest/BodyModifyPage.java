//package com.angkorteam.mbaas.server.page.rest;
//
//import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
//import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.validator.JsonNameValidator;
//import com.angkorteam.mbaas.server.wicket.*;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.markup.html.form.DropDownChoice;
//import org.apache.wicket.markup.html.form.TextField;
//import org.apache.wicket.model.PropertyModel;
//import org.springframework.http.MediaType;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 8/3/16.
// */
//@AuthorizeInstantiation({"administrator"})
//@Mount("/body/modify")
//public class BodyModifyPage extends MasterPage {
//
//    private String jsonId;
//
//    private String name;
//    private TextField<String> nameField;
//    private TextFeedbackPanel nameFeedback;
//
//    private List<String> contentTypes;
//    private String contentType;
//    private DropDownChoice<String> contentTypeField;
//    private TextFeedbackPanel contentTypeFeedback;
//
//    private String description;
//    private TextField<String> descriptionField;
//    private TextFeedbackPanel descriptionFeedback;
//
//    private Form<Void> form;
//    private Button saveButton;
//
//    @Override
//    public String getPageHeader() {
//        return "Modify Body";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        this.jsonId = getPageParameters().get("jsonId").toString("");
//
//        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
//        Map<String, Object> jsonRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", this.jsonId);
//
//        this.form = new Form<>("form");
//        this.add(this.form);
//
//        this.name = (String) jsonRecord.get(Jdbc.Json.NAME);
//        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
//        this.nameField.setRequired(true);
//        this.nameField.add(new JsonNameValidator(getSession().getApplicationCode(), this.jsonId));
//        this.form.add(this.nameField);
//        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
//        this.form.add(this.nameFeedback);
//
//        this.contentTypes = new ArrayList<>();
//        this.contentTypes.add(MediaType.APPLICATION_JSON_VALUE);
//        this.contentTypes.add(MediaType.MULTIPART_FORM_DATA_VALUE);
//        this.contentTypes.add(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//        this.contentType = (String) jsonRecord.get(Jdbc.Json.CONTENT_TYPE);
//        this.contentTypeField = new DropDownChoice<>("contentTypeField", new PropertyModel<>(this, "contentType"), new PropertyModel<>(this, "contentTypes"));
//        this.contentTypeField.setEnabled(false);
//        this.contentTypeField.setRequired(true);
//        this.form.add(this.contentTypeField);
//        this.contentTypeFeedback = new TextFeedbackPanel("contentTypeFeedback", this.contentTypeField);
//        this.form.add(this.contentTypeFeedback);
//
//        this.description = (String) jsonRecord.get(Jdbc.Json.DESCRIPTION);
//        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
//        this.descriptionField.setRequired(true);
//        this.form.add(this.descriptionField);
//        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
//        this.form.add(this.descriptionFeedback);
//
//        this.saveButton = new Button("saveButton");
//        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
//        this.form.add(this.saveButton);
//    }
//
//    private void saveButtonOnSubmit(Button button) {
//        Session session = getSession();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
//        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
//        jdbcUpdate.withTableName(Jdbc.JSON);
//        Map<String, Object> wheres = new HashMap<>();
//        wheres.put(Jdbc.Json.JSON_ID, this.jsonId);
//        Map<String, Object> fields = new HashMap<>();
//        fields.put(Jdbc.Json.CONTENT_TYPE, this.contentType);
//        fields.put(Jdbc.Json.NAME, this.name);
//        fields.put(Jdbc.Json.DESCRIPTION, this.description);
//        jdbcUpdate.execute(fields, wheres);
//        setResponsePage(BodyManagementPage.class);
//    }
//}

package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.validator.AttributeNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/attribute/create")
public class AttributeCreatePage extends MasterPage {

    private String collectionId;
    private String collectionName;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String attributeType;
    private DropDownChoice<String> attributeTypeField;
    private TextFeedbackPanel attributeTypeFeedback;

    private String eav;
    private DropDownChoice<String> eavField;
    private TextFeedbackPanel eavFeedback;

    private String nullable;
    private DropDownChoice<String> nullableField;
    private TextFeedbackPanel nullableFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Collection Attribute :: " + this.collectionName;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        this.collectionId = getPageParameters().get("collectionId").toString();

        Map<String, Object> collectionRecord = null;
        collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", this.collectionId);
        this.collectionName = (String) collectionRecord.get(Jdbc.Collection.NAME);

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new AttributeNameValidator(getSession().getApplicationCode(), this.collectionId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        List<String> attributeTypes = new ArrayList<>();
        for (AttributeTypeEnum attributeTypeEnum : AttributeTypeEnum.values()) {
            if (attributeTypeEnum.isExposed()) {
                attributeTypes.add(attributeTypeEnum.getLiteral());
            }
        }
        this.attributeTypeField = new DropDownChoice<>("attributeTypeField", new PropertyModel<>(this, "attributeType"), attributeTypes);
        this.attributeTypeField.setRequired(true);
        this.form.add(this.attributeTypeField);
        this.attributeTypeFeedback = new TextFeedbackPanel("attributeTypeFeedback", this.attributeTypeField);
        this.form.add(attributeTypeFeedback);

        this.nullableField = new DropDownChoice<>("nullableField", new PropertyModel<>(this, "nullable"), Arrays.asList("Yes", "No"));
        this.nullableField.setRequired(true);
        this.form.add(this.nullableField);
        this.nullableFeedback = new TextFeedbackPanel("nullableFeedback", this.nullableField);
        this.form.add(this.nullableFeedback);

        this.eavField = new DropDownChoice<>("eavField", new PropertyModel<>(this, "eav"), Arrays.asList("Yes", "No"));
        this.eavField.setRequired(true);
        this.form.add(this.eavField);
        this.eavFeedback = new TextFeedbackPanel("eavFeedback", this.eavField);
        this.form.add(this.eavFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", AttributeManagementPage.class, parameters);
        this.form.add(closeLink);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        CollectionAttributeCreateRequest requestBody = new CollectionAttributeCreateRequest();
        requestBody.setAttributeName(this.name);
        requestBody.setNullable("Yes".equals(this.nullable));
        requestBody.setEav("Yes".equals(this.eav));
        requestBody.setAttributeType(AttributeTypeEnum.valueOf(this.attributeType).getLiteral());
        requestBody.setCollectionName(this.collectionName);

        AttributeFunction.createAttribute(getApplicationSchema(), jdbcTemplate, getSession().getApplicationCode(), UUID.randomUUID().toString(), requestBody);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        setResponsePage(AttributeManagementPage.class, parameters);
    }
}

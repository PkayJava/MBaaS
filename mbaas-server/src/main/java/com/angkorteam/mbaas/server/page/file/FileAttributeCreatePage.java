package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.server.function.FileAttributeFunction;
import com.angkorteam.mbaas.server.validator.AttributeNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/file/attribute/create")
public class FileAttributeCreatePage extends MasterPage {

    private String collectionId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String javaType;
    private DropDownChoice<String> javaTypeField;
    private TextFeedbackPanel javaTypeFeedback;

    private String nullable;
    private DropDownChoice<String> nullableField;
    private TextFeedbackPanel nullableFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New File Attribute ";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.FILE.getName())).fetchOneInto(String.class);

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new AttributeNameValidator(this.collectionId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        List<String> javaTypes = new LinkedList<>();
        for (AttributeTypeEnum attributeTypeEnum : AttributeTypeEnum.values()) {
            if (attributeTypeEnum.isExposed()) {
                javaTypes.add(attributeTypeEnum.getLiteral());
            }
        }
        this.javaTypeField = new DropDownChoice<>("javaTypeField", new PropertyModel<>(this, "javaType"), javaTypes);
        this.javaTypeField.setRequired(true);
        this.form.add(this.javaTypeField);
        this.javaTypeFeedback = new TextFeedbackPanel("javaTypeFeedback", this.javaTypeField);
        this.form.add(javaTypeFeedback);

        List<String> nullables = Arrays.asList("Yes", "No");
        this.nullableField = new DropDownChoice<>("nullableField", new PropertyModel<>(this, "nullable"), nullables);
        this.nullableField.setRequired(true);
        this.form.add(this.nullableField);
        this.nullableFeedback = new TextFeedbackPanel("nullableFeedback", this.nullableField);
        this.form.add(this.nullableFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

        CollectionAttributeCreateRequest requestBody = new CollectionAttributeCreateRequest();
        requestBody.setAttributeName(this.name);
        requestBody.setNullable("Yes".equals(this.nullable));
        requestBody.setJavaType(this.javaType);
        requestBody.setCollectionName(collectionRecord.getName());

        FileAttributeFunction.createAttribute(context, requestBody);

        setResponsePage(FileAttributeManagementPage.class);
    }
}

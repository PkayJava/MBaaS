package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.server.function.UserAttributeFunction;
import com.angkorteam.mbaas.server.validator.AttributeNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/attribute/create")
public class UserAttributeCreatePage extends MasterPage {

    private String collectionId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String attributeType;
    private DropDownChoice<String> attributeTypeField;
    private TextFeedbackPanel attributeTypeFeedback;

    private String nullable;
    private DropDownChoice<String> nullableField;
    private TextFeedbackPanel nullableFeedback;

    private String scope;
    private DropDownChoice<String> scopeField;
    private TextFeedbackPanel scopeFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New User Attribute ";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(String.class);

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new AttributeNameValidator(this.collectionId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        List<String> attributeTypes = new LinkedList<>();
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

        List<String> nullables = Arrays.asList("Yes", "No");
        this.nullableField = new DropDownChoice<>("nullableField", new PropertyModel<>(this, "nullable"), nullables);
        this.nullableField.setRequired(true);
        this.form.add(this.nullableField);
        this.nullableFeedback = new TextFeedbackPanel("nullableFeedback", this.nullableField);
        this.form.add(this.nullableFeedback);

        List<String> scopes = Arrays.asList(
                ScopeEnum.VisibleByAnonymousUser.getLiteral(),
                ScopeEnum.VisibleByFriend.getLiteral(),
                ScopeEnum.VisibleByRegisteredUser.getLiteral(),
                ScopeEnum.VisibleByTheUser.getLiteral()
        );
        this.scopeField = new DropDownChoice<>("scopeField", new PropertyModel<>(this, "scope"), scopes);
        this.scopeField.setRequired(true);
        this.form.add(this.scopeField);
        this.scopeFeedback = new TextFeedbackPanel("scopeFeedback", this.scopeField);
        this.form.add(this.scopeFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

        CollectionAttributeCreateRequest requestBody = new CollectionAttributeCreateRequest();
        requestBody.setAttributeName(this.name);
        requestBody.setNullable("Yes".equals(this.nullable));
        requestBody.setAttributeType(this.attributeType);
        requestBody.setCollectionName(collectionRecord.getName());

        ScopeEnum scope = null;
        for (ScopeEnum temp : ScopeEnum.values()) {
            if (temp.getLiteral().equals(this.scope)) {
                scope = temp;
                break;
            }
        }
        UserAttributeFunction.createAttribute(context, jdbcTemplate, requestBody, getSession().getUserId(), scope);

        setResponsePage(UserAttributeManagementPage.class);
    }
}

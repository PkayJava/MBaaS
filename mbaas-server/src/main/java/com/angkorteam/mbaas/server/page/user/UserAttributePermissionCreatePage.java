package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.UserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserPrivacyRecord;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/9/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/attribute/permission/create")
public class UserAttributePermissionCreatePage extends MasterPage {

    private String attributeId;

    private String name;
    private Label nameField;
    private TextFeedbackPanel nameFeedback;

    private String scope;
    private DropDownChoice<String> scopeField;
    private TextFeedbackPanel scopeFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify User Attribute Scope";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        this.attributeId = getPageParameters().get("attributeId").toString();

        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(attributeTable);

        this.name = attributeRecord.getName();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new Label("nameField", new PropertyModel<>(this, "name"));
        this.form.add(this.nameField);

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

        String uuid = UUID.randomUUID().toString();
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");
        UserPrivacyRecord userPrivacyRecord = context.newRecord(userPrivacyTable);
        userPrivacyRecord.setUserPrivacyId(uuid);
        userPrivacyRecord.setUserId(getSession().getUserId());
        userPrivacyRecord.setAttributeId(this.attributeId);
        userPrivacyRecord.setScope(this.scope);
        userPrivacyRecord.store();

        setResponsePage(UserAttributeManagementPage.class);
    }
}

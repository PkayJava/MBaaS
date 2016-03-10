package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.MariaDBFunction;
import com.angkorteam.mbaas.server.page.document.DocumentManagementPage;
import com.angkorteam.mbaas.server.page.document.FieldPanel;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/modify")
public class UserModifyPage extends MasterPage {

    private String userId;
    private Integer optimistic;
    private CollectionPojo collection;
    private String collectionId;
    private String documentId;

    private String login;
    private Label loginLabel;

    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Map<String, Object> fields;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify User Role";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        PageParameters parameters = getPageParameters();
        this.userId = parameters.get("userId").toString();
        this.documentId = this.userId;

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        UserTable userTable = Tables.USER.as("userTable");

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(CollectionPojo.class);
        this.collectionId = this.collection.getCollectionId();
        this.fields = new HashMap<>();

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributePojo> attributes = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionId))
                .and(attributeTable.JAVA_TYPE.eq(TypeEnum.Boolean.getLiteral())
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Byte.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Short.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Integer.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Long.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Float.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Double.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Character.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.String.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Time.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.Date.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(TypeEnum.DateTime.getLiteral())))
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        Map<String, AttributePojo> virtualAttributes = new HashMap<>();
        for (AttributePojo attribute : context.select(attributeTable.fields()).from(attributeTable).fetchInto(AttributePojo.class)) {
            virtualAttributes.put(attribute.getAttributeId(), attribute);
        }

        List<String> selectFields = new ArrayList<>();

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            FieldPanel fieldPanel = new FieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
            if (attribute.getVirtual()) {
                AttributePojo masterAttribute = virtualAttributes.get(attribute.getVirtualAttributeId());
                String column = MariaDBFunction.columnGet(masterAttribute.getName(), attribute.getName(), attribute.getJavaType(), attribute.getName());
                selectFields.add(column);
            } else {
                selectFields.add(attribute.getName());
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        Map<String, Object> document = getJdbcTemplate().queryForMap("select " + StringUtils.join(selectFields, ", ") + " from `" + collectionRecord.getName() + "` where " + collectionRecord.getName() + "_id = ?", this.userId);
        if (document != null && !document.isEmpty()) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                this.fields.put(entry.getKey(), entry.getValue());
            }
        }

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(userId)).fetchOneInto(userTable);

        this.optimistic = userRecord.getOptimistic();

        this.login = userRecord.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));

        List<RolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(RolePojo.class);
        this.role = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(RolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new RoleChoiceRenderer());
        this.roleField.setRequired(true);
        this.roleField.setLabel(JooqUtils.lookup("role", this));
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(fields);
        this.form.add(this.loginLabel);
        this.form.add(this.roleField);
        this.form.add(this.roleFeedback);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

        DocumentModifyRequest requestBody = new DocumentModifyRequest();
        this.fields.put(Tables.USER.ROLE_ID.getName(), this.role.getRoleId());
        requestBody.setDocument(this.fields);

        DocumentFunction.modifyDocument(getDSLContext(), getJdbcTemplate(), collectionRecord.getName(), this.documentId, requestBody);

        setResponsePage(UserManagementPage.class);
    }

}

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
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.template.TextFieldPanel;
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
import org.springframework.jdbc.core.JdbcTemplate;

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
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        List<String> joins = new ArrayList<>();
        List<String> attributeJoins = new ArrayList<>();
        List<String> names = new ArrayList<>();

        boolean hasEav = false;
        for (AttributePojo attribute : attributes) {
            if (attribute.getEav()) {
                hasEav = true;
                AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attribute.getAttributeType());
                String eavTable = null;
                String eavField = null;
                // eav time
                if (attributeType == AttributeTypeEnum.Time) {
                    eavTable = Tables.EAV_TIME.getName();
                    eavField = Tables.EAV_TIME.getName() + "." + Tables.EAV_TIME.DOCUMENT_ID.getName();
                }
                // eav date
                if (attributeType == AttributeTypeEnum.Date) {
                    eavTable = Tables.EAV_DATE.getName();
                    eavField = Tables.EAV_DATE.getName() + "." + Tables.EAV_DATE.DOCUMENT_ID.getName();
                }
                // eav datetime
                if (attributeType == AttributeTypeEnum.DateTime) {
                    eavTable = Tables.EAV_DATE_TIME.getName();
                    eavField = Tables.EAV_DATE_TIME.getName() + "." + Tables.EAV_DATE_TIME.DOCUMENT_ID.getName();
                }
                // eav varchar
                if (attributeType == AttributeTypeEnum.String) {
                    eavTable = Tables.EAV_VARCHAR.getName();
                    eavField = Tables.EAV_VARCHAR.getName() + "." + Tables.EAV_VARCHAR.DOCUMENT_ID.getName();
                }
                // eav character
                if (attributeType == AttributeTypeEnum.Character) {
                    eavTable = Tables.EAV_CHARACTER.getName();
                    eavField = Tables.EAV_CHARACTER.getName() + "." + Tables.EAV_CHARACTER.DOCUMENT_ID.getName();
                }
                // eav decimal
                if (attributeType == AttributeTypeEnum.Float
                        || attributeType == AttributeTypeEnum.Double) {
                    eavTable = Tables.EAV_DECIMAL.getName();
                    eavField = Tables.EAV_DECIMAL.getName() + "." + Tables.EAV_DECIMAL.DOCUMENT_ID.getName();
                }
                // eav boolean
                if (attributeType == AttributeTypeEnum.Boolean) {
                    eavTable = Tables.EAV_BOOLEAN.getName();
                    eavField = Tables.EAV_BOOLEAN.getName() + "." + Tables.EAV_BOOLEAN.DOCUMENT_ID.getName();
                }
                // eav integer
                if (attributeType == AttributeTypeEnum.Byte
                        || attributeType == AttributeTypeEnum.Short
                        || attributeType == AttributeTypeEnum.Integer
                        || attributeType == AttributeTypeEnum.Long) {
                    eavTable = Tables.EAV_INTEGER.getName();
                    eavField = Tables.EAV_INTEGER.getName() + "." + Tables.EAV_INTEGER.DOCUMENT_ID.getName();
                }
                // eav text
                if (attributeType == AttributeTypeEnum.Text) {
                    eavTable = Tables.EAV_TEXT.getName();
                    eavField = Tables.EAV_TEXT.getName() + "." + Tables.EAV_TEXT.DOCUMENT_ID.getName();
                }
                String join = "LEFT JOIN " + eavTable + " ON " + collection.getName() + "." + collection.getName() + "_id" + " = " + eavField;
                if (!joins.contains(join)) {
                    joins.add(join);
                }

                String attributeJoin = "LEFT JOIN attribute " + eavTable + "_attribute ON " + eavTable + "_attribute.attribute_id = " + eavTable + ".attribute_id";
                if (!attributeJoins.contains(attributeJoin)) {
                    attributeJoins.add(attributeJoin);
                }
                names.add("MAX( IF(" + eavTable + "_attribute.name = '" + attribute.getName() + "', " + eavTable + ".eav_value, NULL) ) AS " + attribute.getName());
            } else {
                names.add(this.collection.getName() + "." + attribute.getName() + " AS " + attribute.getName());
            }
        }

        List<String> selectFields = new ArrayList<>();
        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            TextFieldPanel fieldPanel = new TextFieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
            selectFields.add(attribute.getName());
        }

        String documentIdField = collection.getName() + "." + collection.getName() + "_id";
        if (!selectFields.isEmpty()) {
            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            if (hasEav) {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collection.getName() + " " + StringUtils.join(joins, " ") + " " + StringUtils.join(attributeJoins, " ") + " WHERE " + documentIdField + " = ? GROUP BY " + documentIdField;
                this.fields.putAll(jdbcTemplate.queryForMap(query, documentId));
            } else {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collection.getName() + " WHERE " + documentIdField + " = ?";
                this.fields.putAll(jdbcTemplate.queryForMap(query, documentId));
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

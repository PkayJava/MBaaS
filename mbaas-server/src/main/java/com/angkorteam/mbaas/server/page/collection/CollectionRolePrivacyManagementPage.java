package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionRolePrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRolePrivacyRecord;
import com.angkorteam.mbaas.plain.enums.CollectionPermissionEnum;
import com.angkorteam.mbaas.server.page.client.ClientManagementPage;
import com.angkorteam.mbaas.server.provider.CollectionRolePrivacyProvider;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.*;

/**
 * Created by socheat on 3/20/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/collection/role/privacy/management")
public class CollectionRolePrivacyManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Boolean attribute;
    private DropDownChoice<Boolean> attributeField;
    private TextFeedbackPanel attributeFeedback;

    private Boolean read;
    private DropDownChoice<Boolean> readField;
    private TextFeedbackPanel readFeedback;

    private Boolean drop;
    private DropDownChoice<Boolean> dropField;
    private TextFeedbackPanel dropFeedback;

    private Boolean insert;
    private DropDownChoice<Boolean> insertField;
    private TextFeedbackPanel insertFeedback;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        RoleTable roleTable = Tables.ROLE.as("roleTable");

        this.collectionId = getPageParameters().get("collectionId").toString();

        CollectionRolePrivacyProvider provider = new CollectionRolePrivacyProvider(this.collectionId);
        provider.selectField(String.class, "roleId");
        provider.selectField(String.class, "collectionId");


        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("roleName", this), "roleName", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("attribute", this), "attribute", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("drop", this), "drop", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("insert", this), "insert", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("read", this), "read", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", CollectionManagementPage.class, getPageParameters());
        add(refreshLink);

        Form<Void> form = new Form<>("form");
        add(form);

        Button saveButton = new Button("saveButton");
        form.add(saveButton);
        saveButton.setOnSubmit(this::saveButtonOnSubmit);

        List<RolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(RolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new RoleChoiceRenderer());
        this.roleField.setRequired(true);
        form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        form.add(this.roleFeedback);

        this.dropField = new DropDownChoice<>("dropField", new PropertyModel<>(this, "drop"), Arrays.asList(true, false));
        this.dropField.setRequired(true);
        form.add(this.dropField);
        this.dropFeedback = new TextFeedbackPanel("dropFeedback", this.dropField);
        form.add(this.dropFeedback);

        this.attributeField = new DropDownChoice<>("attributeField", new PropertyModel<>(this, "attribute"), Arrays.asList(true, false));
        this.attributeField.setRequired(true);
        form.add(this.attributeField);
        this.attributeFeedback = new TextFeedbackPanel("attributeFeedback", this.attributeField);
        form.add(this.attributeFeedback);

        this.readField = new DropDownChoice<>("readField", new PropertyModel<>(this, "read"), Arrays.asList(true, false));
        this.readField.setRequired(true);
        form.add(this.readField);
        this.readFeedback = new TextFeedbackPanel("readFeedback", this.readField);
        form.add(this.readFeedback);

        this.insertField = new DropDownChoice<>("insertField", new PropertyModel<>(this, "insert"), Arrays.asList(true, false));
        this.insertField.setRequired(true);
        form.add(this.insertField);
        this.insertFeedback = new TextFeedbackPanel("insertFeedback", this.insertField);
        form.add(this.insertFeedback);

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        DSLContext context = getDSLContext();
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(this.collectionId)).fetchOneInto(collectionTable);
        if (getSession().isBackOffice() && !collectionRecord.getOwnerUserId().equals(getSession().getUserId())) {
            setResponsePage(CollectionManagementPage.class);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
        context.delete(collectionRolePrivacyTable).where(collectionRolePrivacyTable.ROLE_ID.eq(role.getRoleId())).and(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionId)).execute();
        CollectionRolePrivacyRecord collectionRolePrivacyRecord = context.newRecord(collectionRolePrivacyTable);
        collectionRolePrivacyRecord.setCollectionRolePrivacyId(UUID.randomUUID().toString());
        collectionRolePrivacyRecord.setRoleId(this.role.getRoleId());
        collectionRolePrivacyRecord.setCollectionId(this.collectionId);
        Integer permission = 0;
        if (this.attribute) {
            permission = permission | CollectionPermissionEnum.Attribute.getLiteral();
        }
        if (this.read) {
            permission = permission | CollectionPermissionEnum.Read.getLiteral();
        }
        if (this.insert) {
            permission = permission | CollectionPermissionEnum.Insert.getLiteral();
        }
        if (this.drop) {
            permission = permission | CollectionPermissionEnum.Drop.getLiteral();
        }
        collectionRolePrivacyRecord.setPermisson(permission);
        collectionRolePrivacyRecord.store();
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        setResponsePage(CollectionRolePrivacyManagementPage.class, parameters);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String roleId = (String) object.get("roleId");
        String collectionId = (String) object.get("collectionId");
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            context.delete(Tables.COLLECTION_ROLE_PRIVACY).where(Tables.COLLECTION_ROLE_PRIVACY.ROLE_ID.eq(roleId)).and(Tables.COLLECTION_ROLE_PRIVACY.COLLECTION_ID.eq(collectionId)).execute();
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", this.collectionId);
            setResponsePage(CollectionRolePrivacyManagementPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }
}

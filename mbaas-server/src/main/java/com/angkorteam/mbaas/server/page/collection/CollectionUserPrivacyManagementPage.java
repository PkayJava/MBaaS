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
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionUserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionUserPrivacyRecord;
import com.angkorteam.mbaas.plain.enums.CollectionPermissionEnum;
import com.angkorteam.mbaas.server.provider.CollectionUserPrivacyProvider;
import com.angkorteam.mbaas.server.renderer.UserChoiceRenderer;
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
@Mount("/collection/user/privacy/management")
public class CollectionUserPrivacyManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    private UserPojo user;
    private DropDownChoice<UserPojo> userField;
    private TextFeedbackPanel userFeedback;

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
        UserTable userTable = Tables.USER.as("userTable");

        this.collectionId = getPageParameters().get("collectionId").toString();

        CollectionUserPrivacyProvider provider = new CollectionUserPrivacyProvider(this.collectionId);
        provider.selectField(String.class, "userId");
        provider.selectField(String.class, "collectionId");


        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("attribute", this), "attribute", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("drop", this), "drop", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("insert", this), "insert", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("read", this), "read", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", CollectionManagementPage.class, getPageParameters());
        add(refreshLink);

        Form<Void> form = new Form<>("form");
        add(form);

        Button saveButton = new Button("saveButton");
        form.add(saveButton);
        saveButton.setOnSubmit(this::saveButtonOnSubmit);

        List<UserPojo> users = context.select(userTable.fields()).from(userTable).fetchInto(UserPojo.class);
        this.userField = new DropDownChoice<>("userField", new PropertyModel<>(this, "user"), users, new UserChoiceRenderer());
        this.userField.setRequired(true);
        form.add(this.userField);
        this.userFeedback = new TextFeedbackPanel("userFeedback", this.userField);
        form.add(this.userFeedback);

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
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("collectionUserPrivacyTable");
        context.delete(collectionUserPrivacyTable).where(collectionUserPrivacyTable.USER_ID.eq(user.getUserId())).and(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionId)).execute();
        CollectionUserPrivacyRecord collectionUserPrivacyRecord = context.newRecord(collectionUserPrivacyTable);
        collectionUserPrivacyRecord.setCollectionUserPrivacyId(UUID.randomUUID().toString());
        collectionUserPrivacyRecord.setUserId(this.user.getUserId());
        collectionUserPrivacyRecord.setCollectionId(this.collectionId);
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
        collectionUserPrivacyRecord.setPermisson(permission);
        collectionUserPrivacyRecord.store();
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        setResponsePage(CollectionUserPrivacyManagementPage.class, parameters);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String userId = (String) object.get("userId");
        String collectionId = (String) object.get("collectionId");
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            context.delete(Tables.COLLECTION_USER_PRIVACY).where(Tables.COLLECTION_USER_PRIVACY.USER_ID.eq(userId)).and(Tables.COLLECTION_USER_PRIVACY.COLLECTION_ID.eq(collectionId)).execute();
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", this.collectionId);
            setResponsePage(CollectionUserPrivacyManagementPage.class, parameters);
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

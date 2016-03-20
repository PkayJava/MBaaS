package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryUserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.model.entity.tables.records.QueryUserPrivacyRecord;
import com.angkorteam.mbaas.plain.enums.QueryPermissionEnum;
import com.angkorteam.mbaas.server.provider.QueryUserPrivacyProvider;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/20/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/query/user/privacy/management")
public class QueryUserPrivacyManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String queryId;

    private UserPojo user;
    private DropDownChoice<UserPojo> userField;
    private TextFeedbackPanel userFeedback;

    private Boolean modify;
    private DropDownChoice<Boolean> modifyField;
    private TextFeedbackPanel modifyFeedback;

    private Boolean read;
    private DropDownChoice<Boolean> readField;
    private TextFeedbackPanel readFeedback;

    private Boolean delete;
    private DropDownChoice<Boolean> deleteField;
    private TextFeedbackPanel deleteFeedback;

    private Boolean execute;
    private DropDownChoice<Boolean> executeField;
    private TextFeedbackPanel executeFeedback;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");

        this.queryId = getPageParameters().get("queryId").toString();

        QueryUserPrivacyProvider provider = new QueryUserPrivacyProvider(this.queryId);
        provider.selectField(String.class, "userId");
        provider.selectField(String.class, "queryId");


        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("modify", this), "modify", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("delete", this), "delete", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("execute", this), "execute", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("read", this), "read", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", QueryManagementPage.class, getPageParameters());
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

        this.deleteField = new DropDownChoice<>("deleteField", new PropertyModel<>(this, "delete"), Arrays.asList(true, false));
        this.deleteField.setRequired(true);
        form.add(this.deleteField);
        this.deleteFeedback = new TextFeedbackPanel("deleteFeedback", this.deleteField);
        form.add(this.deleteFeedback);

        this.modifyField = new DropDownChoice<>("modifyField", new PropertyModel<>(this, "modify"), Arrays.asList(true, false));
        this.modifyField.setRequired(true);
        form.add(this.modifyField);
        this.modifyFeedback = new TextFeedbackPanel("modifyFeedback", this.modifyField);
        form.add(this.modifyFeedback);

        this.readField = new DropDownChoice<>("readField", new PropertyModel<>(this, "read"), Arrays.asList(true, false));
        this.readField.setRequired(true);
        form.add(this.readField);
        this.readFeedback = new TextFeedbackPanel("readFeedback", this.readField);
        form.add(this.readFeedback);

        this.executeField = new DropDownChoice<>("executeField", new PropertyModel<>(this, "execute"), Arrays.asList(true, false));
        this.executeField.setRequired(true);
        form.add(this.executeField);
        this.executeFeedback = new TextFeedbackPanel("executeFeedback", this.executeField);
        form.add(this.executeFeedback);

    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        QueryUserPrivacyTable queryUserPrivacyTable = Tables.QUERY_USER_PRIVACY.as("queryUserPrivacyTable");
        context.delete(queryUserPrivacyTable).where(queryUserPrivacyTable.USER_ID.eq(user.getUserId())).and(queryUserPrivacyTable.QUERY_ID.eq(queryId)).execute();
        QueryUserPrivacyRecord queryUserPrivacyRecord = context.newRecord(queryUserPrivacyTable);
        queryUserPrivacyRecord.setUserId(this.user.getUserId());
        queryUserPrivacyRecord.setQueryId(this.queryId);
        Integer permission = 0;
        if (this.modify) {
            permission = permission | QueryPermissionEnum.Modify.getLiteral();
        }
        if (this.read) {
            permission = permission | QueryPermissionEnum.Read.getLiteral();
        }
        if (this.execute) {
            permission = permission | QueryPermissionEnum.Delete.getLiteral();
        }
        if (this.delete) {
            permission = permission | QueryPermissionEnum.Execute.getLiteral();
        }
        queryUserPrivacyRecord.setPermisson(permission);
        queryUserPrivacyRecord.store();
        PageParameters parameters = new PageParameters();
        parameters.add("queryId", this.queryId);
        setResponsePage(QueryUserPrivacyManagementPage.class, parameters);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String userId = (String) object.get("userId");
        String queryId = (String) object.get("queryId");
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            context.delete(Tables.QUERY_USER_PRIVACY).where(Tables.QUERY_USER_PRIVACY.USER_ID.eq(userId)).and(Tables.QUERY_USER_PRIVACY.QUERY_ID.eq(queryId)).execute();
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", this.queryId);
            setResponsePage(QueryUserPrivacyManagementPage.class, parameters);
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

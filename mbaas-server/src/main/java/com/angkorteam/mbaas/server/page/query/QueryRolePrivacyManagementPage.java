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
import com.angkorteam.mbaas.model.entity.tables.QueryRolePrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRolePrivacyRecord;
import com.angkorteam.mbaas.plain.enums.QueryPermissionEnum;
import com.angkorteam.mbaas.server.provider.QueryRolePrivacyProvider;
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
@Mount("/query/role/privacy/management")
public class QueryRolePrivacyManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String queryId;

    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

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
        RoleTable roleTable = Tables.ROLE.as("roleTable");

        this.queryId = getPageParameters().get("queryId").toString();

        QueryRolePrivacyProvider provider = new QueryRolePrivacyProvider(this.queryId);
        provider.selectField(String.class, "roleId");
        provider.selectField(String.class, "queryId");


        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("roleName", this), "roleName", this, provider));
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

        List<RolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(RolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new RoleChoiceRenderer());
        this.roleField.setRequired(true);
        form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        form.add(this.roleFeedback);

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

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        DSLContext context = getDSLContext();
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.QUERY_ID.eq(this.queryId)).fetchOneInto(queryTable);
        if (getSession().isBackOffice() && !queryRecord.getOwnerUserId().equals(getSession().getUserId())) {
            setResponsePage(QueryManagementPage.class);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        QueryRolePrivacyTable queryRolePrivacyTable = Tables.QUERY_ROLE_PRIVACY.as("queryRolePrivacyTable");
        context.delete(queryRolePrivacyTable).where(queryRolePrivacyTable.ROLE_ID.eq(role.getRoleId())).and(queryRolePrivacyTable.QUERY_ID.eq(queryId)).execute();
        QueryRolePrivacyRecord queryRolePrivacyRecord = context.newRecord(queryRolePrivacyTable);
        queryRolePrivacyRecord.setQueryRolePrivacyId(UUID.randomUUID().toString());
        queryRolePrivacyRecord.setRoleId(this.role.getRoleId());
        queryRolePrivacyRecord.setQueryId(this.queryId);
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
        queryRolePrivacyRecord.setPermisson(permission);
        queryRolePrivacyRecord.store();
        PageParameters parameters = new PageParameters();
        parameters.add("queryId", this.queryId);
        setResponsePage(QueryRolePrivacyManagementPage.class, parameters);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String roleId = (String) object.get("roleId");
        String queryId = (String) object.get("queryId");
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            context.delete(Tables.QUERY_ROLE_PRIVACY).where(Tables.QUERY_ROLE_PRIVACY.ROLE_ID.eq(roleId)).and(Tables.QUERY_ROLE_PRIVACY.QUERY_ID.eq(queryId)).execute();
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", this.queryId);
            setResponsePage(QueryRolePrivacyManagementPage.class, parameters);
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

package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasRoleTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MbaasRolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.server.renderer.MBaaSRoleChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation({"mbaas.system"})
@Mount("/mbaas/user/modify")
public class UserModifyPage extends MBaaSPage {

    private String mbaasUserId;

    private String login;
    private Label loginLabel;

    private String fullName;
    private TextField<String> fullNameField;
    private TextFeedbackPanel fullNameFeedback;

    private MbaasRolePojo role;
    private DropDownChoice<MbaasRolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

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

        this.form = new Form<>("form");
        add(this.form);

        PageParameters parameters = getPageParameters();
        this.mbaasUserId = parameters.get("mbaasUserId").toString();

        MbaasRoleTable roleTable = Tables.MBAAS_ROLE.as("roleTable");
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");

        MbaasUserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.MBAAS_USER_ID.eq(mbaasUserId)).fetchOneInto(userTable);

        this.fullName = userRecord.getFullName();
        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"));
        this.fullNameField.setRequired(true);
        this.fullNameField.setLabel(JooqUtils.lookup("fullName", this));
        this.form.add(fullNameField);
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField);
        this.form.add(fullNameFeedback);

        this.login = userRecord.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(this.loginLabel);

        List<MbaasRolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(MbaasRolePojo.class);
        this.role = context.select(roleTable.fields()).from(roleTable).where(roleTable.MBAAS_ROLE_ID.eq(userRecord.getMbaasRoleId())).fetchOneInto(MbaasRolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new MBaaSRoleChoiceRenderer());
        this.roleField.setRequired(true);
        this.roleField.setLabel(JooqUtils.lookup("role", this));
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        MbaasUserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.MBAAS_USER_ID.eq(mbaasUserId)).fetchOneInto(userTable);
        userRecord.setFullName(this.fullName);
        userRecord.setMbaasRoleId(this.role.getMbaasRoleId());
        userRecord.update();
        setResponsePage(UserManagementPage.class);
    }

}

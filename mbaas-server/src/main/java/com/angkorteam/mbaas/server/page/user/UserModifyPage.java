package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/user/modify")
public class UserModifyPage extends Page {

    private String userId;
    private Integer optimistic;

    private String login;
    private Label loginLabel;

    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        UserTable userTable = Tables.USER.as("userTable");

        PageParameters parameters = getPageParameters();

        this.userId = parameters.get("userId").toString();

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

        this.form.add(this.loginLabel);
        this.form.add(this.roleField);
        this.form.add(this.roleFeedback);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(userId)).fetchOneInto(userTable);
        userRecord.setRoleId(this.role.getRoleId());
        userRecord.setOptimistic(this.optimistic);
        userRecord.update();
        setResponsePage(UserManagementPage.class);
    }

}

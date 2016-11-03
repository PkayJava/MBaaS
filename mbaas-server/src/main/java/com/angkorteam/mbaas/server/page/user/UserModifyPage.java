package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.choice.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */

public class UserModifyPage extends MBaaSPage {

    private String userId;

    private String fullName;
    private TextField<String> fullNameField;
    private TextFeedbackPanel fullNameFeedback;

    private String login;
    private Label loginLabel;

    private List<RolePojo> roles;
    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Button saveButton;
    private Form<Void> form;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return UserModifyPage.class.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        UserTable userTable = Tables.USER.as("userTable");

        PageParameters parameters = getPageParameters();
        this.userId = parameters.get("userId").toString("");

        UserPojo user = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(this.userId)).fetchOneInto(UserPojo.class);

        this.form = new Form<>("form");
        add(this.form);

        this.fullName = user.getFullName();
        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"));
        this.fullNameField.setRequired(true);
        this.form.add(fullNameField);
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField);
        this.form.add(fullNameFeedback);

        this.login = user.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(loginLabel);

        if (user.getRoleId() != null) {
            this.role = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(user.getRoleId())).fetchOneInto(RolePojo.class);
        }
        this.roles = context.select(roleTable.fields()).from(roleTable).fetchInto(RolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), new PropertyModel<>(this, "roles"), new RoleChoiceRenderer());
        this.roleField.setRequired(true);
        this.form.add(roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(roleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", UserBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        UserTable userTable = Tables.USER.as("userTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(this.userId)).fetchOneInto(userTable);
        userRecord.setFullName(this.fullName);
        if (this.role != null) {
            userRecord.setRoleId(this.role.getRoleId());
        } else {
            userRecord.setRoleId(null);
        }
        userRecord.update();

        setResponsePage(UserBrowsePage.class);
    }
}

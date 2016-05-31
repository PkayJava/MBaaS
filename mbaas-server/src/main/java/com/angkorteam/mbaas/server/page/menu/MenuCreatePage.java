package com.angkorteam.mbaas.server.page.menu;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.MenuTitleValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 5/29/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/menu/create")
public class MenuCreatePage extends MasterPage {

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Menu";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new MenuTitleValidator(getSession().getApplicationCode()));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String parentMenuId = jdbcTemplate.queryForObject("SELECT " + Jdbc.Menu.MENU_ID + " FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.PARENT_MENU_ID + " IS NULL", String.class);
        String menuId = UUID.randomUUID().toString();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Menu.MENU_ID, menuId);
        fields.put(Jdbc.Menu.DATE_CREATED, new Date());
        fields.put(Jdbc.Menu.TITLE, this.title);
        fields.put(Jdbc.Menu.USER_ID, getSession().getApplicationUserId());
        fields.put(Jdbc.Menu.PARENT_MENU_ID, parentMenuId);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.MENU);
        jdbcInsert.execute(fields);
        setResponsePage(MenuManagementPage.class);
    }
}

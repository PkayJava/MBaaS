package com.angkorteam.mbaas.server.page.menu;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.MenuTitleValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 5/29/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/menu/modify")
public class MenuModifyPage extends MasterPage {

    private String menuId;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Menu";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        this.menuId = getPageParameters().get("menuId").toString("");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> menuRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.MENU_ID + " = ?", this.menuId);

        this.title = (String) menuRecord.get(Jdbc.Menu.TITLE);
        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new MenuTitleValidator(getSession().getApplicationCode(), this.menuId));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Menu.MENU_ID, this.menuId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Menu.TITLE, this.title);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.MENU);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(MenuManagementPage.class);
    }
}

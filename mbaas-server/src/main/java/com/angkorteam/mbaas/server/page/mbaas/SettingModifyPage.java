package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SettingTable;
import com.angkorteam.mbaas.model.entity.tables.records.SettingRecord;
import com.angkorteam.mbaas.server.validator.SettingKeyValidator;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation("mbaas.system")
@Mount("/mbaas/setting/modify")
public class SettingModifyPage extends MBaaSPage {

    private String settingId;

    private String key;
    private TextField<String> keyField;
    private TextFeedbackPanel keyFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String value;
    private TextField<String> valueField;
    private TextFeedbackPanel valueFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Setting";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();

        SettingTable settingTable = Tables.SETTING.as("settingTable");

        this.settingId = getPageParameters().get("settingId").toString();

        SettingRecord settingRecord = context.select(settingTable.fields()).from(settingTable).where(settingTable.SETTING_ID.eq(settingId)).fetchOneInto(settingTable);

        this.form = new Form<>("form");
        add(this.form);

        this.key = settingRecord.getSettingId();
        this.keyField = new TextField<>("keyField", new PropertyModel<>(this, "key"));
        this.keyField.setRequired(true);
        this.keyField.add(new SettingKeyValidator());
        this.form.add(this.keyField);
        this.keyFeedback = new TextFeedbackPanel("keyFeedback", this.keyField);
        this.form.add(this.keyFeedback);

        this.description = settingRecord.getDescription();
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.value = settingRecord.getValue();
        this.valueField = new TextField<>("valueField", new PropertyModel<>(this, "value"));
        this.valueField.setRequired(true);
        this.form.add(this.valueField);
        this.valueFeedback = new TextFeedbackPanel("valueFeedback", this.valueField);
        this.form.add(this.valueFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        SettingTable settingTable = Tables.SETTING.as("setting");

        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        jdbcTemplate.update("UPDATE " + Tables.SETTING.getName() + " SET " + settingTable.SETTING_ID.getName() + " = ?, " + settingTable.DESCRIPTION.getName() + " = ?, `" + settingTable.VALUE.getName() + "` = ? where " + settingTable.SETTING_ID.getName() + " = ?", this.key, this.description, this.value, this.settingId);

        setResponsePage(SettingManagementPage.class);
    }

}

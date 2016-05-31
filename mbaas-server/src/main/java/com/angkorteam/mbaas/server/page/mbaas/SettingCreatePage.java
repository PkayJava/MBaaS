package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
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

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation("mbaas.system")
@Mount("/mbaas/setting/create")
public class SettingCreatePage extends MBaaSPage{

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
        return "Create New Setting";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.keyField = new TextField<>("keyField", new PropertyModel<>(this, "key"));
        this.keyField.setRequired(true);
        this.keyField.add(new SettingKeyValidator());
        this.form.add(this.keyField);
        this.keyFeedback = new TextFeedbackPanel("keyFeedback", this.keyField);
        this.form.add(this.keyFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

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
        DSLContext context = getDSLContext();
        SettingTable settingTable = Tables.SETTING.as("settingTable");

        SettingRecord settingRecord = context.newRecord(settingTable);
        settingRecord.setSettingId(this.key);
        settingRecord.setValue(this.value);
        settingRecord.setDescription(this.description);
        settingRecord.setSystem(false);
        settingRecord.store();

        setResponsePage(SettingManagementPage.class);
    }

}

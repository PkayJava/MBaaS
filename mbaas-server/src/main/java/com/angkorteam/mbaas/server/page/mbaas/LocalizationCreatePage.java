package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LocalizationTable;
import com.angkorteam.mbaas.model.entity.tables.records.LocalizationRecord;
import com.angkorteam.mbaas.server.validator.LocalizationValidator;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.UUID;

/**
 * Created by socheat on 3/13/16.
 */
@AuthorizeInstantiation("mbaas.system")
@Mount("/mbaas/localization/create")
public class LocalizationCreatePage extends MBaaSPage {

    private String key;
    private TextField<String> keyField;
    private TextFeedbackPanel keyFeedback;

    private String label;
    private TextField<String> labelField;
    private TextFeedbackPanel labelFeedback;

    private String language;
    private TextField<String> languageField;
    private TextFeedbackPanel languageFeedback;

    private String pageText;
    private TextField<String> pageField;
    private TextFeedbackPanel pageFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Localization";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.keyField = new TextField<>("keyField", new PropertyModel<>(this, "key"));
        this.keyField.setRequired(true);
        this.form.add(this.keyField);
        this.keyFeedback = new TextFeedbackPanel("keyFeedback", this.keyField);
        this.form.add(this.keyFeedback);

        this.labelField = new TextField<>("labelField", new PropertyModel<>(this, "label"));
        this.labelField.setRequired(true);
        this.form.add(this.labelField);
        this.labelFeedback = new TextFeedbackPanel("labelFeedback", this.labelField);
        this.form.add(this.labelFeedback);

        this.languageField = new TextField<>("languageField", new PropertyModel<>(this, "language"));
        this.form.add(this.languageField);
        this.languageFeedback = new TextFeedbackPanel("languageFeedback", this.languageField);
        this.form.add(this.languageFeedback);

        this.pageField = new TextField<>("pageField", new PropertyModel<>(this, "pageText"));
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.saveButton = new Button("saveButton");
        this.form.add(this.saveButton);

        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(new LocalizationValidator(pageField, keyField, languageField));
    }

    private void saveButtonOnSubmit(Button button) {
        LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
        DSLContext context = getDSLContext();
        String uuid = UUID.randomUUID().toString();
        LocalizationRecord localizationRecord = context.newRecord(localizationTable);
        localizationRecord.setLocalizationId(uuid);
        localizationRecord.setKey(this.key);
        localizationRecord.setLanguage(this.language);
        localizationRecord.setPage(this.pageText);
        localizationRecord.setLabel(this.label);
        localizationRecord.store();
        setResponsePage(LocalizationManagementPage.class);
    }

}

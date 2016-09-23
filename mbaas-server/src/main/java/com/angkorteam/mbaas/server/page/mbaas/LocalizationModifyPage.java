package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
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

/**
 * Created by socheat on 3/13/16.
 */
@AuthorizeInstantiation({"mbaas.administrator", "mbaas.system"})
@Mount("/mbaas/localization/modify")
public class LocalizationModifyPage extends MBaaSPage {

    private String localizationId;

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
        return "Modify Localization";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();
        LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");

        this.localizationId = getPageParameters().get("localizationId").toString();
        LocalizationRecord localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.LOCALIZATION_ID.eq(localizationId)).fetchOneInto(localizationTable);

        this.form = new Form<>("form");
        add(this.form);

        this.key = localizationRecord.getKey();
        this.keyField = new TextField<>("keyField", new PropertyModel<>(this, "key"));
        this.keyField.setRequired(true);
        this.form.add(this.keyField);
        this.keyFeedback = new TextFeedbackPanel("keyFeedback", this.keyField);
        this.form.add(this.keyFeedback);

        this.label = localizationRecord.getLabel();
        this.labelField = new TextField<>("labelField", new PropertyModel<>(this, "label"));
        this.labelField.setRequired(true);
        this.form.add(this.labelField);
        this.labelFeedback = new TextFeedbackPanel("labelFeedback", this.labelField);
        this.form.add(this.labelFeedback);

        this.language = localizationRecord.getLanguage();
        this.languageField = new TextField<>("languageField", new PropertyModel<>(this, "language"));
        this.form.add(this.languageField);
        this.languageFeedback = new TextFeedbackPanel("languageFeedback", this.languageField);
        this.form.add(this.languageFeedback);

        this.pageText = localizationRecord.getPage();
        this.pageField = new TextField<>("pageField", new PropertyModel<>(this, "pageText"));
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.saveButton = new Button("saveButton");
        this.form.add(this.saveButton);

        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(new LocalizationValidator(this.localizationId, pageField, keyField, languageField));
    }

    private void saveButtonOnSubmit(Button button) {
        LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
        DSLContext context = getDSLContext();
        LocalizationRecord localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.LOCALIZATION_ID.eq(localizationId)).fetchOneInto(localizationTable);
        localizationRecord.setKey(this.key);
        localizationRecord.setLanguage(this.language);
        localizationRecord.setPage(this.pageText);
        localizationRecord.setLabel(this.label);
        localizationRecord.update();
        setResponsePage(LocalizationManagementPage.class);
    }

}

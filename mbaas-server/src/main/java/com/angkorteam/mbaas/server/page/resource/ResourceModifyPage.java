package com.angkorteam.mbaas.server.page.resource;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ResourceTable;
import com.angkorteam.mbaas.model.entity.tables.records.ResourceRecord;
import com.angkorteam.mbaas.server.validator.ResourceValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/13/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/resource/modify")
public class ResourceModifyPage extends MasterPage {

    private String resourceId;

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
        ResourceTable resourceTable = Tables.RESOURCE.as("resourceTable");

        this.resourceId = getPageParameters().get("resourceId").toString();
        ResourceRecord resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.RESOURCE_ID.eq(resourceId)).fetchOneInto(resourceTable);

        this.form = new Form<>("form");
        add(this.form);

        this.key = resourceRecord.getKey();
        this.keyField = new TextField<>("keyField", new PropertyModel<>(this, "key"));
        this.keyField.setRequired(true);
        this.form.add(this.keyField);
        this.keyFeedback = new TextFeedbackPanel("keyFeedback", this.keyField);
        this.form.add(this.keyFeedback);

        this.label = resourceRecord.getLabel();
        this.labelField = new TextField<>("labelField", new PropertyModel<>(this, "label"));
        this.labelField.setRequired(true);
        this.form.add(this.labelField);
        this.labelFeedback = new TextFeedbackPanel("labelFeedback", this.labelField);
        this.form.add(this.labelFeedback);

        this.language = resourceRecord.getLanguage();
        this.languageField = new TextField<>("languageField", new PropertyModel<>(this, "language"));
        this.form.add(this.languageField);
        this.languageFeedback = new TextFeedbackPanel("languageFeedback", this.languageField);
        this.form.add(this.languageFeedback);

        this.pageText = resourceRecord.getPage();
        this.pageField = new TextField<>("pageField", new PropertyModel<>(this, "pageText"));
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.saveButton = new Button("saveButton");
        this.form.add(this.saveButton);

        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(new ResourceValidator(this.resourceId, pageField, keyField, languageField));
    }

    private void saveButtonOnSubmit(Button button) {
        ResourceTable resourceTable = Tables.RESOURCE.as("resourceTable");
        DSLContext context = getDSLContext();
        ResourceRecord resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.RESOURCE_ID.eq(resourceId)).fetchOneInto(resourceTable);
        resourceRecord.setKey(this.key);
        resourceRecord.setLanguage(this.language);
        resourceRecord.setPage(this.pageText);
        resourceRecord.setLabel(this.label);
        resourceRecord.update();
        setResponsePage(ResourceManagementPage.class);
    }

}

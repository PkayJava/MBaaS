package com.angkorteam.mbaas.server.page.asset;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AssetTable;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.template.TextFieldPanel;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.io.File;
import java.util.*;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice", "registered"})
@Mount("/asset/create")
public class AssetCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<FileUpload> asset;
    private FileUploadField assetField;
    private TextFeedbackPanel assetFeedback;

    private String collectionId;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Asset";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.form.add(nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(nameFeedback);

        this.assetField = new FileUploadField("assetField", new PropertyModel<>(this, "asset"));
        this.assetField.setRequired(true);
        this.form.add(this.assetField);
        this.assetFeedback = new TextFeedbackPanel("assetFeedback", this.assetField);
        this.form.add(assetFeedback);

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.ASSET.getName())).fetchOneInto(collectionTable);
        this.collectionId = collectionRecord.getCollectionId();

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        FileUpload asset = this.asset.get(0);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String assetRepo = DateFormatUtils.format(new Date(), patternFolder);

        long length = asset.getSize();
        String path = assetRepo;
        String mime = asset.getContentType();
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(asset.getClientFileName()));

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = getDSLContext().select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        Map<String, Object> fields = new HashMap<>();
        requestBody.setDocument(fields);
        fields.put(Tables.ASSET.PATH.getName(), path);
        fields.put(Tables.ASSET.MIME.getName(), mime);
        fields.put(Tables.ASSET.EXTENSION.getName(), extension);
        fields.put(Tables.ASSET.LENGTH.getName(), length);
        fields.put(Tables.ASSET.LABEL.getName(), this.name);
        String documentId = UUID.randomUUID().toString();
        DocumentFunction.insertDocument(getDSLContext(), getJdbcTemplate(), getSession().getUserId(), documentId, collectionRecord.getName(), requestBody);
        String name = documentId + "_" + this.name;

        AssetTable assetTable = Tables.ASSET.as("assetTable");
        DSLContext context = getDSLContext();
        context.update(assetTable).set(assetTable.NAME, name).where(assetTable.ASSET_ID.eq(documentId)).execute();

        File container = new File(repo + "/asset" + assetRepo);
        container.mkdirs();

        try {
            asset.writeTo(new File(container, name));
        } catch (Exception e) {
        }

        setResponsePage(AssetManagementPage.class);
    }
}

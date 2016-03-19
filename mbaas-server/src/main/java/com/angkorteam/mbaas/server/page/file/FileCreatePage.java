package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.page.document.FieldPanel;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/file/create")
public class FileCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<FileUpload> file;
    private FileUploadField fileField;
    private TextFeedbackPanel fileFeedback;

    private String collectionId;
    private Map<String, Object> fields;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New File";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        this.fields = new LinkedHashMap<>();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.form.add(nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(nameFeedback);

        this.fileField = new FileUploadField("fileField", new PropertyModel<>(this, "file"));
        this.fileField.setRequired(true);
        this.form.add(this.fileField);
        this.fileFeedback = new TextFeedbackPanel("fileFeedback", this.fileField);
        this.form.add(fileFeedback);

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.FILE.getName())).fetchOneInto(collectionTable);
        this.collectionId = collectionRecord.getCollectionId();

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributePojo> attributePojos = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Boolean.getLiteral())
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Byte.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Short.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Integer.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Long.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Float.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Double.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Character.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.String.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Time.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.Date.getLiteral()))
                        .or(attributeTable.JAVA_TYPE.eq(AttributeTypeEnum.DateTime.getLiteral())))
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributePojos) {
            FieldPanel fieldPanel = new FieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
        }
        this.form.add(fields);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        FileUpload file = this.file.get(0);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String fileRepo = DateFormatUtils.format(new Date(), patternFolder);

        long length = file.getSize();
        String path = fileRepo;
        String mime = file.getContentType();
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getClientFileName()));

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = getDSLContext().select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        requestBody.setDocument(fields);
        fields.put(Tables.FILE.PATH.getName(), path);
        fields.put(Tables.FILE.MIME.getName(), mime);
        fields.put(Tables.FILE.EXTENSION.getName(), extension);
        fields.put(Tables.FILE.LENGTH.getName(), length);
        fields.put(Tables.FILE.LABEL.getName(), this.name);
        final String uuid = DocumentFunction.insertDocument(getDSLContext(), getJdbcTemplate(), getSession().getUserId(), collectionRecord.getName(), requestBody);
        String name = uuid + "_" + this.name;

        FileTable fileTable = Tables.FILE.as("fileTable");
        DSLContext context = getDSLContext();
        context.update(fileTable).set(fileTable.NAME, name).where(fileTable.FILE_ID.eq(uuid)).execute();

        File container = new File(repo + "/file" + fileRepo);
        container.mkdirs();

        try {
            file.writeTo(new File(container, name));
        } catch (Exception e) {
        }

        setResponsePage(FileManagementPage.class);
    }
}

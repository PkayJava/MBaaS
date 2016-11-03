package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.records.FileRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class FileCreatePage extends MBaaSPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<FileUpload> file;
    private FileUploadField fileField;
    private TextFeedbackPanel fileFeedback;

    private Button saveButton;
    private Form<Void> form;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return FileCreatePage.class.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.setLabel(Model.of("name"));
        this.form.add(nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(nameFeedback);

        this.fileField = new FileUploadField("fileField", new PropertyModel<>(this, "file"));
        this.fileField.setRequired(true);
        this.form.add(this.fileField);
        this.fileFeedback = new TextFeedbackPanel("fileFeedback", this.fileField);
        this.form.add(fileFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", FileBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        FileTable fileTable = Tables.FILE.as("fileTable");
        System system = Spring.getBean(System.class);

        FileUpload file = this.file.get(0);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String fileRepo = DateFormatUtils.format(new Date(), patternFolder);
        File container = new File(repo, fileRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getClientFileName()));
        String fileId = system.randomUUID();
        String name = fileId + "_" + this.name + "." + extension;
        container.mkdirs();
        try {
            file.writeTo(new File(container, name));
        } catch (Exception e) {
        }

        long length = file.getSize();
        String path = fileRepo;
        String mime = file.getContentType();
        String label = this.name;

        FileRecord fileRecord = context.newRecord(fileTable);
        fileRecord.setFileId(fileId);
        fileRecord.setPath(path);
        fileRecord.setMime(mime);
        fileRecord.setExtension(extension);
        fileRecord.setLength((int) length);
        fileRecord.setLabel(label);
        fileRecord.setName(name);
        fileRecord.setDateCreated(new Date());
        fileRecord.store();

        setResponsePage(FileBrowsePage.class);
    }
}

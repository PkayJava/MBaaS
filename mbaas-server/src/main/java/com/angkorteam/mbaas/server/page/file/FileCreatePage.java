package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
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
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.io.File;
import java.util.*;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/file/create")
public class FileCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<FileUpload> file;
    private FileUploadField fileField;
    private TextFeedbackPanel fileFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New File";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
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
        File container = new File(repo + "/" + getApplicationCode() + "/file" + fileRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getClientFileName()));
        String fileId = UUID.randomUUID().toString();
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
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.File.FILE_ID, fileId);
        fields.put(Jdbc.File.APPLICATION_CODE, getApplicationCode());
        fields.put(Jdbc.File.PATH, path);
        fields.put(Jdbc.File.MIME, mime);
        fields.put(Jdbc.File.EXTENSION, extension);
        fields.put(Jdbc.File.LENGTH, length);
        fields.put(Jdbc.File.LABEL, label);
        fields.put(Jdbc.File.NAME, name);
        fields.put(Jdbc.File.DATE_CREATED, new Date());
        fields.put(Jdbc.File.USER_ID, getSession().getApplicationUserId());

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.FILE);
        jdbcInsert.execute(fields);

        setResponsePage(FileManagementPage.class);
    }
}

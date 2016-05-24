package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/file/modify")
public class FileModifyPage extends MasterPage {

    private String fileId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private Integer length;
    private Label lengthLabel;

    private String mime;
    private Label mimeLabel;

    private String extension;
    private Label extensionLabel;

    private String pathText;
    private Label pathLabel;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify File";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        PageParameters parameters = getPageParameters();
        this.fileId = parameters.get("fileId").toString();

        Map<String, Object> fileRecord = null;
        fileRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.FILE + " WHERE " + Jdbc.File.FILE_ID + " = ?", this.fileId);

        this.form = new Form<>("form");
        add(this.form);

        this.name = (String) fileRecord.get(Jdbc.File.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.length = (Integer) fileRecord.get(Jdbc.File.LENGTH);
        this.lengthLabel = new Label("lengthLabel", new PropertyModel<>(this, "length"));
        this.form.add(lengthLabel);

        this.mime = (String) fileRecord.get(Jdbc.File.MIME);
        this.mimeLabel = new Label("mimeLabel", new PropertyModel<>(this, "mime"));
        this.form.add(mimeLabel);

        this.extension = (String) fileRecord.get(Jdbc.File.EXTENSION);
        this.extensionLabel = new Label("extensionLabel", new PropertyModel<>(this, "extension"));
        this.form.add(extensionLabel);

        this.pathText = (String) fileRecord.get(Jdbc.File.PATH);
        this.pathLabel = new Label("pathLabel", new PropertyModel<>(this, "pathText"));
        this.form.add(pathLabel);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        jdbcTemplate.update("UPDATE " + Jdbc.FILE + " SET " + Jdbc.File.LABEL + " = ? WHERE " + Jdbc.File.FILE_ID + " = ?", this.name, this.fileId);
        setResponsePage(FileManagementPage.class);
    }
}

package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.FilePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/11/16.
 */
public class FileModifyPage extends MBaaSPage {

    private String fileId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String size;
    private Label sizeLabel;

    private String mime;
    private Label mimeLabel;

    private String extension;
    private Label extensionLabel;

    private String pathText;
    private Label pathLabel;

    private Button saveButton;
    private Form<Void> form;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return FileModifyPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        FileTable fileTable = Tables.FILE.as("fileTable");

        PageParameters parameters = getPageParameters();
        this.fileId = parameters.get("fileId").toString();

        FilePojo fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(fileId)).fetchOneInto(FilePojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.name = fileRecord.getName();
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.size = FileUtils.byteCountToDisplaySize(fileRecord.getLength());
        this.sizeLabel = new Label("sizeLabel", new PropertyModel<>(this, "size"));
        this.form.add(this.sizeLabel);

        this.mime = fileRecord.getMime();
        this.mimeLabel = new Label("mimeLabel", new PropertyModel<>(this, "mime"));
        this.form.add(this.mimeLabel);

        this.extension = fileRecord.getExtension();
        this.extensionLabel = new Label("extensionLabel", new PropertyModel<>(this, "extension"));
        this.form.add(this.extensionLabel);

        this.pathText = fileRecord.getPath();
        this.pathLabel = new Label("pathLabel", new PropertyModel<>(this, "pathText"));
        this.form.add(this.pathLabel);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", FileBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        jdbcTemplate.update("UPDATE " + Tables.FILE.getName() + " SET " + Tables.FILE.LABEL.getName() + " = ? WHERE " + Tables.FILE.FILE_ID.getName() + " = ?", this.name, this.fileId);
        setResponsePage(FileBrowsePage.class);
    }
}

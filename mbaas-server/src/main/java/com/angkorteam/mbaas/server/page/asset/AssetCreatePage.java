package com.angkorteam.mbaas.server.page.asset;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
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
@AuthorizeInstantiation({"administrator", "registered"})
@Mount("/asset/create")
public class AssetCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<FileUpload> asset;
    private FileUploadField assetField;
    private TextFeedbackPanel assetFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Asset";
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

        this.assetField = new FileUploadField("assetField", new PropertyModel<>(this, "asset"));
        this.assetField.setRequired(true);
        this.form.add(this.assetField);
        this.assetFeedback = new TextFeedbackPanel("assetFeedback", this.assetField);
        this.form.add(assetFeedback);

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
        File container = new File(repo + "/" + getApplicationCode() + "/asset" + assetRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(asset.getClientFileName()));
        String assetId = UUID.randomUUID().toString();
        String name = assetId + "_" + this.name + "." + extension;
        container.mkdirs();
        try {
            asset.writeTo(new File(container, name));
        } catch (Exception e) {
        }

        long length = asset.getSize();
        String path = assetRepo;
        String mime = asset.getContentType();
        String label = this.name;
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Asset.ASSET_ID, assetId);
        fields.put(Jdbc.Asset.APPLICATION_CODE, getApplicationCode());
        fields.put(Jdbc.Asset.PATH, path);
        fields.put(Jdbc.Asset.MIME, mime);
        fields.put(Jdbc.Asset.EXTENSION, extension);
        fields.put(Jdbc.Asset.LENGTH, length);
        fields.put(Jdbc.Asset.LABEL, label);
        fields.put(Jdbc.Asset.NAME, name);
        fields.put(Jdbc.Asset.DATE_CREATED, new Date());
        fields.put(Jdbc.Asset.USER_ID, getSession().getApplicationUserId());

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.ASSET);
        jdbcInsert.execute(fields);
        setResponsePage(AssetManagementPage.class);
    }
}

package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 6/12/16.
 */
public class Disk implements Serializable {

    private String applicationCode;

    private String applicationUserId;

    public Disk(String applicationCode, String applicationUserId) {
        this.applicationCode = applicationCode;
        this.applicationUserId = applicationUserId;
    }

    public String httpFile(String fileId) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        Map<String, Object> fileRecord = null;
        fileRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.FILE + " WHERE " + Jdbc.File.FILE_ID + " = ?", fileId);
        StringBuffer address = new StringBuffer();
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        address.append(HttpFunction.getHttpAddress(request)).append("/api/resource/").append(this.applicationCode).append("/file").append(fileRecord.get(Jdbc.File.PATH)).append("/").append(fileRecord.get(Jdbc.File.NAME));
        return address.toString();
    }

    public String httpAsset(String assetId) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        Map<String, Object> assetRecord = null;
        assetRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ASSET + " WHERE " + Jdbc.Asset.ASSET_ID + " = ?", assetId);
        StringBuffer address = new StringBuffer();
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        address.append(HttpFunction.getHttpAddress(request)).append("/api/resource/").append(this.applicationCode).append("/asset").append(assetRecord.get(Jdbc.Asset.PATH)).append("/").append(assetRecord.get(Jdbc.Asset.NAME));
        return address.toString();
    }

    public String writeFile(FileUpload fileUpload) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String fileRepo = DateFormatUtils.format(new Date(), patternFolder);
        File container = new File(repo + "/" + this.applicationCode + "/file" + fileRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileUpload.getClientFileName()));
        String fileId = UUID.randomUUID().toString();
        String name = fileId + "." + extension;
        container.mkdirs();
        File file = new File(container, name);
        try {
            fileUpload.writeTo(file);
        } catch (Exception e) {
        }
        long length = fileUpload.getSize();
        String path = fileRepo;
        String mime = fileUpload.getContentType();
        String label = fileUpload.getClientFileName();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.File.FILE_ID, fileId);
        fields.put(Jdbc.File.APPLICATION_CODE, this.applicationCode);
        fields.put(Jdbc.File.PATH, path);
        fields.put(Jdbc.File.MIME, mime);
        fields.put(Jdbc.File.EXTENSION, extension);
        fields.put(Jdbc.File.LENGTH, length);
        fields.put(Jdbc.File.LABEL, label);
        fields.put(Jdbc.File.NAME, name);
        fields.put(Jdbc.File.DATE_CREATED, new Date());
        fields.put(Jdbc.File.USER_ID, this.applicationUserId);
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.FILE);
        jdbcInsert.execute(fields);
        return fileId;
    }

    public String writeAsset(FileUpload fileUpload) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String assetRepo = DateFormatUtils.format(new Date(), patternFolder);
        File container = new File(repo + "/" + this.applicationCode + "/asset" + assetRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileUpload.getClientFileName()));
        String assetId = UUID.randomUUID().toString();
        String name = assetId + "." + extension;
        container.mkdirs();
        try {
            fileUpload.writeTo(new File(container, name));
        } catch (Exception e) {
        }
        long length = fileUpload.getSize();
        String path = assetRepo;
        String mime = fileUpload.getContentType();
        String label = fileUpload.getClientFileName();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Asset.ASSET_ID, assetId);
        fields.put(Jdbc.Asset.APPLICATION_CODE, this.applicationCode);
        fields.put(Jdbc.Asset.PATH, path);
        fields.put(Jdbc.Asset.MIME, mime);
        fields.put(Jdbc.Asset.EXTENSION, extension);
        fields.put(Jdbc.Asset.LENGTH, length);
        fields.put(Jdbc.Asset.LABEL, label);
        fields.put(Jdbc.Asset.NAME, name);
        fields.put(Jdbc.Asset.DATE_CREATED, new Date());
        fields.put(Jdbc.Asset.USER_ID, this.applicationUserId);
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.ASSET);
        jdbcInsert.execute(fields);
        return assetId;
    }

}

package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AssetTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AssetRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.request.asset.AssetCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.response.asset.AssetCreateResponse;
import com.angkorteam.mbaas.plain.response.asset.AssetDeleteResponse;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.HttpFunction;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 4/9/16.
 */
@Controller
@RequestMapping(path = "/asset")
public class AssetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(
            path = "/create/{filename:.+}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AssetCreateResponse> create(
            HttpServletRequest request,
            Identity identity,
            @PathVariable("filename") String filename,
            @RequestBody(required = true) AssetCreateRequest requestBody
    ) {
        LOGGER.info("{}", request.getRequestURL());

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String fileRepo = DateFormatUtils.format(new Date(), patternFolder);

        long length = requestBody.getContent().length;
        String path = fileRepo;
        String mime = requestBody.getContentType();
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(filename));

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.ASSET.getName())).fetchOneInto(collectionTable);

        DocumentCreateRequest documentCreateRequest = new DocumentCreateRequest();

        Map<String, Object> fields = requestBody.getAttributes();
        documentCreateRequest.setDocument(fields);
        fields.put(Tables.ASSET.PATH.getName(), path);
        fields.put(Tables.ASSET.MIME.getName(), mime);
        fields.put(Tables.ASSET.EXTENSION.getName(), extension);
        fields.put(Tables.ASSET.LENGTH.getName(), length);
        fields.put(Tables.ASSET.LABEL.getName(), filename);
        fields.put(Tables.ASSET.CLIENT_ID.getName(), identity.getClientId());

        final String uuid = DocumentFunction.insertDocument(context, jdbcTemplate, identity.getUserId(), collectionRecord.getName(), documentCreateRequest);
        String name = uuid + "_" + filename;

        AssetTable assetTable = Tables.ASSET.as("assetTable");
        context.update(assetTable).set(assetTable.NAME, name).where(assetTable.ASSET_ID.eq(uuid)).execute();

        File container = new File(repo + "/asset" + fileRepo);
        container.mkdirs();

        try {
            FileUtils.writeByteArrayToFile(new File(container, name), requestBody.getContent());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }

        AssetRecord assetRecord = context.select(assetTable.fields()).from(assetTable).where(assetTable.ASSET_ID.eq(uuid)).fetchOneInto(assetTable);
        StringBuffer address = new StringBuffer();
        address.append(HttpFunction.getHttpAddress(request)).append("/api/resource/asset").append(assetRecord.getPath()).append("/").append(assetRecord.getName());

        AssetCreateResponse response = new AssetCreateResponse();
        response.getData().setContentType(mime);
        response.getData().setAssetId(uuid);
        response.getData().setFilename(filename);
        response.getData().setAddress(address.toString());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/delete/{assetId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AssetDeleteResponse> delete(
            HttpServletRequest request,
            @PathVariable("assetId") String assetId
    ) {
        LOGGER.info("{}", request.getRequestURL());

        AssetTable assetTable = Tables.ASSET.as("assetTable");

        AssetRecord assetRecord = context.select(assetTable.fields()).from(assetTable).where(assetTable.ASSET_ID.eq(assetId)).fetchOneInto(assetTable);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        File file = new File(repo + "/asset" + assetRecord.getPath() + "/" + assetRecord.getName());
        FileUtils.deleteQuietly(file);
        context.delete(assetTable).where(assetTable.ASSET_ID.eq(assetId)).execute();

        AssetDeleteResponse response = new AssetDeleteResponse();
        return ResponseEntity.ok(response);
    }

}

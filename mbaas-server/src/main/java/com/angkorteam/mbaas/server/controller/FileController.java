package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.FileRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.file.FileCreateRequest;
import com.angkorteam.mbaas.plain.response.file.FileCreateResponse;
import com.angkorteam.mbaas.plain.response.file.FileDeleteResponse;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.google.gson.Gson;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 4/9/16.
 */
@Controller
@RequestMapping(path = "/file")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @RequestMapping(
            path = "/create/{filename}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FileCreateResponse> create(
            HttpServletRequest request,
            HttpServletResponse resp,
            Identity identity,
            @PathVariable("filename") String filename,
            @RequestBody FileCreateRequest requestBody
    ) throws IOException {
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
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.FILE.getName())).fetchOneInto(collectionTable);

        DocumentCreateRequest documentCreateRequest = new DocumentCreateRequest();

        Map<String, Object> fields = requestBody.getAttributes();
        documentCreateRequest.setDocument(fields);
        fields.put(Tables.FILE.PATH.getName(), path);
        fields.put(Tables.FILE.MIME.getName(), mime);
        fields.put(Tables.FILE.EXTENSION.getName(), extension);
        fields.put(Tables.FILE.LENGTH.getName(), length);
        fields.put(Tables.FILE.LABEL.getName(), filename);
        fields.put(Tables.FILE.CLIENT_ID.getName(), identity.getClientId());

        final String uuid = DocumentFunction.insertDocument(context, jdbcTemplate, identity.getUserId(), collectionRecord.getName(), documentCreateRequest);
        String name = uuid + "_" + filename;

        FileTable fileTable = Tables.FILE.as("fileTable");
        context.update(fileTable).set(fileTable.NAME, name).where(fileTable.FILE_ID.eq(uuid)).execute();

        File container = new File(repo + "/file" + fileRepo);
        container.mkdirs();

        try {
            FileUtils.writeByteArrayToFile(new File(container, name), requestBody.getContent());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }

        FileRecord fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(uuid)).fetchOneInto(fileTable);
        StringBuffer address = new StringBuffer();
        address.append(HttpFunction.getHttpAddress(request)).append("/api/resource/file").append(fileRecord.getPath()).append("/").append(fileRecord.getName());

        FileCreateResponse response = new FileCreateResponse();
        response.getData().setContentType(mime);
        response.getData().setFileId(uuid);
        response.getData().setFilename(filename);
        response.getData().setAddress(address.toString());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/delete/{fileId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FileDeleteResponse> delete(
            HttpServletRequest request,
            @PathVariable("fileId") String fileId
    ) {
        LOGGER.info("{}", request.getRequestURL());

        FileTable fileTable = Tables.FILE.as("fileTable");

        FileRecord fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(fileId)).fetchOneInto(fileTable);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        File file = new File(repo + "/file" + fileRecord.getPath() + "/" + fileRecord.getName());
        FileUtils.deleteQuietly(file);
        context.delete(fileTable).where(fileTable.FILE_ID.eq(fileId)).execute();

        FileDeleteResponse response = new FileDeleteResponse();
        return ResponseEntity.ok(response);
    }
}

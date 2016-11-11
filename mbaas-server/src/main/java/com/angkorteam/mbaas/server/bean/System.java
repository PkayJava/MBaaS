package com.angkorteam.mbaas.server.bean;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.records.FileRecord;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class System {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    public System(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    public synchronized String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public String saveFile(File file) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String patternFolder = configuration.getString(Constants.PATTERN_FOLDER);
        String repo = configuration.getString(Constants.RESOURCE_REPO);
        String fileRepo = DateFormatUtils.format(new Date(), patternFolder);
        File container = new File(repo, fileRepo);
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getName()));
        String fileId = randomUUID();
        String name = fileId + "." + extension;
        container.mkdirs();
        try {
            FileUtils.copyFile(file, new File(container, name));
        } catch (Exception e) {
        }

        long length = file.length();
        String path = fileRepo;
        String mime = parseMimeType(file.getName());
        String label = file.getName();

        FileTable fileTable = Tables.FILE.as("fileTable");

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
        return fileId;
    }

    public String parseMimeType(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (StringUtils.equalsIgnoreCase("png", extension)) {
            return "image/png";
        } else if (StringUtils.equalsIgnoreCase("jpg", extension)) {
            return "image/jpg";
        } else if (StringUtils.equalsIgnoreCase("gif", extension)) {
            return "image/gif";
        } else if (StringUtils.equalsIgnoreCase("tiff", extension)) {
            return "image/tiff";
        } else if (StringUtils.equalsIgnoreCase("txt", extension)) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

}
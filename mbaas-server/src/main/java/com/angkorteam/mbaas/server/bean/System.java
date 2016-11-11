package com.angkorteam.mbaas.server.bean;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.records.FileRecord;
import com.angkorteam.mbaas.server.Spring;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.common.Strings;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Date;
import java.util.UUID;

public class System {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final ServletContext servletContext;

    private Configuration configuration;

    private long lastModified = -1;

    public System(DSLContext context, JdbcTemplate jdbcTemplate, ServletContext servletContext) {
        this.context = context;
        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Configuration getConfiguration() {
        String configuration = this.servletContext.getInitParameter("configuration");
        File file;
        if (!Strings.isNullOrEmpty(configuration)) {
            file = new File(configuration);
        } else {
            File home = new File(java.lang.System.getProperty("user.home"));
            file = new File(home, ".xml/" + Configuration.KEY);
        }
        try {
            if (this.configuration == null) {
                this.configuration = new Configuration(file);
            } else {
                if (lastModified != file.lastModified()) {
                    this.configuration = new Configuration(file);
                }
            }
        } catch (ConfigurationException e) {
        }
        return this.configuration;
    }

    public synchronized String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public String saveFile(File file) {
        System system = Spring.getBean(System.class);
        Configuration configuration = system.getConfiguration();

        String patternFolder = configuration.getString(Configuration.PATTERN_FOLDER);
        String repo = configuration.getString(Configuration.RESOURCE_REPO);
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
package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.response.RestResponse;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.gson.Layout;
import com.angkorteam.mbaas.server.gson.Page;
import com.angkorteam.mbaas.server.gson.Rest;
import com.angkorteam.mbaas.server.gson.Sync;
import com.angkorteam.mbaas.server.page.CmsPage;
import com.angkorteam.mbaas.server.page.layout.LayoutCreatePage;
import com.angkorteam.mbaas.server.page.page.PageCreatePage;
import com.angkorteam.mbaas.server.page.rest.RestCreatePage;
import com.angkorteam.mbaas.server.validator.RestPathMethodValidator;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by socheat on 11/3/16.
 */
@Controller
@RequestMapping(path = "/system")
public class SystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    @Qualifier("gson")
    private Gson gson;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext context;

    @Autowired
    private Sql2o sql2o;

    @Autowired
    private GroovyClassLoader classLoader;

    @Autowired
    private System system;

    @RequestMapping(method = RequestMethod.POST, path = "/page")
    public ResponseEntity<RestResponse> page(Authentication authentication, HttpServletRequest request) throws Throwable {
        Page page = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Page.class);

        try {

            if (page == null) {
                throw new IllegalArgumentException("invalid page");
            }
            // PropertyResolver.destroy(org.apache.wicket.Application.get());

            {
                // class name validation
                String className = page.getClassName();
                if (Strings.isNullOrEmpty(className)) {
                    throw new IllegalArgumentException("invalid class name");
                }

                for (int i = 0; i < StringUtils.length(className); i++) {
                    char ch = page.getClassName().charAt(i);
                    if (!Application.CHARACTERS.contains(Character.toLowerCase(ch))) {
                        throw new IllegalArgumentException("invalid class name");
                    }
                }

                int count = context.selectCount().from(Tables.GROOVY).where(Tables.GROOVY.JAVA_CLASS.eq("com.angkorteam.mbaas.server.groovy." + className)).fetchOneInto(int.class);
                if (count > 0) {
                    throw new IllegalArgumentException("invalid class name");
                }
            }

            {
                // path validation
                if (Strings.isNullOrEmpty(page.getPath())) {
                    throw new IllegalArgumentException("invalid path");
                }

                String path = page.getPath();

                if (StringUtils.startsWithIgnoreCase(path, "/api")) {
                    throw new IllegalArgumentException("invalid path");
                }

                if (path.charAt(0) != '/') {
                    throw new IllegalArgumentException("invalid path");
                }

                if (path.length() > 1) {
                    for (int i = 1; i < path.length(); i++) {
                        char ch = path.charAt(i);
                        if (ch == '/' || Application.CHARACTERS.contains(ch) || Application.NUMBERS.contains(ch)) {
                        } else {
                            throw new IllegalArgumentException("invalid path");
                        }
                    }
                }
                if (StringUtils.containsIgnoreCase(path, "//")) {
                    throw new IllegalArgumentException("invalid path");
                }
                DSLContext context = Spring.getBean(DSLContext.class);
                PageTable table = Tables.PAGE.as("table");
                int count = context.selectCount().from(table).where(table.PATH.eq(path)).fetchOneInto(int.class);
                if (count > 0) {
                    throw new IllegalArgumentException("invalid path");
                }
            }

            LayoutPojo layout = null;
            {
                // layout validation
                if (Strings.isNullOrEmpty(page.getLayout())) {
                    throw new IllegalArgumentException("invalid layout");
                }

                layout = context.select(Tables.LAYOUT.fields()).from(Tables.LAYOUT).where(Tables.LAYOUT.TITLE.eq(page.getLayout())).limit(1).fetchOneInto(LayoutPojo.class);
                if (layout == null) {
                    throw new IllegalArgumentException("invalid layout");
                }
            }

            String pageId = this.system.randomUUID();
            Properties configuration = new Properties();
            try (InputStream inputStream = PageCreatePage.class.getResourceAsStream("PageCreatePage.properties.xml")) {
                configuration.loadFromXML(inputStream);
            }
            String pageGroovy = String.format(configuration.getProperty("page.groovy"), page.getClassName(), page.getClassName(), pageId);
            String pageHtml = configuration.getProperty("page.html");

            DSLContext context = Spring.getBean(DSLContext.class);
            System system = Spring.getBean(System.class);
            PageTable pageTable = Tables.PAGE.as("pageTable");
            GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

            File htmlTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".html");
            try {
                FileUtils.write(htmlTemp, pageHtml, "UTF-8");
            } catch (IOException e) {
            }

            long htmlCrc32 = -1;
            try {
                htmlCrc32 = FileUtils.checksumCRC32(htmlTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.deleteQuietly(htmlTemp);

            File groovyTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".groovy");
            try {
                FileUtils.write(groovyTemp, pageGroovy, "UTF-8");
            } catch (IOException e) {
            }

            long groovyCrc32 = -1;
            try {
                groovyCrc32 = FileUtils.checksumCRC32(groovyTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.deleteQuietly(groovyTemp);

            String groovyId = system.randomUUID();

            GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
            GroovyCodeSource source = new GroovyCodeSource(pageGroovy, groovyId, "/groovy/script");
            source.setCachable(true);
            Class<?> pageClass = classLoader.parseClass(source, true);

            GroovyRecord groovyRecord = context.newRecord(groovyTable);
            groovyRecord.setGroovyId(groovyId);
            groovyRecord.setScript(pageGroovy);
            groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
            groovyRecord.setSystem(false);
            groovyRecord.setJavaClass(pageClass.getName());
            groovyRecord.store();

            Application.get().mountPage(page.getPath(), (Class<? extends org.apache.wicket.Page>) pageClass);

            PageRecord pageRecord = context.newRecord(pageTable);
            pageRecord.setPageId(pageId);
            pageRecord.setLayoutId(layout.getLayoutId());
            pageRecord.setDateCreated(new Date());
            pageRecord.setDateModified(new Date());
            pageRecord.setGroovyId(groovyId);
            pageRecord.setTitle(page.getTitle());
            pageRecord.setHtml(pageHtml);
            pageRecord.setHtmlCrc32(String.valueOf(htmlCrc32));
            pageRecord.setCode(page.getCode());
            pageRecord.setPath(page.getPath());
            pageRecord.setDescription(page.getDescription());
            pageRecord.setSystem(false);
            pageRecord.setModified(true);
            pageRecord.setCmsPage(true);
            pageRecord.store();

            RolePojo role = this.context.select(Tables.ROLE.fields()).from(Tables.ROLE).where(Tables.ROLE.NAME.eq("administrator")).fetchOneInto(RolePojo.class);

            PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
            PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
            pageRoleRecord.setPageRoleId(system.randomUUID());
            pageRoleRecord.setRoleId(role.getRoleId());
            pageRoleRecord.setPageId(pageId);
            pageRoleRecord.store();

            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.OK.value());
            response.setResultMessage(HttpStatus.OK.getReasonPhrase());
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResultMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            List<String> stackTraces = Lists.newArrayList();
            for (StackTraceElement element : e.getStackTrace()) {
                String line = element.getClassName() + "." + element.getMethodName() + "(" + FilenameUtils.getName(element.getFileName()) + ":" + element.getLineNumber() + ")";
                stackTraces.add(line);
            }
            response.setStackTrace(stackTraces);
            response.setDebugMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/layout")
    public ResponseEntity<RestResponse> layout(Authentication authentication, HttpServletRequest request) throws Throwable {
        Layout layout = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Layout.class);
        try {

            if (layout == null) {
                throw new IllegalArgumentException("invalid layout");
            }

            {
                // class name validation
                String className = layout.getClassName();
                if (Strings.isNullOrEmpty(className)) {
                    throw new IllegalArgumentException("invalid class name");
                }

                for (int i = 0; i < StringUtils.length(className); i++) {
                    char ch = layout.getClassName().charAt(i);
                    if (!Application.CHARACTERS.contains(Character.toLowerCase(ch))) {
                        throw new IllegalArgumentException("invalid class name");
                    }
                }

                int count = context.selectCount().from(Tables.GROOVY).where(Tables.GROOVY.JAVA_CLASS.eq("com.angkorteam.mbaas.server.groovy." + className)).fetchOneInto(int.class);
                if (count > 0) {
                    throw new IllegalArgumentException("invalid class name");
                }
            }

            Properties configuration = new Properties();
            try (InputStream inputStream = LayoutCreatePage.class.getResourceAsStream("LayoutCreatePage.properties.xml")) {
                configuration.loadFromXML(inputStream);
            }

            String layoutId = system.randomUUID();
            String layoutGroovy = String.format(configuration.getProperty("layout.groovy"), layout.getClassName(), layout.getClassName(), layout.getClassName(), layoutId);
            String layoutHtml = configuration.getProperty("layout.html");

            File htmlTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".html");
            try {
                FileUtils.write(htmlTemp, layoutHtml, "UTF-8");
            } catch (IOException e) {
            }

            long htmlCrc32 = -1;
            try {
                htmlCrc32 = FileUtils.checksumCRC32(htmlTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.deleteQuietly(htmlTemp);

            File groovyTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".groovy");
            try {
                FileUtils.write(groovyTemp, layoutGroovy, "UTF-8");
            } catch (IOException e) {
            }

            long groovyCrc32 = -1;
            try {
                groovyCrc32 = FileUtils.checksumCRC32(groovyTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.deleteQuietly(groovyTemp);

            System system = Spring.getBean(System.class);
            DSLContext context = Spring.getBean(DSLContext.class);
            LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
            GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

            String groovyId = system.randomUUID();

            GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
            GroovyCodeSource source = new GroovyCodeSource(layoutGroovy, groovyId, "/groovy/script");
            source.setCachable(true);
            Class<?> layoutClass = classLoader.parseClass(source, true);

            GroovyRecord groovyRecord = context.newRecord(groovyTable);
            groovyRecord.setGroovyId(groovyId);
            groovyRecord.setSystem(false);
            groovyRecord.setJavaClass(layoutClass.getName());
            groovyRecord.setScript(layoutGroovy);
            groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
            groovyRecord.store();

            LayoutRecord layoutRecord = context.newRecord(layoutTable);
            layoutRecord.setLayoutId(layoutId);
            layoutRecord.setGroovyId(groovyId);
            layoutRecord.setDateCreated(new Date());
            layoutRecord.setDateModified(new Date());
            layoutRecord.setTitle(layout.getTitle());
            layoutRecord.setHtml(layoutHtml);
            layoutRecord.setHtmlCrc32(String.valueOf(htmlCrc32));
            layoutRecord.setDescription(layout.getDescription());
            layoutRecord.setSystem(false);
            layoutRecord.setModified(true);
            layoutRecord.store();

            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.OK.value());
            response.setResultMessage(HttpStatus.OK.getReasonPhrase());
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResultMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            List<String> stackTraces = Lists.newArrayList();
            for (StackTraceElement element : e.getStackTrace()) {
                String line = element.getClassName() + "." + element.getMethodName() + "(" + FilenameUtils.getName(element.getFileName()) + ":" + element.getLineNumber() + ")";
                stackTraces.add(line);
            }
            response.setStackTrace(stackTraces);
            response.setDebugMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest")
    public ResponseEntity<RestResponse> rest(Authentication authentication, HttpServletRequest request) throws Throwable {
        Rest rest = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Rest.class);
        try {

            if (rest == null) {
                throw new IllegalArgumentException("invalid rest");
            }

            {
                // class name validation
                String className = rest.getClassName();
                if (Strings.isNullOrEmpty(className)) {
                    throw new IllegalArgumentException("invalid class name");
                }

                for (int i = 0; i < StringUtils.length(className); i++) {
                    char ch = className.charAt(i);
                    if (!Application.CHARACTERS.contains(Character.toLowerCase(ch))) {
                        throw new IllegalArgumentException("invalid class name");
                    }
                }

                int count = context.selectCount().from(Tables.GROOVY).where(Tables.GROOVY.JAVA_CLASS.eq("com.angkorteam.mbaas.server.groovy." + className)).fetchOneInto(int.class);
                if (count > 0) {
                    throw new IllegalArgumentException("invalid class name");
                }
            }

            {
                String newPath = null;
                String method = rest.getMethod();
                String path = rest.getPath();
                if (StringUtils.equalsIgnoreCase(RequestMethod.GET.name(), rest.getMethod())
                        || StringUtils.equalsIgnoreCase(RequestMethod.POST.name(), rest.getMethod())
                        || StringUtils.equalsIgnoreCase(RequestMethod.PUT.name(), rest.getMethod())
                        || StringUtils.equalsIgnoreCase(RequestMethod.DELETE.name(), rest.getMethod())) {
                } else {
                    throw new IllegalArgumentException("invalid method");
                }

                if (Strings.isNullOrEmpty(rest.getPath())) {
                    throw new IllegalArgumentException("invalid mount path");
                }

                List<String> segmentNames = Lists.newArrayList();
                if (!StringUtils.startsWithIgnoreCase(path, "/")
                        || StringUtils.equalsIgnoreCase(path, "/system")
                        || StringUtils.startsWithIgnoreCase(path, "/system/")
                        || StringUtils.equalsIgnoreCase(path, "/resource")
                        || StringUtils.startsWithIgnoreCase(path, "/resource/")
                        || StringUtils.endsWithIgnoreCase(path, "/")
                        || StringUtils.endsWithIgnoreCase(path, "//")) {
                    throw new IllegalArgumentException("invalid mount path");
                }
                if (path.length() > 1) {
                    for (int i = 1; i < path.length(); i++) {
                        char ch = path.charAt(i);
                        if (ch == '/' || ch == '_' || Application.CURLLY_BRACES.contains(ch) || Application.CHARACTERS.contains(ch) || Application.NUMBERS.contains(ch)) {
                        } else {
                            throw new IllegalArgumentException("invalid mount path");
                        }
                    }
                }
                String[] segments = StringUtils.split(path, "/");
                List<String> newSegments = Lists.newLinkedList();
                for (String segment : segments) {
                    if (!Strings.isNullOrEmpty(segment)) {
                        if (StringUtils.startsWithIgnoreCase(segment, "{") && StringUtils.endsWithIgnoreCase(segment, "}")) {
                            String name = segment.substring(1, segment.length() - 1);
                            if (StringUtils.containsIgnoreCase(name, "{") || StringUtils.containsIgnoreCase(name, "}")) {
                                throw new IllegalArgumentException("invalid mount path");
                            } else {
                                if (segmentNames.contains(name)) {
                                    throw new IllegalArgumentException("invalid mount path");
                                } else {
                                    segmentNames.add(name);
                                }
                            }
                            newSegments.add(RestPathMethodValidator.PATH);
                        } else {
                            if (StringUtils.containsIgnoreCase(segment, "{") || StringUtils.containsIgnoreCase(segment, "}")) {
                                throw new IllegalArgumentException("invalid mount path");
                            }
                            newSegments.add(segment);
                        }
                    }
                }
                newPath = "/" + StringUtils.join(newSegments, "/");
                if (!Strings.isNullOrEmpty(newPath) && !Strings.isNullOrEmpty(method)) {
                    DSLContext context = Spring.getBean(DSLContext.class);
                    RestTable table = Tables.REST.as("table");
                    int count = context.selectCount().from(table).where(table.PATH_VARIABLE.eq(newPath)).and(table.METHOD.eq(method)).fetchOneInto(int.class);
                    if (count > 0) {
                        throw new IllegalArgumentException("invalid mount path");
                    }
                }
            }

            Properties configuration = new Properties();
            try (InputStream inputStream = RestCreatePage.class.getResourceAsStream("RestCreatePage.properties.xml")) {
                configuration.loadFromXML(inputStream);
            }

            DSLContext context = Spring.getBean(DSLContext.class);
            System system = Spring.getBean(System.class);
            RestTable restTable = Tables.REST.as("restTable");
            GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

            String restId = system.randomUUID();

            String restGroovy = String.format(configuration.getProperty("groovy.script"), rest.getClassName(), rest.getClassName(), restId);

            File groovyTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".groovy");
            try {
                FileUtils.write(groovyTemp, restGroovy, "UTF-8");
            } catch (IOException e) {
            }

            long groovyCrc32 = -1;
            try {
                groovyCrc32 = FileUtils.checksumCRC32(groovyTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.deleteQuietly(groovyTemp);

            String groovyId = system.randomUUID();

            GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
            GroovyCodeSource source = new GroovyCodeSource(restGroovy, groovyId, "/groovy/script");
            source.setCachable(true);
            Class<?> serviceClass = classLoader.parseClass(source, true);

            GroovyRecord groovyRecord = context.newRecord(groovyTable);
            groovyRecord.setGroovyId(groovyId);
            groovyRecord.setSystem(false);
            groovyRecord.setJavaClass(serviceClass.getName());
            groovyRecord.setScript(restGroovy);
            groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
            groovyRecord.store();

            String[] segments = StringUtils.split(rest.getPath(), "/");
            List<String> newSegments = Lists.newLinkedList();
            for (String segment : segments) {
                if (!org.elasticsearch.common.Strings.isNullOrEmpty(segment)) {
                    if (StringUtils.startsWithIgnoreCase(segment, "{") && StringUtils.endsWithIgnoreCase(segment, "}")) {
                        newSegments.add(RestPathMethodValidator.PATH);
                    } else {
                        newSegments.add(segment);
                    }
                }
            }

            RestRecord restRecord = context.newRecord(restTable);
            restRecord.setRestId(restId);
            restRecord.setSystem(false);
            restRecord.setName(rest.getName());
            restRecord.setPath(rest.getPath());
            restRecord.setPathVariable("/" + StringUtils.join(newSegments, "/"));
            restRecord.setSegment(StringUtils.countMatches(rest.getPath(), '/'));
            restRecord.setSecurity(SecurityEnum.Granted.getLiteral());
            restRecord.setDescription(rest.getDescription());
            restRecord.setMethod(rest.getMethod());
            restRecord.setGroovyId(groovyId);
            restRecord.store();

            RolePojo role = this.context.select(Tables.ROLE.fields()).from(Tables.ROLE).where(Tables.ROLE.NAME.eq("service")).fetchOneInto(RolePojo.class);

            RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
            RestRoleRecord restRoleRecord = context.newRecord(restRoleTable);
            restRoleRecord.setRestRoleId(system.randomUUID());
            restRoleRecord.setRoleId(role.getRoleId());
            restRoleRecord.setRestId(restId);
            restRoleRecord.store();

            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.OK.value());
            response.setResultMessage(HttpStatus.OK.getReasonPhrase());
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            RestResponse response = new RestResponse();
            response.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResultMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            List<String> stackTraces = Lists.newArrayList();
            for (StackTraceElement element : e.getStackTrace()) {
                String line = element.getClassName() + "." + element.getMethodName() + "(" + FilenameUtils.getName(element.getFileName()) + ":" + element.getLineNumber() + ")";
                stackTraces.add(line);
            }
            response.setStackTrace(stackTraces);
            response.setDebugMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/monitor")
    public ResponseEntity<RestResponse> monitor(Authentication authentication, HttpServletRequest request) throws Throwable {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("time", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        RestResponse response = new RestResponse();
        response.setResultCode(HttpStatus.OK.value());
        response.setResultMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(monitor);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sync")
    public ResponseEntity<RestResponse> sync(Authentication authentication, HttpServletRequest request) throws Throwable {
        Sync sync = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Sync.class);
        if (sync == null) {
            sync = new Sync();
        }
        // PropertyResolver.destroy(org.apache.wicket.Application.get());
        List<String> pageIds = new ArrayList<>();
        List<String> restIds = new ArrayList<>();
        List<String> layoutIds = new ArrayList<>();
        try (Connection connection = sql2o.open()) {
            if (sync.getPages() != null && !sync.getPages().isEmpty()) {
                for (Page clientPage : sync.getPages()) {
                    Query query = connection.createQuery("select groovy.java_class as javaClass, page.path as mountPath, groovy.groovy_id as groovyId, page.page_id as pageId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from page inner join groovy on page.groovy_id = groovy.groovy_id where page.page_id = :pageId");
                    query.addParameter("pageId", clientPage.getPageId());
                    Page serverPage = query.executeAndFetchFirst(Page.class);
                    boolean groovyConflicted = !clientPage.getServerGroovyCrc32().equals(serverPage.getServerGroovyCrc32());
                    boolean htmlConflicted = !clientPage.getServerHtmlCrc32().equals(serverPage.getServerHtmlCrc32());
                    pageIds.add(clientPage.getPageId());
                    clientPage.setGroovyConflicted(groovyConflicted);
                    clientPage.setHtmlConflicted(htmlConflicted);
                    String path = StringUtils.replaceChars(serverPage.getJavaClass(), '.', '/');
                    clientPage.setHtmlPath(path + ".html");
                    clientPage.setGroovyPath(path + ".groovy");
                    if (!groovyConflicted && !htmlConflicted && Strings.isNullOrEmpty(clientPage.getClientGroovyCrc32()) && Strings.isNullOrEmpty(clientPage.getClientHtmlCrc32())) {
                        // delete command
                        Application.get().unmount(serverPage.getMountPath());
                        Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                        clientPage.setServerGroovyCrc32(null);
                        clientPage.setServerGroovy(null);
                        clientPage.setServerHtmlCrc32(null);
                        clientPage.setServerHtml(null);
                        classLoader.removeSourceCache(serverPage.getJavaClass());
                        classLoader.removeClassCache(serverPage.getJavaClass());
                        connection.createQuery("delete from page where page_id = :page_id").addParameter("page_id", serverPage.getPageId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverPage.getGroovyId()).executeUpdate();
                    } else {
                        if (!groovyConflicted) {
                            // update command

                            String groovyScript = Strings.isNullOrEmpty(clientPage.getClientGroovy()) ? serverPage.getServerGroovy() : clientPage.getClientGroovy();
                            GroovyCodeSource source = new GroovyCodeSource(groovyScript, serverPage.getGroovyId(), "/groovy/script");
                            source.setCachable(false);
                            Class<?> pageClass = classLoader.parseClass(source, false);
                            String javaClass = pageClass.getName();

                            classLoader.removeSourceCache(serverPage.getJavaClass());
                            classLoader.removeClassCache(serverPage.getJavaClass());

                            classLoader.writeGroovy(javaClass, groovyScript);
                            pageClass = classLoader.compileGroovy(javaClass);

                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", groovyScript)
                                    .addParameter("script_crc32", clientPage.getClientGroovyCrc32())
                                    .addParameter("java_class", pageClass.getName())
                                    .addParameter("groovy_id", serverPage.getGroovyId())
                                    .executeUpdate();
                            clientPage.setServerGroovyCrc32(clientPage.getClientGroovyCrc32());
                            clientPage.setServerGroovy(groovyScript);
                        } else {
                            clientPage.setServerGroovyCrc32(serverPage.getServerGroovyCrc32());
                            clientPage.setServerGroovy(serverPage.getServerGroovy());
                            if (Strings.isNullOrEmpty(clientPage.getClientGroovy()) && Strings.isNullOrEmpty(clientPage.getClientGroovyCrc32())) {
                                clientPage.setGroovyConflicted(false);
                            }
                        }

                        if (!htmlConflicted) {
                            // update command
                            connection.createQuery("update page set html = :html, html_crc32 = :html_crc32 where page_id = :page_id")
                                    .addParameter("html", Strings.isNullOrEmpty(clientPage.getClientHtml()) ? serverPage.getServerHtml() : clientPage.getClientHtml())
                                    .addParameter("html_crc32", clientPage.getClientHtmlCrc32())
                                    .addParameter("page_id", serverPage.getPageId())
                                    .executeUpdate();
                            Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                            clientPage.setServerHtmlCrc32(clientPage.getClientHtmlCrc32());
                            clientPage.setServerHtml(Strings.isNullOrEmpty(clientPage.getClientHtml()) ? serverPage.getServerHtml() : clientPage.getClientHtml());
                        } else {
                            clientPage.setServerHtmlCrc32(serverPage.getServerHtmlCrc32());
                            clientPage.setServerHtml(serverPage.getServerHtml());
                        }
                    }
                }
            }
            if (sync.getRests() != null && !sync.getRests().isEmpty()) {
                for (Rest clientRest : sync.getRests()) {
                    restIds.add(clientRest.getRestId());
                    Query query = connection.createQuery("select groovy.java_class as javaClass, groovy.groovy_id as groovyId, rest.rest_id as restId, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from rest inner join groovy on rest.groovy_id = groovy.groovy_id where rest.rest_id = :restId");
                    query.addParameter("restId", clientRest.getRestId());
                    Rest serverRest = query.executeAndFetchFirst(Rest.class);
                    boolean groovyConflicted = !clientRest.getServerGroovyCrc32().equals(serverRest.getServerGroovyCrc32());
                    clientRest.setGroovyConflicted(groovyConflicted);
                    String path = StringUtils.replaceChars(serverRest.getJavaClass(), '.', '/');
                    clientRest.setGroovyPath(path + ".groovy");
                    if (!groovyConflicted && Strings.isNullOrEmpty(clientRest.getClientGroovyCrc32())) {
                        // delete command
                        clientRest.setServerGroovy(null);
                        clientRest.setServerGroovyCrc32(null);
                        classLoader.removeSourceCache(serverRest.getJavaClass());
                        classLoader.removeClassCache(serverRest.getJavaClass());
                        connection.createQuery("delete from rest where rest_id = :rest_id").addParameter("rest_id", serverRest.getRestId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverRest.getGroovyId()).executeUpdate();
                    } else {
                        if (!groovyConflicted) {
                            // update command
                            String groovyScript = Strings.isNullOrEmpty(clientRest.getClientGroovy()) ? serverRest.getServerGroovy() : clientRest.getClientGroovy();
                            GroovyCodeSource source = new GroovyCodeSource(groovyScript, serverRest.getGroovyId(), "/groovy/script");
                            source.setCachable(false);
                            Class<?> serviceClass = classLoader.parseClass(source, false);
                            String javaClass = serviceClass.getName();

                            classLoader.removeSourceCache(serverRest.getJavaClass());
                            classLoader.removeClassCache(serverRest.getJavaClass());

                            classLoader.writeGroovy(javaClass, groovyScript);
                            classLoader.compileGroovy(javaClass);

                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", groovyScript)
                                    .addParameter("script_crc32", clientRest.getClientGroovyCrc32())
                                    .addParameter("java_class", serviceClass.getName())
                                    .addParameter("groovy_id", serverRest.getGroovyId())
                                    .executeUpdate();
                            clientRest.setServerGroovyCrc32(clientRest.getClientGroovyCrc32());
                            clientRest.setServerGroovy(groovyScript);
                        } else {
                            clientRest.setServerGroovyCrc32(serverRest.getServerGroovyCrc32());
                            clientRest.setServerGroovy(serverRest.getServerGroovy());
                        }
                    }
                }
            }
            if (sync.getLayouts() != null && !sync.getLayouts().isEmpty()) {
                for (Layout clientLayout : sync.getLayouts()) {
                    Query query = connection.createQuery("select groovy.java_class as javaClass, groovy.groovy_id as groovyId, layout.layout_id as layoutId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from layout inner join groovy on layout.groovy_id = groovy.groovy_id where layout.layout_id = :layoutId");
                    query.addParameter("layoutId", clientLayout.getLayoutId());
                    Layout serverLayout = query.executeAndFetchFirst(Layout.class);
                    boolean groovyConflicted = !clientLayout.getServerGroovyCrc32().equals(serverLayout.getServerGroovyCrc32());
                    boolean htmlConflicted = !clientLayout.getServerHtmlCrc32().equals(serverLayout.getServerHtmlCrc32());
                    layoutIds.add(clientLayout.getLayoutId());
                    clientLayout.setGroovyConflicted(groovyConflicted);
                    clientLayout.setHtmlConflicted(htmlConflicted);
                    String path = StringUtils.replaceChars(serverLayout.getJavaClass(), '.', '/');
                    clientLayout.setHtmlPath(path + ".html");
                    clientLayout.setGroovyPath(path + ".groovy");
                    if (!groovyConflicted && !htmlConflicted && Strings.isNullOrEmpty(clientLayout.getClientGroovyCrc32()) && Strings.isNullOrEmpty(clientLayout.getClientHtmlCrc32())) {
                        // delete command
                        clientLayout.setServerGroovyCrc32(null);
                        clientLayout.setServerGroovy(null);
                        clientLayout.setServerHtmlCrc32(null);
                        clientLayout.setServerHtml(null);
                        classLoader.removeSourceCache(serverLayout.getJavaClass());
                        classLoader.removeClassCache(serverLayout.getJavaClass());
                        connection.createQuery("delete from layout where layout_id = :layout_id").addParameter("layout_id", serverLayout.getLayoutId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverLayout.getGroovyId()).executeUpdate();
                        Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                    } else {
                        if (!groovyConflicted) {
                            // update command
                            String groovyScript = Strings.isNullOrEmpty(clientLayout.getClientGroovy()) ? serverLayout.getServerGroovy() : clientLayout.getClientGroovy();
                            GroovyCodeSource source = new GroovyCodeSource(groovyScript, serverLayout.getGroovyId(), "/groovy/script");
                            source.setCachable(false);
                            Class<?> pageClass = classLoader.parseClass(source, false);
                            String javaClass = pageClass.getName();

                            classLoader.removeSourceCache(serverLayout.getJavaClass());
                            classLoader.removeClassCache(serverLayout.getJavaClass());

                            classLoader.writeGroovy(javaClass, groovyScript);
                            classLoader.compileGroovy(javaClass);

                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", groovyScript)
                                    .addParameter("script_crc32", clientLayout.getClientGroovyCrc32())
                                    .addParameter("java_class", pageClass.getName())
                                    .addParameter("groovy_id", serverLayout.getGroovyId())
                                    .executeUpdate();
                            clientLayout.setServerGroovyCrc32(clientLayout.getClientGroovyCrc32());
                            clientLayout.setServerGroovy(groovyScript);
                        } else {
                            clientLayout.setServerGroovyCrc32(serverLayout.getServerGroovyCrc32());
                            clientLayout.setServerGroovy(serverLayout.getServerGroovy());
                            if (Strings.isNullOrEmpty(clientLayout.getClientGroovy()) && Strings.isNullOrEmpty(clientLayout.getClientGroovyCrc32())) {
                                clientLayout.setGroovyConflicted(false);
                            }
                        }

                        if (!htmlConflicted) {
                            // update command
                            connection.createQuery("update layout set html = :html, html_crc32 = :html_crc32 where layout_id = :layout_id")
                                    .addParameter("html", Strings.isNullOrEmpty(clientLayout.getClientHtml()) ? serverLayout.getServerHtml() : clientLayout.getClientHtml())
                                    .addParameter("html_crc32", clientLayout.getClientHtmlCrc32())
                                    .addParameter("layout_id", serverLayout.getLayoutId())
                                    .executeUpdate();
                            Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                            clientLayout.setServerHtmlCrc32(clientLayout.getClientHtmlCrc32());
                            clientLayout.setServerHtml(Strings.isNullOrEmpty(clientLayout.getClientHtml()) ? serverLayout.getServerHtml() : clientLayout.getClientHtml());
                        } else {
                            clientLayout.setServerHtmlCrc32(serverLayout.getServerHtmlCrc32());
                            clientLayout.setServerHtml(serverLayout.getServerHtml());
                        }
                    }
                }
            }

            List<Layout> serverLayouts;
            if (!layoutIds.isEmpty()) {
                Query query = connection.createQuery("select groovy.java_class as javaClass, layout.layout_id as layoutId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from layout inner join groovy on layout.groovy_id = groovy.groovy_id where layout.system = false and layout.layout_id not in (:layoutId)");
                query.addParameter("layoutId", layoutIds);
                serverLayouts = query.executeAndFetch(Layout.class);
            } else {
                Query query = connection.createQuery("select groovy.java_class as javaClass, layout.layout_id as layoutId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from layout inner join groovy on layout.groovy_id = groovy.groovy_id where layout.system = false");
                serverLayouts = query.executeAndFetch(Layout.class);
            }
            if (serverLayouts != null && !serverLayouts.isEmpty()) {
                for (Layout serverLayout : serverLayouts) {
                    String path = StringUtils.replaceChars(serverLayout.getJavaClass(), '.', '/');
                    serverLayout.setHtmlPath(path + ".html");
                    serverLayout.setGroovyPath(path + ".groovy");
                    serverLayout.setGroovyConflicted(false);
                    serverLayout.setHtmlConflicted(false);
                    sync.addLayout(serverLayout);
                }
            }

            List<Page> serverPages;
            if (!pageIds.isEmpty()) {
                Query query = connection.createQuery("select groovy.java_class as javaClass, page.page_id as pageId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from page inner join groovy on page.groovy_id = groovy.groovy_id where page.system = false and page.page_id not in (:pageId)");
                query.addParameter("pageId", pageIds);
                serverPages = query.executeAndFetch(Page.class);
            } else {
                Query query = connection.createQuery("select groovy.java_class as javaClass, page.page_id as pageId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from page inner join groovy on page.groovy_id = groovy.groovy_id where page.system = false");
                serverPages = query.executeAndFetch(Page.class);
            }
            if (serverPages != null && !serverPages.isEmpty()) {
                for (Page serverPage : serverPages) {
                    String path = StringUtils.replaceChars(serverPage.getJavaClass(), '.', '/');
                    serverPage.setHtmlPath(path + ".html");
                    serverPage.setGroovyPath(path + ".groovy");
                    serverPage.setGroovyConflicted(false);
                    serverPage.setHtmlConflicted(false);
                    sync.addPage(serverPage);
                }
            }

            List<Rest> serverRests;
            if (!restIds.isEmpty()) {
                Query query = connection.createQuery("select groovy.java_class as javaClass, rest.rest_id as restId, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from rest inner join groovy on rest.groovy_id = groovy.groovy_id where rest.system = false and rest.rest_id not in (:restId)");
                query.addParameter("restId", restIds);
                serverRests = query.executeAndFetch(Rest.class);
            } else {
                Query query = connection.createQuery("select groovy.java_class as javaClass, rest.rest_id as restId, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from rest inner join groovy on rest.groovy_id = groovy.groovy_id where rest.system = false");
                serverRests = query.executeAndFetch(Rest.class);
            }
            if (serverRests != null && !serverRests.isEmpty()) {
                for (Rest restPage : serverRests) {
                    String path = StringUtils.replaceChars(restPage.getJavaClass(), '.', '/');
                    restPage.setGroovyPath(path + ".groovy");
                    restPage.setGroovyConflicted(false);
                    sync.addRest(restPage);
                }
            }
        }

        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        List<Map<String, Object>> groovys = jdbcTemplate.queryForList("SELECT * FROM groovy WHERE script_crc32 IS NULL");
        for (Map<String, Object> groovy : groovys) {
            String javaClass = (String) groovy.get("java_class");
            String groovyId = (String) groovy.get("groovy_id");
            try {
                Class<?> clazz = classLoader.loadClass(javaClass);
                if (CmsPage.class.isAssignableFrom(clazz) || clazz.isAssignableFrom(CmsPage.class)) {
                    try {
                        String path = jdbcTemplate.queryForObject("SELECT path FROM page WHERE groovy_id = ?", String.class, groovyId);
                        if (!Strings.isNullOrEmpty(path)) {
                            Application.get().mountPage(path, (Class<? extends org.apache.wicket.Page>) clazz);
                        }
                    } catch (EmptyResultDataAccessException e) {
                        LOGGER.info(e.getMessage(), e);
                    }
                }
            } catch (ClassNotFoundException e) {
                LOGGER.info(e.getMessage(), e);
            }
        }
        RestResponse response = new RestResponse();
        response.setData(sync);
        response.setResultCode(HttpStatus.OK.value());
        response.setResultMessage(HttpStatus.OK.getReasonPhrase());
        return ResponseEntity.ok(response);
    }

}

package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.PageRoleTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.GroovyRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PageRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PageRoleRecord;
import com.angkorteam.mbaas.plain.response.RestResponse;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.gson.Layout;
import com.angkorteam.mbaas.server.gson.Page;
import com.angkorteam.mbaas.server.gson.Rest;
import com.angkorteam.mbaas.server.gson.Sync;
import com.angkorteam.mbaas.server.page.page.PageCreatePage;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
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

    @RequestMapping(method = RequestMethod.POST, path = "/system/page")
    public ResponseEntity<RestResponse> page(Authentication authentication, HttpServletRequest request) throws Throwable {

        Page page = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Page.class);

        try {

            if (page == null) {
                throw new IllegalArgumentException("invalid page");
            }

            {
                // class name validation
                String clazz = page.getClazz();
                if (Strings.isNullOrEmpty(clazz)) {
                    throw new IllegalArgumentException("invalid class name");
                }

                for (int i = 0; i < StringUtils.length(clazz); i++) {
                    char ch = page.getClazz().charAt(i);
                    if (!Application.CHARACTERS.contains(Character.toLowerCase(ch))) {
                        throw new IllegalArgumentException("invalid class name");
                    }
                }

                int count = context.selectCount().from(Tables.GROOVY).where(Tables.GROOVY.JAVA_CLASS.eq("com.angkorteam.mbaas.server.groovy." + clazz)).fetchOneInto(int.class);
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
            XMLPropertiesConfiguration configuration = new XMLPropertiesConfiguration();
            try (InputStream inputStream = PageCreatePage.class.getResourceAsStream("PageCreatePage.properties.xml")) {
                configuration.load(inputStream);
                String pageGroovy = String.format(configuration.getString("page.groovy"), page.getClazz(), pageId);
                String pageHtml = configuration.getString("page.html");

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
            }
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

    @RequestMapping(method = RequestMethod.POST, path = "/system/layout")
    public ResponseEntity<RestResponse> layout(Authentication authentication, HttpServletRequest request) throws Throwable {
        Layout layout = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Layout.class);
        try {

            if (layout == null) {
                throw new IllegalArgumentException("invalid layout");
            }

            {
                // class name validation
                String clazz = layout.getClassName();
                if (Strings.isNullOrEmpty(clazz)) {
                    throw new IllegalArgumentException("invalid class name");
                }

                for (int i = 0; i < StringUtils.length(clazz); i++) {
                    char ch = layout.getClassName().charAt(i);
                    if (!Application.CHARACTERS.contains(Character.toLowerCase(ch))) {
                        throw new IllegalArgumentException("invalid class name");
                    }
                }

                int count = context.selectCount().from(Tables.GROOVY).where(Tables.GROOVY.JAVA_CLASS.eq("com.angkorteam.mbaas.server.groovy." + clazz)).fetchOneInto(int.class);
                if (count > 0) {
                    throw new IllegalArgumentException("invalid class name");
                }
            }

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

    @RequestMapping(method = RequestMethod.POST, path = "/system/rest")
    public ResponseEntity<RestResponse> rest(Authentication authentication, HttpServletRequest request) throws Throwable {
        Rest rest = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Rest.class);
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/system/monitor")
    public ResponseEntity<RestResponse> monitor(Authentication authentication, HttpServletRequest request) throws Throwable {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("time", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        RestResponse response = new RestResponse();
        response.setResultCode(HttpStatus.OK.value());
        response.setResultMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(monitor);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/system/sync")
    public ResponseEntity<RestResponse> sync(Authentication authentication, HttpServletRequest request) throws Throwable {
        Sync sync = this.gson.fromJson(new InputStreamReader(request.getInputStream()), Sync.class);
        if (sync == null) {
            sync = new Sync();
        }
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
                        clientPage.setServerGroovyCrc32(null);
                        clientPage.setServerGroovy(null);
                        clientPage.setServerHtmlCrc32(null);
                        clientPage.setServerHtml(null);
                        classLoader.removeSourceCache(serverPage.getGroovyId());
                        classLoader.removeClassCache(serverPage.getJavaClass());
                        connection.createQuery("delete from page where page_id = :page_id").addParameter("page_id", serverPage.getPageId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverPage.getGroovyId()).executeUpdate();
                        Application.get().unmount(serverPage.getMountPath());
                        Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                    } else {
                        if (!groovyConflicted) {
                            // update command
                            classLoader.removeSourceCache(serverPage.getGroovyId());
                            classLoader.removeClassCache(serverPage.getJavaClass());
                            GroovyCodeSource source = new GroovyCodeSource(Strings.isNullOrEmpty(clientPage.getClientGroovy()) ? serverPage.getServerGroovy() : clientPage.getClientGroovy(), serverPage.getGroovyId(), "/groovy/script");
                            source.setCachable(true);
                            Class<?> pageClass = classLoader.parseClass(source, true);
                            Application.get().mountPage(serverPage.getMountPath(), (Class<? extends org.apache.wicket.Page>) pageClass);
                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", Strings.isNullOrEmpty(clientPage.getClientGroovy()) ? serverPage.getServerGroovy() : clientPage.getClientGroovy())
                                    .addParameter("script_crc32", clientPage.getClientGroovyCrc32())
                                    .addParameter("java_class", pageClass.getName())
                                    .addParameter("groovy_id", serverPage.getGroovyId())
                                    .executeUpdate();
                            clientPage.setServerGroovyCrc32(clientPage.getClientGroovyCrc32());
                            clientPage.setServerGroovy(Strings.isNullOrEmpty(clientPage.getClientGroovy()) ? serverPage.getServerGroovy() : clientPage.getClientGroovy());
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
                        classLoader.removeSourceCache(serverRest.getGroovyId());
                        classLoader.removeClassCache(serverRest.getJavaClass());
                        connection.createQuery("delete from rest where rest_id = :rest_id").addParameter("rest_id", serverRest.getRestId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverRest.getGroovyId()).executeUpdate();
                    } else {
                        if (!groovyConflicted) {
                            // update command
                            classLoader.removeSourceCache(serverRest.getGroovyId());
                            classLoader.removeClassCache(serverRest.getJavaClass());
                            GroovyCodeSource source = new GroovyCodeSource(Strings.isNullOrEmpty(clientRest.getClientGroovy()) ? serverRest.getServerGroovy() : clientRest.getClientGroovy(), serverRest.getGroovyId(), "/groovy/script");
                            source.setCachable(true);
                            Class<?> serviceClass = classLoader.parseClass(source, true);
                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", Strings.isNullOrEmpty(clientRest.getClientGroovy()) ? serverRest.getServerGroovy() : clientRest.getClientGroovy())
                                    .addParameter("script_crc32", clientRest.getClientGroovyCrc32())
                                    .addParameter("java_class", serviceClass.getName())
                                    .addParameter("groovy_id", serverRest.getGroovyId())
                                    .executeUpdate();
                            clientRest.setServerGroovyCrc32(clientRest.getClientGroovyCrc32());
                            clientRest.setServerGroovy(Strings.isNullOrEmpty(clientRest.getClientGroovy()) ? serverRest.getServerGroovy() : clientRest.getClientGroovy());
                        } else {
                            clientRest.setServerGroovyCrc32(serverRest.getServerGroovyCrc32());
                            clientRest.setServerGroovy(serverRest.getServerGroovy());
                        }
                    }
                }
            }
            if (sync.getLayouts() != null && !sync.getLayouts().isEmpty()) {
                for (Layout clientLayout : sync.getLayouts()) {
                    Query query = connection.createQuery("select groovy.java_class as javaClass, page.path as mountPath, groovy.groovy_id as groovyId, layout.layout_id as layoutId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from layout inner join groovy on layout.groovy_id = groovy.groovy_id where layout.layout_id = :layoutId");
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
                        classLoader.removeSourceCache(serverLayout.getGroovyId());
                        classLoader.removeClassCache(serverLayout.getJavaClass());
                        connection.createQuery("delete from layout where layout_id = :layout_id").addParameter("layout_id", serverLayout.getLayoutId()).executeUpdate();
                        connection.createQuery("delete from groovy where groovy_id = :groovy_id").addParameter("groovy_id", serverLayout.getGroovyId()).executeUpdate();
                        Application.get().unmount(serverLayout.getMountPath());
                        Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
                    } else {
                        if (!groovyConflicted) {
                            // update command
                            classLoader.removeSourceCache(serverLayout.getGroovyId());
                            classLoader.removeClassCache(serverLayout.getJavaClass());
                            GroovyCodeSource source = new GroovyCodeSource(Strings.isNullOrEmpty(clientLayout.getClientGroovy()) ? serverLayout.getServerGroovy() : clientLayout.getClientGroovy(), serverLayout.getGroovyId(), "/groovy/script");
                            source.setCachable(true);
                            Class<?> pageClass = classLoader.parseClass(source, true);
                            Application.get().mountPage(serverLayout.getMountPath(), (Class<? extends org.apache.wicket.Page>) pageClass);
                            connection.createQuery("update groovy set script = :script, script_crc32 = :script_crc32, java_class = :java_class where groovy_id = :groovy_id")
                                    .addParameter("script", Strings.isNullOrEmpty(clientLayout.getClientGroovy()) ? serverLayout.getServerGroovy() : clientLayout.getClientGroovy())
                                    .addParameter("script_crc32", clientLayout.getClientGroovyCrc32())
                                    .addParameter("java_class", pageClass.getName())
                                    .addParameter("groovy_id", serverLayout.getGroovyId())
                                    .executeUpdate();
                            clientLayout.setServerGroovyCrc32(clientLayout.getClientGroovyCrc32());
                            clientLayout.setServerGroovy(Strings.isNullOrEmpty(clientLayout.getClientGroovy()) ? serverLayout.getServerGroovy() : clientLayout.getClientGroovy());
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
        RestResponse response = new RestResponse();
        response.setData(sync);
        response.setResultCode(HttpStatus.OK.value());
        response.setResultMessage(HttpStatus.OK.getReasonPhrase());
        return ResponseEntity.ok(response);
    }

}

package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.plain.response.RestResponse;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.gson.Page;
import com.angkorteam.mbaas.server.gson.Rest;
import com.angkorteam.mbaas.server.gson.Sync;
import com.google.gson.Gson;
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
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
    private GroovyClassLoader classLoader;

    @Autowired
    private Sql2o sql2o;

    @RequestMapping(path = "/system/monitor")
    public ResponseEntity<RestResponse> monitor(Authentication authentication, HttpServletRequest request) throws Throwable {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("time", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        RestResponse response = new RestResponse();
        response.setResultCode(HttpStatus.OK.value());
        response.setResultMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(monitor);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/system/sync")
    public ResponseEntity<RestResponse> sync(Authentication authentication, HttpServletRequest request) throws Throwable {
        String json = org.apache.commons.io.IOUtils.toString(request.getInputStream(), "UTF-8");
        Sync sync = this.gson.fromJson(json, Sync.class);
        if (sync == null) {
            sync = new Sync();
        }
        List<String> pageIds = new ArrayList<>();
        List<String> restIds = new ArrayList<>();
        try (Connection connection = sql2o.open()) {
            if (sync.getPages() != null && !sync.getPages().isEmpty()) {
                for (Page clientPage : sync.getPages()) {
                    pageIds.add(clientPage.getPageId());
                    Query query = connection.createQuery("select page.page_id as pageId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from page inner join groovy on page.groovy_id = groovy.groovy_id where pageId = :pageId");
                    query.addParameter("pageId", clientPage.getPageId());
                    Page serverPage = query.executeAndFetchFirst(Page.class);
                    boolean groovyConflicted = !clientPage.getServerGroovyCrc32().equals(serverPage.getServerGroovyCrc32());
                    clientPage.setGroovyConflicted(groovyConflicted);
                    clientPage.setServerGroovyCrc32(serverPage.getServerGroovyCrc32());
                    clientPage.setServerGroovy(serverPage.getServerGroovy());
                    if (!groovyConflicted) {
                        // update page groovy
                    }
                    boolean htmlConflicted = !clientPage.getServerHtmlCrc32().equals(serverPage.getServerHtmlCrc32());
                    clientPage.setHtmlConflicted(htmlConflicted);
                    clientPage.setServerHtmlCrc32(serverPage.getClientHtmlCrc32());
                    clientPage.setServerHtml(serverPage.getServerHtml());
                    if (!htmlConflicted) {
                        // update page html
                    }
                }
            }
            if (sync.getRests() != null && !sync.getRests().isEmpty()) {
                for (Rest clientRest : sync.getRests()) {
                    restIds.add(clientRest.getRestId());
                    Query query = connection.createQuery("select rest.rest_id as restId, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from rest inner join groovy on rest.groovy_id = groovy.groovy_id where restId = :restId");
                    query.addParameter("restId", clientRest.getRestId());
                    Rest serverRest = query.executeAndFetchFirst(Rest.class);
                    boolean groovyConflicted = !clientRest.getServerGroovyCrc32().equals(serverRest.getServerGroovyCrc32());
                    clientRest.setGroovyConflicted(groovyConflicted);
                    clientRest.setServerGroovyCrc32(serverRest.getServerGroovyCrc32());
                    clientRest.setServerGroovy(serverRest.getServerGroovy());
                    if (!groovyConflicted) {
                        // update rest groovy
                    }
                }
            }

            List<Page> serverPages;
            if (!pageIds.isEmpty()) {
                Query query = connection.createQuery("select groovy.java_class as javaClass, page.page_id as pageId, html as serverHtml, html_crc32 as serverHtmlCrc32, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from page inner join groovy on page.groovy_id = groovy.groovy_id where page.system = false and pageId not in (:pageId)");
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
                Query query = connection.createQuery("select groovy.java_class as javaClass, rest.rest_id as restId, groovy.script as serverGroovy, groovy.script_crc32 as serverGroovyCrc32 from rest inner join groovy on rest.groovy_id = groovy.groovy_id where rest.system = false and restId not in (:restId)");
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

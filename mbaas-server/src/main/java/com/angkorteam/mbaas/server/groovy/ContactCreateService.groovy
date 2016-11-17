//package com.angkorteam.mbaas.server.groovy
//
//import com.angkorteam.mbaas.plain.response.RestResponse
//import com.angkorteam.mbaas.server.bean.System
//import groovy.sql.Sql
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.ResponseEntity
//import com.angkorteam.mbaas.server.spring.RestService
//import org.springframework.jdbc.core.JdbcTemplate
//import org.slf4j.Logger
//import org.sql2o.Connection
//import org.sql2o.Sql2o
//import org.sql2o.StatementRunnable
//
//import javax.servlet.ServletContext
//import com.google.common.collect.Lists
//import com.google.gson.Gson
//import org.apache.commons.io.IOUtils
//import org.apache.commons.io.FileUtils
//import org.apache.commons.io.FilenameUtils
//import org.apache.commons.io.FileSystemUtils
//import org.apache.commons.lang3.StringUtils
//import org.apache.commons.lang3.RandomStringUtils
//import org.apache.commons.lang3.RandomUtils
//import org.apache.commons.lang3.StringEscapeUtils
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import javax.servlet.http.HttpServletRequest
//import javax.sql.DataSource
//
//class ContactCreateService implements RestService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ContactCreateService.class);
//
//    @Autowired
//    private System system
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate
//
//    @Autowired
//    @Qualifier("gson")
//    private Gson gson
//
//    @Autowired
//    private DataSource dataSource
//
//    @Autowired
//    private ServletContext servletContext;
//
//    @Override
//    ResponseEntity<RestResponse> service(HttpServletRequest request) throws Throwable {
//        // enjoy your logic here
//        Sql2o o = new Sql2o(dataSource);
//
//        o.runInTransaction(new StatementRunnable() {
//            @Override
//            void run(Connection connection, Object argument) throws Throwable {
//
//            }
//        })
//
//        String json = IOUtils.toString(request.getInputStream(), "UTF-8");
//        Map<String, Object> contact = this.gson.fromJson(json, Map.class);
//        String login = contact.get("login");
//        String password = contact.get("password");
//        String bannderFileId = contact.get("bannerFileId");
//        String name = contact.get("name");
//        String profilePictureFileId = contact.get("profilePictureFileId");
//
//        RestResponse response = new RestResponse()
//        response.setData(system.randomUUID())
//        return ResponseEntity.ok(response)
//
//        login	String	false	false	true	255	-1	0	Delete
//        password	String	false	false	true	255	-1	0	Delete
//        banner_file_id	String	false	false	true	100	-1	0	Delete
//        contact_id	String	false	true	false	100	0	1
//        system	Boolean	false	true	false	1	0	0
//        last_modified	DateTime	false	false	true	-1	-1	0	Delete
//        name	String	false	false	true	255	-1	0	Delete
//        profile_picture_file_id	String	false	false	true	100	-1	0	Delete
//        description	String
//
//    }
//
//    @Override
//    public final String getRestUUID() {
//        // DO NOT MODIFIED
//        return "ace8a198-c585-4dd2-a2b8-c18fc968b083"
//    }
//
//}
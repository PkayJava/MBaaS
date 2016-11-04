package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
import com.angkorteam.mbaas.plain.response.GroovyResponse;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.spring.GroovyService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 11/3/16.
 */
@Controller
public class GroovyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyController.class);

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

    @RequestMapping(path = "/**")
    public ResponseEntity<GroovyResponse> execute(HttpServletRequest request) throws Throwable {
        String method = StringUtils.upperCase(request.getMethod());
        RestTable restTable = Tables.REST.as("restTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");
        RestPojo restPojo = this.context.select(restTable.fields()).from(restTable).where(restTable.PATH.eq(request.getPathInfo())).and(restTable.METHOD.eq(method)).fetchOneInto(RestPojo.class);
        GroovyPojo groovyPojo = null;
        if (restPojo != null) {
            groovyPojo = this.context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(restPojo.getGroovyId())).fetchOneInto(GroovyPojo.class);
        }
        if (groovyPojo != null) {
            Class<?> clazz = classLoader.loadClass(groovyPojo.getJavaClass());
            GroovyService service = (GroovyService) clazz.newInstance();
            SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(service, servletContext);
            return service.service(request);
        }
        return null;
    }
}

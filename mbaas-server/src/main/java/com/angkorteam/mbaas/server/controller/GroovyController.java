package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.plain.response.RestResponse;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.spring.RestService;
import com.angkorteam.mbaas.server.validator.RestPathMethodValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> execute(HttpServletRequest request) throws Throwable {
        String method = StringUtils.upperCase(request.getMethod());
        RestTable restTable = Tables.REST.as("restTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        String pathInfo = request.getPathInfo();
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        Map<String, String> pathVariables = Maps.newHashMap();
        String[] segments = StringUtils.split(pathInfo, "/");
        int segmentCount = StringUtils.countMatches(pathInfo, '/');

        List<RestPojo> restPojos = this.context.select(restTable.fields()).from(restTable).where(restTable.SEGMENT.eq(segmentCount)).and(restTable.METHOD.eq(method)).fetchInto(RestPojo.class);
        if (restPojos == null || restPojos.isEmpty()) {
            return notFound();
        }

        RestPojo restPojo = null;

        List<RestPojo> candidates = new ArrayList<>(restPojos);
        for (int index = 0; index < segmentCount; index++) {
            List<RestPojo> newNameCandidates = Lists.newLinkedList();
            List<RestPojo> newLikeCandidates = Lists.newLinkedList();
            while (!candidates.isEmpty()) {
                RestPojo candidate = candidates.remove(0);
                String[] dbSegment = StringUtils.split(candidate.getPathVariable(), '/');
                if (StringUtils.endsWithIgnoreCase(segments[index], dbSegment[index])) {
                    newNameCandidates.add(candidate);
                } else if (StringUtils.equalsIgnoreCase(dbSegment[index], RestPathMethodValidator.PATH)) {
                    newLikeCandidates.add(candidate);
                }
            }
            if (!newNameCandidates.isEmpty()) {
                candidates.addAll(newNameCandidates);
            } else {
                candidates.addAll(newLikeCandidates);
            }
        }

        if (candidates.isEmpty()) {
            return notFound();
        }

        restPojo = candidates.get(0);

        String[] candidateSegments = StringUtils.split(restPojo.getPath(), '/');
        for (int i = 0; i < candidateSegments.length; i++) {
            String candidateSegment = candidateSegments[i];
            if (StringUtils.startsWithIgnoreCase(candidateSegment, "{") && StringUtils.endsWithIgnoreCase(candidateSegment, "}")) {
                String name = StringUtils.substring(candidateSegment, 1, StringUtils.length(candidateSegment) - 1);
                pathVariables.put(name, segments[i]);
            }
        }

        GroovyPojo groovyPojo = this.context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(restPojo.getGroovyId())).fetchOneInto(GroovyPojo.class);
        if (groovyPojo == null) {
            return notFound();
        }
        Class<?> clazz = classLoader.loadClass(groovyPojo.getJavaClass());
        RestService service = null;
        try {
            service = (RestService) clazz.newInstance();
        } catch (Throwable e) {
            return catchError(e);
        }

        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(service, servletContext);

        RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        List<String> serviceRoles = context.select(roleTable.NAME).from(roleTable).innerJoin(restRoleTable).on(roleTable.ROLE_ID.eq(restRoleTable.ROLE_ID)).where(restRoleTable.REST_ID.eq(restPojo.getRestId())).fetchInto(String.class);
        if (serviceRoles == null || serviceRoles.isEmpty()) {
        } else {
            String userRole = service.lookupUserRole(request, pathVariables);
            if (userRole == null || "".equals(userRole) || !serviceRoles.contains(userRole)) {
                return forbidden();
            }
        }
        try {
            return service.service(request, pathVariables);
        } catch (Throwable e) {
            return catchError(e);
        }
    }

    protected ResponseEntity<RestResponse> catchError(Throwable e) {
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

    protected ResponseEntity<RestResponse> notFound() {
        RestResponse response = new RestResponse();
        response.setResultCode(HttpStatus.NOT_FOUND.value());
        response.setResultMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setDebugMessage("service is not found");
        return ResponseEntity.ok(response);
    }

    protected ResponseEntity<RestResponse> forbidden() {
        RestResponse response = new RestResponse();
        response.setResultCode(HttpStatus.FORBIDDEN.value());
        response.setResultMessage(HttpStatus.FORBIDDEN.getReasonPhrase());
        response.setDebugMessage("service is forbidden");
        return ResponseEntity.ok(response);
    }
}

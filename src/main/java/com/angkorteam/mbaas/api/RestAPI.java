package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.request.LoginRequest;
import com.angkorteam.mbaas.request.SignupRequest;
import com.angkorteam.mbaas.response.Response;
import com.google.gson.Gson;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loginPost(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            path = "/signup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signupPost(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            path = "/logout",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logoutPost(@Header("X-SESSION") String session, @Header("X-APPCODE") String appCode) {
        return ResponseEntity.ok(null);
    }

}

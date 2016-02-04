package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.Application;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.google.gson.Gson;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.DSLContext;
import org.jooq.util.mariadb.MariaDBDSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

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
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Map<String, Object>>> signup() {

        jdbcTemplate.update("insert into application values(?,?,?,now(),COLUMN_CREATE('color', 'blue', 'size', 'XL'),1,1)", System.currentTimeMillis(), 1, String.valueOf(System.currentTimeMillis()));

        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT code, COLUMN_GET(extra, 'color' as char) AS color FROM application");

        return ResponseEntity.ok(result);
    }

}

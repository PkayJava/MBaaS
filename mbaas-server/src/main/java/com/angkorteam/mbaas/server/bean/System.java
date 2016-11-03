package com.angkorteam.mbaas.server.bean;

import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

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

}
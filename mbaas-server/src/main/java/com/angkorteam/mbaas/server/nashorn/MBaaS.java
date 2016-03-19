package com.angkorteam.mbaas.server.nashorn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 3/12/16.
 */
public class MBaaS {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    public final Console Console;

    public final Database Database;

    private final HttpServletRequest request;

    public MBaaS(JdbcTemplate jdbcTemplate, HttpServletRequest request) {
        Console = new Console(LOGGER);
        this.request = request;
        Database = new Database(jdbcTemplate);
    }

    public String getMobileId() {
        return request.getHeader("X-MBAAS-SESSION");
    }

}

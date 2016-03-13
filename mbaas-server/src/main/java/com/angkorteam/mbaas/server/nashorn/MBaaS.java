package com.angkorteam.mbaas.server.nashorn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Created by socheat on 3/12/16.
 */
public class MBaaS {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    public final Console Console;

    public final JdbcTemplate JdbcTemplate;

    public MBaaS(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        Console = new Console(LOGGER);
        JdbcTemplate = new JdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate));
    }

}

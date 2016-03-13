package com.angkorteam.mbaas.server.nashorn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/12/16.
 */
public class MBaaS {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    public final Console Console;

    public final Database Database;

    public MBaaS(JdbcTemplate jdbcTemplate) {
        Console = new Console(LOGGER);
        Database = new Database(jdbcTemplate);
    }

}

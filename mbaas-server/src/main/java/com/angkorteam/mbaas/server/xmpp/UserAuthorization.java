package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.vysper.xmpp.addressing.Entity;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 5/6/16.
 */
public class UserAuthorization implements org.apache.vysper.xmpp.authorization.UserAuthorization {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorization.class);

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    public UserAuthorization(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean verifyCredentials(Entity owner, String passwordCleartext, Object credentials) {
        LOGGER.info("UserAuthorization.verifyCredentials owner {}", owner.getNode());
        return verifyCredentials(owner.getNode(), passwordCleartext, passwordCleartext);
    }

    @Override
    public boolean verifyCredentials(String username, String passwordCleartext, Object credentials) {
        String login = username;
        if (username.contains("@")) {
            login = StringUtils.split(username, "@")[0];
        }
        LOGGER.info("UserAuthorization.verifyCredentials owner {}", login);
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(login)).and(userTable.PASSWORD.eq(DSL.md5(passwordCleartext))).fetchOneInto(userTable);
        return userRecord != null;
    }
}

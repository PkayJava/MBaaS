package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.vysper.xmpp.addressing.Entity;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.CredentialsExpiredException;

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
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(login)).fetchOneInto(userTable);
        if (userRecord == null) {
            return false;
        }
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.OWNER_USER_ID.eq(userRecord.getUserId())).and(mobileTable.ACCESS_TOKEN.eq(passwordCleartext)).fetchOneInto(mobileTable);
        if (mobileRecord == null) {
            return false;
        }
        DateTime dateTime = new DateTime(mobileRecord.getAccessTokenIssuedDate());
        dateTime = dateTime.plusSeconds(mobileRecord.getTimeToLive());
        if (dateTime.isBeforeNow()) {
            return false;
        }
        return true;
    }
}

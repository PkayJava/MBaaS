package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/6/16.
 */
public class AccountManagement implements org.apache.vysper.xmpp.authorization.AccountManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagement.class);

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    public AccountManagement(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUser(Entity username, String password) throws AccountCreationException {
        LOGGER.info("AccountManagement.addUser");
        throw new AccountCreationException("not implemented");
    }

    @Override
    public void changePassword(Entity username, String password) throws AccountCreationException {
        LOGGER.info("AccountManagement.changePassword");
        throw new AccountCreationException("not implemented");
    }

    @Override
    public boolean verifyAccountExists(Entity buddy) {
        LOGGER.info("AccountManagement.verifyAccountExists buddy {}", buddy.getNode());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(buddy.getNode())).fetchOneInto(userTable);
        return buddyRecord != null;
    }
}

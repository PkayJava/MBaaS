package com.angkorteam.mbaas.server.xmpp;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by socheat on 5/6/16.
 */
public class AccountManagement implements org.apache.vysper.xmpp.authorization.AccountManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagement.class);

    public AccountManagement() {
    }

    @Override
    public void addUser(Entity username, String password) throws AccountCreationException {
        throw new AccountCreationException("not implemented");
    }

    @Override
    public void changePassword(Entity username, String password) throws AccountCreationException {
        throw new AccountCreationException("not implemented");
    }

    @Override
    public boolean verifyAccountExists(Entity jid) {
        LOGGER.info("domain {}", jid.getDomain());
        LOGGER.info("node {}", jid.getNode());
        LOGGER.info("resource {}", jid.getResource());
        LOGGER.info("full qualified name {}", jid.getFullQualifiedName());
        LOGGER.info("canonicalized name {}", jid.getCanonicalizedName());
        if (jid.getBareJID() != null) {
            LOGGER.info("bare jid {}", jid.getBareJID().toString());
        }
        return false;
    }
}

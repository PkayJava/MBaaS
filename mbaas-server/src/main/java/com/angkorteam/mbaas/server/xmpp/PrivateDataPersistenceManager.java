package com.angkorteam.mbaas.server.xmpp;

import org.apache.vysper.xmpp.addressing.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by socheat on 5/6/16.
 */
public class PrivateDataPersistenceManager implements org.apache.vysper.xmpp.modules.extension.xep0049_privatedata.PrivateDataPersistenceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateDataPersistenceManager.class);

    public PrivateDataPersistenceManager() {
    }

    @Override
    public boolean isAvailable() {
        LOGGER.info("PrivateDataPersistenceManager.isAvailable");
        return false;
    }

    @Override
    public String getPrivateData(Entity entity, String key) {
        LOGGER.info("PrivateDataPersistenceManager.getPrivateData");
        LOGGER.info("domain {}", entity.getDomain());
        LOGGER.info("node {}", entity.getNode());
        LOGGER.info("resource {}", entity.getResource());
        LOGGER.info("full qualified name {}", entity.getFullQualifiedName());
        LOGGER.info("canonicalized name {}", entity.getCanonicalizedName());
        LOGGER.info("key {}", key);
        if (entity.getBareJID() != null) {
            LOGGER.info("bare jid {}", entity.getBareJID().toString());
        }
        return null;
    }

    @Override
    public boolean setPrivateData(Entity entity, String key, String xml) {
        LOGGER.info("PrivateDataPersistenceManager.setPrivateData");
        LOGGER.info("domain {}", entity.getDomain());
        LOGGER.info("node {}", entity.getNode());
        LOGGER.info("resource {}", entity.getResource());
        LOGGER.info("full qualified name {}", entity.getFullQualifiedName());
        LOGGER.info("canonicalized name {}", entity.getCanonicalizedName());
        LOGGER.info("key {}", key);
        LOGGER.info("xml {}", xml);
        if (entity.getBareJID() != null) {
            LOGGER.info("bare jid {}", entity.getBareJID().toString());
        }
        return false;
    }
}

package com.angkorteam.mbaas.server.xmpp;

import org.apache.vysper.xmpp.addressing.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by socheat on 5/6/16.
 */
public class VcardTempPersistenceManager implements org.apache.vysper.xmpp.modules.extension.xep0054_vcardtemp.VcardTempPersistenceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcardTempPersistenceManager.class);

    @Override
    public boolean isAvailable() {
        LOGGER.info("VcardTempPersistenceManager.isAvailable");
        return false;
    }

    @Override
    public String getVcard(Entity entity) {
        LOGGER.info("VcardTempPersistenceManager.getVcard");
        return null;
    }

    @Override
    public boolean setVcard(Entity entity, String xml) {
        LOGGER.info("VcardTempPersistenceManager.setVcard");
        return false;
    }
}

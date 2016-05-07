package com.angkorteam.mbaas.server.xmpp;

import org.apache.vysper.xmpp.stanza.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by socheat on 5/6/16.
 */
public class OfflineStorageProvider implements org.apache.vysper.xmpp.modules.extension.xep0160_offline_storage.OfflineStorageProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineStorageProvider.class);

    public OfflineStorageProvider() {
    }

    @Override
    public Collection<Stanza> getStanzasForBareJID(String bareJID) {
        LOGGER.info("bareJID {}", bareJID);
        return null;
    }

    @Override
    public void receive(Stanza stanza) {
        LOGGER.info("stanza {}", "stanza");
    }
}

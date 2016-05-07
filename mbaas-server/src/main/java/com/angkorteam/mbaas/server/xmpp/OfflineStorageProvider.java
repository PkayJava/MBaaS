package com.angkorteam.mbaas.server.xmpp;

import org.apache.commons.lang3.StringUtils;
import org.apache.vysper.xmpp.modules.extension.xep0160_offline_storage.AbstractOfflineStorageProvider;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by socheat on 5/6/16.
 */
public class OfflineStorageProvider extends AbstractOfflineStorageProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineStorageProvider.class);

    public OfflineStorageProvider() {
    }

    @Override
    public Collection<Stanza> getStanzasForBareJID(String buddy) {
        String buddyLogin;
        if (buddy.contains("@")) {
            buddyLogin = StringUtils.split(buddy, '@')[0];
        } else {
            buddyLogin = buddy;
        }
        LOGGER.info("OfflineStorageProvider.getStanzasForBareJID buddy {}", buddyLogin);
        List<Stanza> stanzas = new LinkedList<>();
        return stanzas;
    }

    @Override
    protected void storeStanza(Stanza stanza) {
        LOGGER.info("OfflineStorageProvider.storeStanza");
    }
}

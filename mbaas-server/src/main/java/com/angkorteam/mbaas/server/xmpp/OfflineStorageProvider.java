package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.OfflineTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.OfflineRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xml.fragment.XMLSemanticError;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.extension.xep0160_offline_storage.AbstractOfflineStorageProvider;
import org.apache.vysper.xmpp.stanza.*;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 5/6/16.
 */
public class OfflineStorageProvider extends AbstractOfflineStorageProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineStorageProvider.class);

    private final DSLContext context;
    private final JdbcTemplate jdbcTemplate;

    public OfflineStorageProvider(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
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
        UserTable userTable = Tables.USER.as("userTable");
        List<Stanza> stanzas = new LinkedList<>();
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(buddyLogin)).fetchOneInto(userTable);
        if (buddyRecord == null) {
            return stanzas;
        }
        OfflineTable offlineTable = Tables.OFFLINE.as("offlineTable");
        List<OfflineRecord> offlineRecords = context.select(offlineTable.fields()).from(offlineTable).where(offlineTable.TO_USER_ID.eq(buddyRecord.getUserId())).orderBy(offlineTable.DATE_CREATED.asc()).fetchInto(offlineTable);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String address = configuration.getString(Constants.XMPP_ADDRESS);
        List<String> ids = new ArrayList<>();
        List<String> offlines = new ArrayList<>();
        for (OfflineRecord offlineRecord : offlineRecords) {
            offlines.add(offlineRecord.getOfflineId());
            if (!ids.contains(offlineRecord.getFromUserId())) {
                ids.add(offlineRecord.getFromUserId());
            }
            if (!ids.contains(offlineRecord.getToUserId())) {
                ids.add(offlineRecord.getToUserId());
            }
        }
        Map<String, UserRecord> userRecords = new HashMap<>();
        for (UserRecord userRecord : context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.in(ids)).fetchInto(userTable)) {
            userRecords.put(userRecord.getUserId(), userRecord);
        }
        for (OfflineRecord offlineRecord : offlineRecords) {
            UserRecord fromUserRecord = userRecords.get(offlineRecord.getFromUserId());
            UserRecord toUserRecord = userRecords.get(offlineRecord.getToUserId());
            Entity from = new EntityImpl(fromUserRecord.getLogin(), address, null);
            Entity to = new EntityImpl(toUserRecord.getLogin(), address, null);
            if ("PresenceStanza".equals(offlineRecord.getCategory())) {
                Stanza stanza = StanzaBuilder.createPresenceStanza(from, to, offlineRecord.getLanguage(), PresenceStanzaType.valueOf(offlineRecord.getPresenceType()), offlineRecord.getPresenceShow(), offlineRecord.getPresenceStatus()).build();
                stanzas.add(stanza);
            } else if ("MessageStanza".equals(offlineRecord.getCategory())) {
                Stanza stanza = StanzaBuilder.createMessageStanza(from, to, offlineRecord.getLanguage(), offlineRecord.getMessageBody()).build();
                stanzas.add(stanza);
            }
        }
        context.delete(offlineTable).where(offlineTable.OFFLINE_ID.in(offlines)).execute();
        return stanzas;
    }

    @Override
    protected void storeStanza(Stanza stanza) {
        LOGGER.info("OfflineStorageProvider.storeStanza from buddy {} to buddy {}", stanza.getFrom().getNode(), stanza.getTo().getNode());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord fromUserRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(stanza.getFrom().getNode())).fetchOneInto(userTable);
        UserRecord toUserRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(stanza.getTo().getNode())).fetchOneInto(userTable);
        if (fromUserRecord == null || toUserRecord == null) {
            return;
        }
        if (stanza instanceof PresenceStanza) {
            OfflineTable offlineTable = Tables.OFFLINE.as("offlineTable");
            OfflineRecord offlineRecord = context.newRecord(offlineTable);
            offlineRecord.setOfflineId(UUID.randomUUID().toString());
            offlineRecord.setCategory("PresenceStanza");
            offlineRecord.setDateCreated(new Date());
            offlineRecord.setFromUserId(fromUserRecord.getUserId());
            offlineRecord.setToUserId(toUserRecord.getUserId());
            offlineRecord.setLanguage(stanza.getXMLLang());
            offlineRecord.setPresenceType(((PresenceStanza) stanza).getPresenceType().name());
            try {
                offlineRecord.setPresenceStatus(((PresenceStanza) stanza).getStatus(stanza.getXMLLang()));
            } catch (XMLSemanticError e) {
                LOGGER.info(e.getMessage());
            }
            try {
                offlineRecord.setPresenceShow(((PresenceStanza) stanza).getShow());
            } catch (XMLSemanticError e) {
                LOGGER.info(e.getMessage());
            }
            offlineRecord.store();
        } else if (stanza instanceof MessageStanza) {
            List<XMLElement> bodyElements = stanza.getInnerElementsNamed("body");
            if (bodyElements != null && !bodyElements.isEmpty()) {
                for (XMLElement element : bodyElements) {
                    OfflineTable offlineTable = Tables.OFFLINE.as("offlineTable");
                    OfflineRecord offlineRecord = context.newRecord(offlineTable);
                    offlineRecord.setOfflineId(UUID.randomUUID().toString());
                    offlineRecord.setCategory("MessageStanza");
                    offlineRecord.setDateCreated(new Date());
                    offlineRecord.setFromUserId(fromUserRecord.getUserId());
                    offlineRecord.setToUserId(toUserRecord.getUserId());
                    offlineRecord.setLanguage(element.getXMLLang());
                    offlineRecord.setMessageBody(element.getInnerText().getText());
                    offlineRecord.store();
                }
            }
        }
    }
}

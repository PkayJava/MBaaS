package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FriendTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.FriendRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.roster.*;
import org.apache.vysper.xmpp.modules.roster.Roster;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by socheat on 5/6/16.
 */
public class RosterManager implements org.apache.vysper.xmpp.modules.roster.persistence.RosterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RosterManager.class);

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final WeakHashMap<String, com.angkorteam.mbaas.server.xmpp.Roster> rosters;

    public RosterManager(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.rosters = new WeakHashMap<>();
    }

    @Override
    public Roster retrieve(Entity owner) throws RosterException {
        LOGGER.info("RosterManager.retrieve owner {}", owner.getNode());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(owner.getNode())).fetchOneInto(userTable);
        if (userRecord == null) {
            throw new RosterException("RosterManager.retrieve owner {}" + owner.getNode());
        }
        com.angkorteam.mbaas.server.xmpp.Roster roster = rosters.get(userRecord.getUserId());
        if (roster == null) {
            LOGGER.info("RosterManager.retrieve owner {}", owner.getNode());
            roster = new com.angkorteam.mbaas.server.xmpp.Roster(this.context, this.jdbcTemplate, userRecord.getUserId(), userRecord.getLogin());
            rosters.put(userRecord.getUserId(), roster);
        }
        return roster;
    }

    @Override
    public void addContact(Entity owner, RosterItem rosterItem) throws RosterException {
        Entity buddy = rosterItem.getJid();
        LOGGER.info("RosterManager.addContact owner {} buddy {} subscription {} ask subscription {}", owner.getNode(), buddy.getNode(), rosterItem.getSubscriptionType().name(), rosterItem.getAskSubscriptionType().name());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord ownerRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(owner.getNode())).fetchOneInto(userTable);
        if (ownerRecord == null) {
            throw new RosterException("RosterManager.addContact owner " + owner.getNode() + " buddy " + buddy.getNode() + " subscription " + rosterItem.getSubscriptionType().name() + " ask subscription " + rosterItem.getAskSubscriptionType().name());
        }
        String friendUserLogin = rosterItem.getJid().getNode();
        String askSubscriptionType = rosterItem.getAskSubscriptionType().name();
        String subscriptionType = rosterItem.getSubscriptionType().name();
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(friendUserLogin)).fetchOneInto(userTable);
        if (buddyRecord == null) {
            throw new RosterException("RosterManager.addContact owner " + owner.getNode() + " buddy " + buddy.getNode() + " subscription " + rosterItem.getSubscriptionType().name() + " ask subscription " + rosterItem.getAskSubscriptionType().name());
        }
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord friendRecord = context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(ownerRecord.getUserId())).and(friendTable.FRIEND_USER_ID.eq(buddyRecord.getUserId())).fetchOneInto(friendTable);
        if (friendRecord == null) {
            friendRecord = context.newRecord(friendTable);
            friendRecord.setFriendId(UUID.randomUUID().toString());
            friendRecord.setDateCreated(new Date());
            friendRecord.setUserId(ownerRecord.getUserId());
            friendRecord.setFriendUserId(buddyRecord.getUserId());
            friendRecord.setAskSubscription(askSubscriptionType);
            friendRecord.setSubscription(subscriptionType);
            friendRecord.store();
        } else {
            friendRecord.setAskSubscription(askSubscriptionType);
            friendRecord.setSubscription(subscriptionType);
            friendRecord.update();
        }
    }

    @Override
    public RosterItem getContact(Entity owner, Entity buddy) throws RosterException {
        LOGGER.info("RosterManager.getContact owner {} buddy {}", owner.getNode(), buddy.getNode());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord ownerRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(owner.getNode())).fetchOneInto(userTable);
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(buddy.getNode())).fetchOneInto(userTable);
        if (ownerRecord == null || buddyRecord == null) {
            throw new RosterException("RosterManager.getContact owner " + owner.getNode() + " buddy " + buddy.getNode());
        }
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord friendRecord = context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(ownerRecord.getUserId())).and(friendTable.FRIEND_USER_ID.eq(buddyRecord.getUserId())).fetchOneInto(friendTable);
        if (friendRecord == null) {
            throw new RosterException("RosterManager.getContact owner " + owner.getNode() + " buddy " + buddy.getNode());
        }
        Entity entity = new EntityImpl(buddy.getNode(), null, null);
        RosterItem rosterItem = new RosterItem(entity, SubscriptionType.valueOf(friendRecord.getSubscription()), AskSubscriptionType.valueOf(friendRecord.getAskSubscription()));
        return rosterItem;
    }

    @Override
    public void removeContact(Entity owner, Entity buddy) throws RosterException {
        LOGGER.info("RosterManager.removeContact owner {} buddy {}", owner.getNode(), buddy.getNode());
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord ownerRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(owner.getNode())).fetchOneInto(userTable);
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(buddy.getNode())).fetchOneInto(userTable);
        if (ownerRecord == null || buddyRecord == null) {
            throw new RosterException("RosterManager.removeContact owner " + owner.getNode() + " buddy " + buddy.getNode());
        }
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        context.delete(friendTable).where(friendTable.USER_ID.eq(ownerRecord.getUserId())).and(friendTable.FRIEND_USER_ID.eq(buddyRecord.getUserId())).execute();
        context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(ownerRecord.getUserId())).and(friendTable.USER_ID.eq(buddyRecord.getUserId())).execute();
    }
}

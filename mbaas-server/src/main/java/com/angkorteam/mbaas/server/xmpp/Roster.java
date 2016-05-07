package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FriendTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.FriendRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.roster.AskSubscriptionType;
import org.apache.vysper.xmpp.modules.roster.RosterItem;
import org.apache.vysper.xmpp.modules.roster.SubscriptionType;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by socheat on 5/6/16.
 */
public class Roster implements org.apache.vysper.xmpp.modules.roster.Roster {

    private static final Logger LOGGER = LoggerFactory.getLogger(Roster.class);

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final String owner;

    private final String ownerId;

    public Roster(DSLContext context, JdbcTemplate jdbcTemplate, String ownerId, String owner) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.ownerId = ownerId;
        this.owner = owner;
    }

    @Override
    public Iterator<RosterItem> iterator() {
        LOGGER.info("Roster.iterator owner {}", owner);
        UserTable userTable = Tables.USER.as("userTable");
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        Result<?> result = context.select(userTable.LOGIN, friendTable.SUBSCRIPTION, friendTable.ASK_SUBSCRIPTION).from(userTable).innerJoin(friendTable).on(friendTable.FRIEND_USER_ID.eq(userTable.USER_ID)).where(friendTable.USER_ID.eq(this.ownerId)).fetch();
        List<RosterItem> rosterItems = new ArrayList<>();
        for (int row = 0; row < result.size(); row++) {
            String login = result.getValue(row, userTable.LOGIN);
            String subscription = result.getValue(row, friendTable.SUBSCRIPTION);
            String askSubscription = result.getValue(row, friendTable.ASK_SUBSCRIPTION);
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            String address = configuration.getString(Constants.XMPP_ADDRESS);
            EntityImpl entity = new EntityImpl(login, address, null);
            RosterItem rosterItem = new RosterItem(entity, SubscriptionType.valueOf(subscription), AskSubscriptionType.valueOf(askSubscription));
            rosterItems.add(rosterItem);
        }
        return rosterItems.iterator();
    }

    @Override
    public RosterItem getEntry(Entity buddy) {
        LOGGER.info("Roster.getEntry owner {} buddy {}", owner, buddy.getNode());
        UserTable userTable = Tables.USER.as("userTable");
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        UserRecord buddyRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(buddy.getNode())).fetchOneInto(userTable);
        if (buddyRecord == null) {
            return null;
        }
        FriendRecord friendRecord = context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(ownerId)).and(friendTable.FRIEND_USER_ID.eq(buddyRecord.getUserId())).fetchOneInto(friendTable);
        RosterItem rosterItem = new RosterItem(buddy, SubscriptionType.valueOf(friendRecord.getSubscription()), AskSubscriptionType.valueOf(friendRecord.getAskSubscription()));
        return rosterItem;
    }
}

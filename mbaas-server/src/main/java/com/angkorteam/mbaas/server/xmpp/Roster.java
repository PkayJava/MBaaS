package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FriendTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.roster.RosterItem;
import org.apache.vysper.xmpp.modules.roster.SubscriptionType;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by socheat on 5/6/16.
 */
public class Roster implements org.apache.vysper.xmpp.modules.roster.Roster {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final String userId;

    public Roster(DSLContext context, JdbcTemplate jdbcTemplate, String userId) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.userId = userId;
    }

    @Override
    public Iterator<RosterItem> iterator() {
        UserTable userTable = Tables.USER.as("userTable");
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        Result<?> result = context.select(userTable.LOGIN, friendTable.SUBSCRIPTION).from(userTable).innerJoin(friendTable).on(friendTable.FRIEND_USER_ID.eq(userTable.USER_ID)).where(friendTable.USER_ID.eq(this.userId)).fetch();
        List<RosterItem> rosterItems = new ArrayList<>();
        for (int row = 0; row < result.size(); row++) {
            String login = result.getValue(row, userTable.LOGIN);
            String subscription = result.getValue(row, friendTable.SUBSCRIPTION);
            EntityImpl entity = new EntityImpl(login, null, null);
            RosterItem rosterItem = new RosterItem(entity, SubscriptionType.valueOf(subscription));
            rosterItems.add(rosterItem);
        }
        return rosterItems.iterator();
    }

    @Override
    public RosterItem getEntry(Entity contact) {
        UserTable userTable = Tables.USER.as("userTable");
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(contact.getNode())).fetchOneInto(userTable);
        if (userRecord == null) {
            return null;
        }
        Record2<String, String> result = context.select(userTable.LOGIN, friendTable.SUBSCRIPTION).from(userTable).innerJoin(friendTable).on(friendTable.FRIEND_USER_ID.eq(userTable.USER_ID)).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userRecord.getUserId())).fetchOne();
        String login = contact.getNode();
        return null;
    }

}

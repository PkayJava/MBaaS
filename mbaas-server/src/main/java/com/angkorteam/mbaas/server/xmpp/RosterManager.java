package com.angkorteam.mbaas.server.xmpp;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.roster.MutableRoster;
import org.apache.vysper.xmpp.modules.roster.Roster;
import org.apache.vysper.xmpp.modules.roster.RosterException;
import org.apache.vysper.xmpp.modules.roster.RosterItem;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 5/6/16.
 */
public class RosterManager implements org.apache.vysper.xmpp.modules.roster.persistence.RosterManager {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    public RosterManager(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Roster retrieve(Entity jid) throws RosterException {
        com.angkorteam.mbaas.server.xmpp.Roster roster = new com.angkorteam.mbaas.server.xmpp.Roster(this.context, this.jdbcTemplate, jid.getNode());
        return roster;
    }

    @Override
    public void addContact(Entity jid, RosterItem rosterItem) throws RosterException {
        System.out.println("");
    }

    @Override
    public RosterItem getContact(Entity jidUser, Entity jidContact) throws RosterException {
        System.out.println("");
        return null;
    }

    @Override
    public void removeContact(Entity jid, Entity jidContact) throws RosterException {
        System.out.println("");
    }
}

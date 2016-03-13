package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.WicketTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/13/16.
 */
public class WicketProvider extends JooqProvider {

    private WicketTable wicketTable = Tables.WICKET.as("wicketTable");

    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public WicketProvider() {
        this.from = wicketTable.join(userTable).on(wicketTable.USER_ID.eq(userTable.USER_ID));
        setSort("dateSeen", SortOrder.DESCENDING);
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getWicketId() {
        return this.wicketTable.WICKET_ID;
    }

    public Field<String> getClientIp() {
        return this.wicketTable.CLIENT_IP;
    }

    public Field<String> getSessionId() {
        return this.wicketTable.SESSION_ID;
    }

    public Field<String> getUserAgent() {
        return this.wicketTable.USER_AGENT;
    }

    public Field<Date> getDateCreated() {
        return this.wicketTable.DATE_CREATED;
    }

    public Field<Date> getDateSeen() {
        return this.wicketTable.DATE_SEEN;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}

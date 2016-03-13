package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SessionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/13/16.
 */
public class SessionProvider extends JooqProvider {

    private SessionTable sessionTable = Tables.SESSION.as("sessionTable");

    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public SessionProvider() {
        this.from = sessionTable.join(userTable).on(sessionTable.USER_ID.eq(userTable.USER_ID));
        setSort("dateSeen", SortOrder.DESCENDING);
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getSessionId() {
        return this.sessionTable.SESSION_ID;
    }

    public Field<String> getClientIp() {
        return this.sessionTable.CLIENT_IP;
    }

    public Field<String> getPushToken() {
        return this.sessionTable.PUSH_TOKEN;
    }

    public Field<String> getUserAgent() {
        return this.sessionTable.USER_AGENT;
    }

    public Field<Date> getDateCreated() {
        return this.sessionTable.DATE_CREATED;
    }

    public Field<Date> getDateSeen() {
        return this.sessionTable.DATE_SEEN;
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

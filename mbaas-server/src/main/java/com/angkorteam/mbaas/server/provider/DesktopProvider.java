package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
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
public class DesktopProvider extends JooqProvider {

    private DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");

    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public DesktopProvider() {
        this.from = desktopTable.join(userTable).on(desktopTable.USER_ID.eq(userTable.USER_ID));
        setSort("dateSeen", SortOrder.DESCENDING);
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getDesktopId() {
        return this.desktopTable.DESKTOP_ID;
    }

    public Field<String> getClientIp() {
        return this.desktopTable.CLIENT_IP;
    }

    public Field<String> getSessionId() {
        return this.desktopTable.SESSION_ID;
    }

    public Field<String> getUserAgent() {
        return this.desktopTable.USER_AGENT;
    }

    public Field<Date> getDateCreated() {
        return this.desktopTable.DATE_CREATED;
    }

    public Field<Date> getDateSeen() {
        return this.desktopTable.DATE_SEEN;
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

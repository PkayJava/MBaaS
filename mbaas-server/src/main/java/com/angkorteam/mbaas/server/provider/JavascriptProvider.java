package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class JavascriptProvider extends JooqProvider {

    private JavascriptTable javascriptTable;
    private UserTable userTable;

    private TableLike<?> from;

    private String ownerUserId;

    public JavascriptProvider() {
        this(null);
    }

    public JavascriptProvider(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        this.javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        this.userTable = Tables.USER.as("userTable");
        this.from = this.javascriptTable.join(this.userTable).on(this.javascriptTable.OWNER_USER_ID.eq(this.userTable.USER_ID));
    }

    public Field<String> getJavascriptId() {
        return this.javascriptTable.JAVASCRIPT_ID;
    }

    public Field<String> getSecurity() {
        return this.javascriptTable.SECURITY;
    }

    public Field<Date> getDateCreated() {
        return this.javascriptTable.DATE_CREATED;
    }

    public Field<String> getDescription() {
        return this.javascriptTable.DESCRIPTION;
    }

    public Field<String> getPath() {
        return this.javascriptTable.PATH;
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<String> getOwnerUserId() {
        return this.userTable.USER_ID;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        if (this.ownerUserId != null) {
            where.add(userTable.USER_ID.eq(this.ownerUserId));
        }
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}

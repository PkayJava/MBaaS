package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class JavascriptPrivder extends JooqProvider {

    private JavascriptTable javascriptTable;
    private UserTable userTable;

    private TableLike<?> from;

    public JavascriptPrivder() {
        this.javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        this.userTable = Tables.USER.as("userTable");
        this.from = this.javascriptTable.join(this.userTable).on(this.javascriptTable.OWNER_USER_ID.eq(this.userTable.USER_ID));
    }

    public Field<String> getJavascriptId() {
        return this.javascriptTable.JAVASCRIPT_ID;
    }

    public Field<Date> getDateCreated() {
        return this.javascriptTable.DATE_CREATED;
    }

    public Field<String> getDescription() {
        return this.javascriptTable.DESCRIPTION;
    }

    public Field<String> getName() {
        return this.javascriptTable.NAME;
    }

    public Field<String> getOwner() {
        return this.userTable.LOGIN;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(javascriptTable.DELETED.eq(false));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}

package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 8/18/16.
 */
public class RestProvider extends JooqProvider {

    private TableLike<?> from;

    private RestTable restTable;

    private String applicationCode;

    public RestProvider() {
        this.restTable = Tables.REST.as("restTable");
        this.from = this.restTable;
    }

    public Field<String> getRestId() {
        return this.restTable.REST_ID;
    }

    public Field<String> getMethod() {
        return this.restTable.METHOD;
    }

    public Field<String> getPath() {
        return this.restTable.PATH;
    }

    public Field<String> getName() {
        return this.restTable.NAME;
    }

    public Field<String> getDescription() {
        return this.restTable.DESCRIPTION;
    }

    public Field<Boolean> getSystem() {
        return this.restTable.SYSTEM;
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

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }
}

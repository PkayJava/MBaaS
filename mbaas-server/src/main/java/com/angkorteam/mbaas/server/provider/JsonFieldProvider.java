package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 8/6/16.
 */
public class JsonFieldProvider extends JooqProvider {

    private String applicationCode;
    private String jsonId;
    private TableLike<?> from;

    private Table<?> jsonFieldTable;
    private Table<?> jsonTable;

    public JsonFieldProvider(String applicationCode, String jsonId) {
        this.jsonId = jsonId;
        this.applicationCode = applicationCode;
        this.jsonFieldTable = DSL.table(Jdbc.JSON_FIELD).as("jsonFieldTable");
        this.jsonTable = DSL.table(Jdbc.JSON).as("jsonTable");
        this.from = this.jsonFieldTable.innerJoin(this.jsonTable).on(DSL.field(this.jsonFieldTable.getName() + "." + Jdbc.JsonField.JSON_ID, String.class).eq(DSL.field(this.jsonTable.getName() + "." + Jdbc.Json.JSON_ID, String.class)));
    }

    public Field<String> getJsonFieldId() {
        return DSL.field(this.jsonFieldTable.getName() + "." + Jdbc.JsonField.JSON_FIELD_ID, String.class);
    }

    public Field<String> getJsonId() {
        return DSL.field(this.jsonTable.getName() + "." + Jdbc.Json.JSON_ID, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.jsonFieldTable.getName() + "." + Jdbc.JsonField.NAME, String.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.jsonFieldTable.getName() + "." + Jdbc.JsonField.DESCRIPTION, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(DSL.field(this.jsonFieldTable.getName() + "." + Jdbc.JsonField.JSON_ID, String.class).eq(this.jsonId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }
}

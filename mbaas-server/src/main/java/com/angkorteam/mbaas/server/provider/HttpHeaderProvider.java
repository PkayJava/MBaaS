package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Created by socheat on 8/14/16.
 */
public class HttpHeaderProvider extends JooqProvider {

    private TableLike<?> from = null;

    private String applicationCode;

    private Table<?> httpHeaderTable;

    public HttpHeaderProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.httpHeaderTable = DSL.table(Jdbc.HTTP_HEADER).as("httpHeaderTable");
        this.from = this.httpHeaderTable;
    }

    public Field<String> getDescription() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.DESCRIPTION, String.class);
    }

    public Field<String> getHttpHeaderId() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.HTTP_HEADER_ID, String.class);
    }

    public Field<String> getEnumId() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.ENUM_ID, String.class);
    }

    public Field<String> getFormat() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.FORMAT, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.NAME, String.class);
    }

    public Field<String> getType() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.TYPE, String.class);
    }

    public Field<String> getSubType() {
        return DSL.field(this.httpHeaderTable.getName() + "." + Jdbc.HttpHeader.SUB_TYPE, String.class);
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
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }
}

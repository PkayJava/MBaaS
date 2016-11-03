//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.jooq.*;
//import org.jooq.impl.DSL;
//
//import java.util.List;
//
///**
// * Created by socheat on 8/14/16.
// */
//public class HttpQueryProvider extends JooqProvider {
//
//    private TableLike<?> from = null;
//
//    private String applicationCode;
//
//    private Table<?> httpQueryTable;
//
//    public HttpQueryProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.httpQueryTable = DSL.table(Jdbc.HTTP_QUERY).as("httpQueryTable");
//        this.from = this.httpQueryTable;
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.DESCRIPTION, String.class);
//    }
//
//    public Field<String> getHttpQueryId() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.HTTP_QUERY_ID, String.class);
//    }
//
//    public Field<String> getEnumId() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.ENUM_ID, String.class);
//    }
//
//    public Field<String> getFormat() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.FORMAT, String.class);
//    }
//
//    public Field<String> getName() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.NAME, String.class);
//    }
//
//    public Field<String> getType() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.TYPE, String.class);
//    }
//
//    public Field<String> getSubType() {
//        return DSL.field(this.httpQueryTable.getName() + "." + Jdbc.HttpQuery.SUB_TYPE, String.class);
//    }
//
//    @Override
//    protected TableLike<?> from() {
//        return this.from;
//    }
//
//    @Override
//    protected List<Condition> where() {
//        return null;
//    }
//
//    @Override
//    protected List<Condition> having() {
//        return null;
//    }
//
//    @Override
//    protected DSLContext getDSLContext() {
//        Application application = ApplicationUtils.getApplication();
//        return application.getDSLContext(this.applicationCode);
//    }
//}

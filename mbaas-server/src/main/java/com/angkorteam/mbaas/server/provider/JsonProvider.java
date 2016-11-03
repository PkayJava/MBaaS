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
// * Created by socheat on 3/25/16.
// */
//public class JsonProvider extends JooqProvider {
//
//    private Table<?> jsonTable;
//
//    private final String applicationCode;
//
//    private TableLike<?> from;
//
//    public JsonProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.jsonTable = DSL.table(Jdbc.JSON).as("jsonTable");
//        this.from = this.jsonTable;
//    }
//
//    public Field<String> getJsonId() {
//        return DSL.field(this.jsonTable.getName() + "." + Jdbc.Json.JSON_ID, String.class);
//    }
//
//    public Field<String> getName() {
//        return DSL.field(this.jsonTable.getName() + "." + Jdbc.Json.NAME, String.class);
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.jsonTable.getName() + "." + Jdbc.Json.DESCRIPTION, String.class);
//    }
//
//    @Override
//    protected DSLContext getDSLContext() {
//        Application application = ApplicationUtils.getApplication();
//        return application.getDSLContext(this.applicationCode);
//    }
//
//    @Override
//
//    protected TableLike<?> from() {
//        return from;
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
//}
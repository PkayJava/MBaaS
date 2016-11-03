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
// * Created by socheat on 8/18/16.
// */
//public class RestProvider extends JooqProvider {
//
//    private TableLike<?> from;
//
//    private Table<?> restTable;
//
//    private String applicationCode;
//
//    public RestProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.restTable = DSL.table("rest").as("restTable");
//        this.from = this.restTable;
//    }
//
//    public Field<String> getRestId() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.REST_ID, String.class);
//    }
//
//    public Field<String> getMethod() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.METHOD, String.class);
//    }
//
//    public Field<Boolean> getModified() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.MODIFIED, Boolean.class);
//    }
//
//    public Field<String> getPath() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.PATH, String.class);
//    }
//
//    public Field<String> getName() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.NAME, String.class);
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.restTable.getName() + "." + Jdbc.Rest.DESCRIPTION, String.class);
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

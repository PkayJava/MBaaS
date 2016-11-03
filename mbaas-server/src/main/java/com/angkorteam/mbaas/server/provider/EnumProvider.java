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
// * Created by socheat on 8/4/16.
// */
//public class EnumProvider extends JooqProvider {
//
//    private TableLike<?> from;
//
//    private final String applicationCode;
//
//    private Table<?> enumTable;
//
//    public EnumProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.enumTable = DSL.table(Jdbc.ENUM).as("enumTable");
//        this.from = this.enumTable;
//    }
//
//    public Field<String> getEnumId() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.ENUM_ID, String.class);
//    }
//
//    public Field<String> getName() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.NAME, String.class);
//    }
//
//    public Field<String> getFormat() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.FORMAT, String.class);
//    }
//
//    public Field<String> getType() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.TYPE, String.class);
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.DESCRIPTION, String.class);
//    }
//
//    @Override
//    protected DSLContext getDSLContext() {
//        Application application = ApplicationUtils.getApplication();
//        return application.getDSLContext(this.applicationCode);
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
//}

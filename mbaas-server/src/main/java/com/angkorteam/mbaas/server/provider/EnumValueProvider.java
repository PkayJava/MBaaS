//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.jooq.*;
//import org.jooq.impl.DSL;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by socheat on 8/6/16.
// */
//public class EnumValueProvider extends JooqProvider {
//
//    private String applicationCode;
//    private String enumId;
//    private TableLike<?> from;
//
//    private Table<?> enumItemTable;
//    private Table<?> enumTable;
//
//    public EnumValueProvider(String applicationCode, String enumId) {
//        this.enumId = enumId;
//        this.applicationCode = applicationCode;
//        this.enumItemTable = DSL.table(Jdbc.ENUM_ITEM).as("enumItemTable");
//        this.enumTable = DSL.table(Jdbc.ENUM).as("enumTable");
//        this.from = this.enumItemTable.innerJoin(this.enumTable).on(DSL.field(this.enumItemTable.getName() + "." + Jdbc.EnumItem.ENUM_ID, String.class).eq(DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.ENUM_ID, String.class)));
//    }
//
//    public Field<String> getEnumItemId() {
//        return DSL.field(this.enumItemTable.getName() + "." + Jdbc.EnumItem.ENUM_ITEM_ID, String.class);
//    }
//
//    public Field<String> getEnumId() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.ENUM_ID, String.class);
//    }
//
//    public Field<String> getType() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.TYPE, String.class);
//    }
//
//    public Field<String> getFormat() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.FORMAT, String.class);
//    }
//
//    public Field<String> getName() {
//        return DSL.field(this.enumTable.getName() + "." + Jdbc.Enum.NAME, String.class);
//    }
//
//    public Field<String> getValue() {
//        return DSL.field(this.enumItemTable.getName() + "." + Jdbc.EnumItem.VALUE, String.class);
//    }
//
//    @Override
//    protected TableLike<?> from() {
//        return this.from;
//    }
//
//    @Override
//    protected List<Condition> where() {
//        List<Condition> where = new ArrayList<>();
//        where.add(DSL.field(this.enumItemTable.getName() + "." + Jdbc.EnumItem.ENUM_ID, String.class).eq(this.enumId));
//        return where;
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

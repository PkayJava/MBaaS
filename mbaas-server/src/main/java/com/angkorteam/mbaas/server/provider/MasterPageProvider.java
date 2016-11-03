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
// * Created by socheat on 5/28/16.
// */
//public class MasterPageProvider extends JooqProvider {
//
//    private TableLike<?> from;
//
//    private final String applicationCode;
//
//    private Table<?> masterPageTable;
//
//    public MasterPageProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.masterPageTable = DSL.table(Jdbc.MASTER_PAGE).as("masterPageTable");
//        this.from = this.masterPageTable;
//    }
//
//    public Field<String> getMasterPageId() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.MASTER_PAGE_ID, String.class);
//    }
//
//    public Field<Boolean> getModified() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.MODIFIED, Boolean.class);
//    }
//
//    public Field<String> getCode() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.CODE, String.class);
//    }
//
//    public Field<String> getUserId() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.USER_ID, String.class);
//    }
//
//    public Field<String> getTitle() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.TITLE, String.class);
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.masterPageTable.getName() + "." + Jdbc.MasterPage.DESCRIPTION, String.class);
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

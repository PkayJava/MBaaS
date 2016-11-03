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
//public class BlockProvider extends JooqProvider {
//
//    private TableLike<?> from;
//
//    private final String applicationCode;
//
//    private Table<?> blockTable;
//
//    public BlockProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.blockTable = DSL.table(Jdbc.BLOCK).as("blockTable");
//        this.from = this.blockTable;
//    }
//
//    public Field<String> getBlockId() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.BLOCK_ID, String.class);
//    }
//
//    public Field<Boolean> getModified() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.MODIFIED, Boolean.class);
//    }
//
//    public Field<String> getCode() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.CODE, String.class);
//    }
//
//    public Field<String> getUserId() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.USER_ID, String.class);
//    }
//
//    public Field<String> getTitle() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.TITLE, String.class);
//    }
//
//    public Field<String> getDescription() {
//        return DSL.field(this.blockTable.getName() + "." + Jdbc.Block.DESCRIPTION, String.class);
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

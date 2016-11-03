//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.NashornTable;
//import org.jooq.Condition;
//import org.jooq.Field;
//import org.jooq.TableLike;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by socheat on 3/12/16.
// */
//public class NashornProvider extends JooqProvider {
//
//    private NashornTable nashornTable;
//    private TableLike<?> from;
//
//    public NashornProvider() {
//        this.nashornTable = Tables.NASHORN.as("nashornTable");
//        this.from = nashornTable;
//    }
//
//    public Field<String> getJavaClass() {
//        return this.nashornTable.NASHORN_ID;
//    }
//
//    public Field<String> getSecurity() {
//        return this.nashornTable.SECURITY;
//    }
//
//    public Field<Date> getDateCreated() {
//        return this.nashornTable.DATE_CREATED;
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

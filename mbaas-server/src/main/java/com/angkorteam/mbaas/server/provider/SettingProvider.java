//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.SettingTable;
//import org.jooq.Condition;
//import org.jooq.Field;
//import org.jooq.TableLike;
//
//import java.util.List;
//
///**
// * Created by socheat on 3/10/16.
// */
//public class SettingProvider extends JooqProvider {
//
//    private SettingTable settingTable = Tables.SETTING.as("settingTable");
//
//    private TableLike<?> from;
//
//    public SettingProvider() {
//        this.from = settingTable;
//    }
//
//    public Field<String> getKey() {
//        return this.settingTable.SETTING_ID;
//    }
//
//    public Field<String> getValue() {
//        return this.settingTable.VALUE;
//    }
//
//    public Field<String> getDescription() {
//        return this.settingTable.DESCRIPTION;
//    }
//
//    public Field<Boolean> getSystem() {
//        return this.settingTable.SYSTEM;
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

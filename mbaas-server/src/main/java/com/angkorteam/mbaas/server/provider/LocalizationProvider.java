//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.LocalizationTable;
//import org.jooq.Condition;
//import org.jooq.Field;
//import org.jooq.TableLike;
//
//import java.util.List;
//
///**
// * Created by socheat on 3/13/16.
// */
//public class LocalizationProvider extends JooqProvider {
//
//    private LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
//
//    private TableLike<?> from;
//
//    public LocalizationProvider() {
//        this.from = localizationTable;
//    }
//
//    public Field<String> getLocalizationId() {
//        return this.localizationTable.LOCALIZATION_ID;
//    }
//
//    public Field<String> getKey() {
//        return this.localizationTable.KEY;
//    }
//
//    public Field<String> getLanguage() {
//        return this.localizationTable.LANGUAGE;
//    }
//
//    public Field<String> getPage() {
//        return this.localizationTable.PAGE;
//    }
//
//    public Field<String> getLabel() {
//        return this.localizationTable.LABEL;
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

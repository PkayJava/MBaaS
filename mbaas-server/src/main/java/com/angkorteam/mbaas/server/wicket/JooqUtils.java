//package com.angkorteam.mbaas.server.wicket;
//
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.LocalizationTable;
//import com.angkorteam.mbaas.model.entity.tables.records.LocalizationRecord;
//import org.apache.wicket.core.request.ClientInfo;
//import org.apache.wicket.model.AbstractReadOnlyModel;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.protocol.http.request.WebClientInfo;
//import org.jooq.DSLContext;
//
//import java.util.UUID;
//
///**
// * Created by socheat on 3/1/16.
// */
//public abstract class JooqUtils extends AbstractReadOnlyModel<String> {
//
//    public static IModel<String> lookup(String key) {
//        return new Model(key, null, null);
//    }
//
//    public static IModel<String> lookup(String key, String language) {
//        return new Model(key, language, null);
//    }
//
//    public static IModel<String> lookup(String key, org.apache.wicket.Page page) {
//        ClientInfo clientInfo = page.getSession().getClientInfo();
//        if (clientInfo instanceof WebClientInfo) {
//            return new Model(key, ((WebClientInfo) clientInfo).getProperties().getNavigatorLanguage(), page.getClass().getSimpleName());
//        } else {
//            return new Model(key, "en", page.getClass().getSimpleName());
//        }
//    }
//
//    private static class Model extends AbstractReadOnlyModel<String> {
//
//        private String key;
//
//        private String page;
//
//        private String language;
//
//        public Model(String key, String language, String page) {
//            this.key = key;
//            this.page = page;
//            this.language = language;
//        }
//
//        @Override
//        public String getObject() {
//            Application application = (Application) Application.get();
//            DSLContext context = application.getDSLContext();
//
//            LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
//
//            LocalizationRecord localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.KEY.eq(key)).and(localizationTable.PAGE.eq(page)).and(localizationTable.LANGUAGE.eq(language)).limit(1).fetchOneInto(localizationTable);
//            if (localizationRecord != null) {
//                return localizationRecord.getLabel();
//            }
//            localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.KEY.eq(key)).and(localizationTable.PAGE.eq(page)).limit(1).fetchOneInto(localizationTable);
//            if (localizationRecord != null) {
//                return localizationRecord.getLabel();
//            }
//            localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.KEY.eq(key)).and(localizationTable.LANGUAGE.eq(language)).limit(1).fetchOneInto(localizationTable);
//            if (localizationRecord != null) {
//                return localizationRecord.getLabel();
//            }
//            localizationRecord = context.select(localizationTable.fields()).from(localizationTable).where(localizationTable.KEY.eq(key)).limit(1).fetchOneInto(localizationTable);
//            if (localizationRecord != null) {
//                return localizationRecord.getLabel();
//            }
//
//            localizationRecord = context.newRecord(localizationTable);
//            localizationRecord.setLocalizationId(UUID.randomUUID().toString());
//            localizationRecord.setKey(key);
//            localizationRecord.setLabel(key);
//            localizationRecord.setLanguage(language);
//            localizationRecord.setPage(page);
//            localizationRecord.store();
//
//            return key;
//        }
//    }
//
//}

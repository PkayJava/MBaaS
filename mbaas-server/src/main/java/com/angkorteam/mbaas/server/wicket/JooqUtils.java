package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ResourceTable;
import com.angkorteam.mbaas.model.entity.tables.records.ResourceRecord;
import org.apache.wicket.core.request.ClientInfo;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.jooq.DSLContext;

import java.util.UUID;

/**
 * Created by socheat on 3/1/16.
 */
public abstract class JooqUtils extends AbstractReadOnlyModel<String> {

    public static IModel<String> lookup(String key) {
        return new Model(key, null, null);
    }

    public static IModel<String> lookup(String key, String language) {
        return new Model(key, language, null);
    }

    public static IModel<String> lookup(String key, org.apache.wicket.Page page) {
        ClientInfo clientInfo = page.getSession().getClientInfo();
        if (clientInfo instanceof WebClientInfo) {
            return new Model(key, ((WebClientInfo) clientInfo).getProperties().getNavigatorLanguage(), page.getClass().getSimpleName());
        } else {
            return new Model(key, "en", page.getClass().getSimpleName());
        }
    }

    private static class Model extends AbstractReadOnlyModel<String> {

        private String key;

        private String page;

        private String language;

        public Model(String key, String language, String page) {
            this.key = key;
            this.page = page;
            this.language = language;
        }

        @Override
        public String getObject() {
            Application application = (Application) Application.get();
            DSLContext context = application.getDSLContext();

            ResourceTable resourceTable = Tables.RESOURCE.as("resourceTable");

            ResourceRecord resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.KEY.eq(key)).and(resourceTable.PAGE.eq(page)).and(resourceTable.LANGUAGE.eq(language)).limit(1).fetchOneInto(resourceTable);
            if (resourceRecord != null) {
                return resourceRecord.getLabel();
            }
            resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.KEY.eq(key)).and(resourceTable.PAGE.eq(page)).limit(1).fetchOneInto(resourceTable);
            if (resourceRecord != null) {
                return resourceRecord.getLabel();
            }
            resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.KEY.eq(key)).and(resourceTable.LANGUAGE.eq(language)).limit(1).fetchOneInto(resourceTable);
            if (resourceRecord != null) {
                return resourceRecord.getLabel();
            }
            resourceRecord = context.select(resourceTable.fields()).from(resourceTable).where(resourceTable.KEY.eq(key)).limit(1).fetchOneInto(resourceTable);
            if (resourceRecord != null) {
                return resourceRecord.getLabel();
            }

            resourceRecord = context.newRecord(resourceTable);
            resourceRecord.setResourceId(UUID.randomUUID().toString());
            resourceRecord.setKey(key);
            resourceRecord.setLabel(key);
            resourceRecord.store();

            return key;
        }
    }

}

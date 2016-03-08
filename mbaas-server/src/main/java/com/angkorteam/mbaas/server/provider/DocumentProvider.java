package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.configuration.*;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/4/16.
 */
public class DocumentProvider extends JooqProvider {

    private TableLike<?> from;

    private String collectionName;

    private UserTable userTable = Tables.USER.as("userTable");

    public DocumentProvider(String collectionId) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        this.collectionName = collectionRecord.getName();

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable);

        Table<Record> table = DSL.table(this.collectionName);

        for (AttributeRecord attributeRecord : attributeRecords) {
            if (!attributeRecord.getVirtual()) {
                if (String.class.getName().equals(attributeRecord.getJavaType())) {
                    boardField(attributeRecord.getName(), DSL.field(attributeRecord.getName(), String.class));
                } else if (Date.class.getName().equals(attributeRecord.getJavaType())) {
                    boardField(attributeRecord.getName(), DSL.field(attributeRecord.getName(), Date.class));
                }
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String jdbcColumnOwnerUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
        this.from = table.innerJoin(userTable).on(DSL.field(this.collectionName + "." + jdbcColumnOwnerUserId, String.class).eq(userTable.USER_ID));
    }

    public Field<String> getOwner() {
        return this.userTable.LOGIN;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}

package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.server.function.MariaDBFunction;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        CollectionRecord collectionRecord = context.select(collectionTable.fields())
                .from(collectionTable)
                .where(collectionTable.COLLECTION_ID.eq(collectionId))
                .fetchOneInto(collectionTable);

        this.collectionName = collectionRecord.getName();

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .fetchInto(attributeTable);

        Map<String, AttributeRecord> virtualAttributeRecords = new HashMap<>();
        for (AttributeRecord attributeRecord : attributeRecords) {
            virtualAttributeRecords.put(attributeRecord.getAttributeId(), attributeRecord);
        }

        Table<Record> table = DSL.table(this.collectionName);

        for (AttributeRecord attributeRecord : attributeRecords) {
            if (AttributeTypeEnum.Boolean.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Boolean.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Boolean> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Boolean.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Byte.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Byte.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Byte> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Byte.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Short.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Short.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Short> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Short.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Integer.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Integer.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Integer> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Integer.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Long.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Long.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Long> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Long.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Float.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Float.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Float> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Float.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Double.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Double.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Double> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Double.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Character.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Character.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Character> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Character.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.String.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), String.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<String> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), String.class);
                    boardField(attributeRecord.getName(), field);
                }
            } else if (AttributeTypeEnum.Time.getLiteral().equals(attributeRecord.getJavaType())
                    || AttributeTypeEnum.Date.getLiteral().equals(attributeRecord.getJavaType())
                    || AttributeTypeEnum.DateTime.getLiteral().equals(attributeRecord.getJavaType())) {
                if (!attributeRecord.getVirtual()) {
                    boardField(attributeRecord.getName(), DSL.field(collectionRecord.getName() + "." + attributeRecord.getName(), Date.class));
                } else {
                    AttributeRecord virtualAttributeRecord = virtualAttributeRecords.get(attributeRecord.getVirtualAttributeId());
                    Field<Date> field = DSL.field(MariaDBFunction.columnGet(collectionRecord.getName() + "." + virtualAttributeRecord.getName(), attributeRecord.getName(), attributeRecord.getJavaType()), Date.class);
                    boardField(attributeRecord.getName(), field);
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

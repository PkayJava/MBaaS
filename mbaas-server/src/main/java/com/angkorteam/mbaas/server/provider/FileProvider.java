package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
public class FileProvider extends JooqProvider {

    private FileTable fileTable;
    private UserTable userTable;

    private TableLike<?> from;

    public FileProvider() {
        this.fileTable = Tables.FILE.as("fileTable");
        this.userTable = Tables.USER.as("userTable");

        this.from = fileTable.join(userTable).on(fileTable.OWNER_USER_ID.eq(userTable.USER_ID));
        DSLContext context = getDSLContext();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields())
                .from(collectionTable)
                .where(collectionTable.NAME.eq(Tables.FILE.getName()))
                .fetchOneInto(collectionTable);
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .fetchInto(attributeTable);

        Map<String, AttributeRecord> virtualAttributeRecords = new HashMap<>();
        for (AttributeRecord attributeRecord : attributeRecords) {
            virtualAttributeRecords.put(attributeRecord.getAttributeId(), attributeRecord);
        }

        for (AttributeRecord attributeRecord : attributeRecords) {
            if (attributeRecord.getSystem()) {
                continue;
            }
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attributeRecord.getAttributeType());
            if (AttributeTypeEnum.Boolean == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Boolean.class));
            } else if (AttributeTypeEnum.Byte == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Byte.class));
            } else if (AttributeTypeEnum.Short == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Short.class));
            } else if (AttributeTypeEnum.Integer == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Integer.class));
            } else if (AttributeTypeEnum.Long == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Long.class));
            } else if (AttributeTypeEnum.Float == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Float.class));
            } else if (AttributeTypeEnum.Double == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Double.class));
            } else if (AttributeTypeEnum.Character == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), String.class));
            } else if (AttributeTypeEnum.String == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), String.class));
            } else if (AttributeTypeEnum.Time == attributeType
                    || AttributeTypeEnum.Date == attributeType
                    || AttributeTypeEnum.DateTime == attributeType) {
                boardField(attributeRecord.getName(), DSL.field(fileTable.getName() + "." + attributeRecord.getName(), Date.class));
            }
        }
    }

    public Field<String> getFileId() {
        return this.fileTable.FILE_ID;
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<Integer> getLength() {
        return this.fileTable.LENGTH;
    }

    public Field<String> getMime() {
        return this.fileTable.MIME;
    }

    public Field<String> getName() {
        return this.fileTable.LABEL;
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

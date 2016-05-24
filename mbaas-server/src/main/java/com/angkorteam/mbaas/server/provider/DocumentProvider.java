package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;

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
    private String collectionId;

    private final String applicationCode;

    private Table<?> userTable;

    public DocumentProvider(String applicationCode, String collectionId, String collectionName) {
        this.applicationCode = applicationCode;
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.collectionName = collectionName;
        this.collectionId = collectionId;

        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

        List<Map<String, Object>> attributeRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.VISIBILITY + " = ?", collectionId, VisibilityEnum.Shown.getLiteral());

        Map<String, Map<String, Object>> virtualAttributeRecords = new HashMap<>();
        for (Map<String, Object> attributeRecord : attributeRecords) {
            virtualAttributeRecords.put((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID), attributeRecord);
        }

        Table<Record> table = DSL.table(this.collectionName);

        for (Map<String, Object> attributeRecord : attributeRecords) {
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
            if (AttributeTypeEnum.Boolean == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Boolean.class));
            } else if (AttributeTypeEnum.Byte == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Byte.class));
            } else if (AttributeTypeEnum.Short == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Short.class));
            } else if (AttributeTypeEnum.Integer == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Integer.class));
            } else if (AttributeTypeEnum.Long == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Long.class));
            } else if (AttributeTypeEnum.Float == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Float.class));
            } else if (AttributeTypeEnum.Double == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Double.class));
            } else if (AttributeTypeEnum.Character == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), String.class));
            } else if (AttributeTypeEnum.String == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), String.class));
            } else if (AttributeTypeEnum.Time == attributeType
                    || AttributeTypeEnum.Date == attributeType
                    || AttributeTypeEnum.DateTime == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Date.class));
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String jdbcColumnOwnerUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_APPLICATION_USER_ID);
        this.from = table.innerJoin(this.userTable).on(DSL.field(this.collectionName + "." + jdbcColumnOwnerUserId, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    public Field<String> getOwner() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.LOGIN, String.class);
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

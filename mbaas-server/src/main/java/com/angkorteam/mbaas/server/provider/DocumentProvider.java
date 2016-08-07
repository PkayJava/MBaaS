package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
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
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
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
            TypeEnum attributeType = TypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
            if (TypeEnum.Boolean == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Boolean.class));
            } else if (TypeEnum.Long == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Long.class));
            } else if (TypeEnum.Double == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Double.class));
            } else if (TypeEnum.Character == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), String.class));
            } else if (TypeEnum.String == attributeType || TypeEnum.Text == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), String.class));
            } else if (TypeEnum.Time == attributeType
                    || TypeEnum.Date == attributeType
                    || TypeEnum.DateTime == attributeType) {
                boardField((String) attributeRecord.get(Jdbc.Attribute.NAME), DSL.field(collectionName + "." + attributeRecord.get(Jdbc.Attribute.NAME), Date.class));
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String jdbcColumnOwnerUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
        this.from = table.innerJoin(this.userTable).on(DSL.field(this.collectionName + "." + jdbcColumnOwnerUserId, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    public Field<String> getOwner() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
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

package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by socheat on 2/15/16.
 */

public class PermissionFactoryBean implements FactoryBean<PermissionFactoryBean.Permission>, InitializingBean {

    private Permission permission;

    private DataSource dataSource;

    private DSLContext context;

    private JdbcTemplate jdbcTemplate;

    @Override
    public Permission getObject() throws Exception {
        return this.permission;
    }

    @Override
    public Class<?> getObjectType() {
        return Permission.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.permission = new Permission(dataSource, context, jdbcTemplate);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DSLContext getContext() {
        return context;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class Permission {

        private final DataSource dataSource;

        private final DSLContext context;

        private final JdbcTemplate jdbcTemplate;

        private Permission(DataSource dataSource, DSLContext context, JdbcTemplate jdbcTemplate) {
            this.dataSource = dataSource;
            this.context = context;
            this.jdbcTemplate = jdbcTemplate;
        }

        public boolean hasDocumentAccess(String session, String collection, int documentId, int action) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

            Token tokenTable = Tables.TOKEN.as("tokenTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            Table tableTable = Tables.TABLE.as("tableTable");
            DocumentUserPrivacy documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
            DocumentRolePrivacy documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

            TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);

            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);

            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);

            TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);

            Integer documentOwnerUserId = jdbcTemplate.queryForObject("select " + configuration.getString(Constants.JDBC_OWNER_USER_ID) + " from `" + tableRecord.getName() + "` where " + tableRecord.getName() + "_id = ?", Integer.class, documentId);

            DocumentUserPrivacyRecord documentUserPrivacyRecord = context.select(documentUserPrivacyTable.fields())
                    .from(documentUserPrivacyTable)
                    .where(documentUserPrivacyTable.TABLE_ID.eq(tableRecord.getTableId()))
                    .and(documentUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                    .and(documentUserPrivacyTable.DOCUMENT_ID.eq(documentId))
                    .fetchOneInto(documentUserPrivacyTable);
            DocumentRolePrivacyRecord documentRolePrivacyRecord = context.select(documentRolePrivacyTable.fields())
                    .from(documentRolePrivacyTable)
                    .where(documentRolePrivacyTable.TABLE_ID.eq(tableRecord.getTableId()))
                    .and(documentRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                    .and(documentRolePrivacyTable.DOCUMENT_ID.eq(documentId))
                    .fetchOneInto(documentRolePrivacyTable);

            return roleRecord.getName().equals(configuration.getString(Constants.ROLE_ADMINISTRATOR))
                    || (userRecord.getUserId().equals(documentOwnerUserId))
                    || (documentUserPrivacyRecord != null && (documentUserPrivacyRecord.getPermisson() & action) == action)
                    || (documentRolePrivacyRecord != null && (documentRolePrivacyRecord.getPermisson() & action) == action);
        }

        public boolean hasCollectionAccess(String session, String collection, int action) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

            Token tokenTable = Tables.TOKEN.as("tokenTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            Table tableTable = Tables.TABLE.as("tableTable");
            TableUserPrivacy tableUserPrivacyTable = Tables.TABLE_USER_PRIVACY.as("tableUserPrivacyTable");
            TableRolePrivacy tableRolePrivacyTable = Tables.TABLE_ROLE_PRIVACY.as("tableRolePrivacyTable");

            TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);

            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);

            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);

            TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);

            TableUserPrivacyRecord tableUserPrivacyRecord = context.select(tableUserPrivacyTable.fields()).from(tableUserPrivacyTable).where(tableUserPrivacyTable.TABLE_ID.eq(tableRecord.getTableId())).and(tableUserPrivacyTable.USER_ID.eq(userRecord.getUserId())).fetchOneInto(tableUserPrivacyTable);
            TableRolePrivacyRecord tableRolePrivacyRecord = context.select(tableRolePrivacyTable.fields()).from(tableRolePrivacyTable).where(tableRolePrivacyTable.TABLE_ID.eq(tableRecord.getTableId())).and(tableRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId())).fetchOneInto(tableRolePrivacyTable);

            return roleRecord.getName().equals(configuration.getString(Constants.ROLE_ADMINISTRATOR))
                    || (userRecord.getUserId().equals(tableRecord.getOwnerUserId()))
                    || (tableUserPrivacyRecord != null && (tableUserPrivacyRecord.getPermisson() & action) == action)
                    || (tableRolePrivacyRecord != null && (tableRolePrivacyRecord.getPermisson() & action) == action);
        }
    }

}

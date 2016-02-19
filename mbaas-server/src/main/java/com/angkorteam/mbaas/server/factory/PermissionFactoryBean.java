package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.PermissionEnum;
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

        public boolean isCollectionOwner(String session, String collectionName) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            User userTable = Tables.USER.as("userTable");
            Collection collectionTable = Tables.COLLECTION.as("collectionTable");
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collectionName)).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                return false;
            }
            if (collectionRecord.getOwnerUserId().equals(userRecord.getUserId())) {
                return true;
            }
            return false;
        }

        public boolean isDocumentOwner(String session, String collectionName, String documentId) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            User userTable = Tables.USER.as("userTable");
            Collection collectionTable = Tables.COLLECTION.as("collectionTable");
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collectionName)).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                return false;
            }
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

            String ownerUserId = jdbcTemplate.queryForObject("SELECT " + configuration.getString(Constants.JDBC_OWNER_USER_ID) + " FROM `" + collectionName + "` WHERE " + collectionName + "_id = ?", String.class, documentId);
            if (ownerUserId == null) {
                return false;
            }

            if (userRecord.getUserId().equals(ownerUserId)) {
                return true;
            }
            return false;
        }

        public boolean isAdministratorUser(String session) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                return false;
            }
            if (roleRecord.getName().equals(configuration.getString(Constants.ROLE_ADMINISTRATOR))) {
                return true;
            }
            return false;
        }

        public boolean isBackOfficeUser(String session) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                return false;
            }
            if (roleRecord.getName().equals(configuration.getString(Constants.ROLE_BACKOFFICE))) {
                return true;
            }
            return false;
        }

        public boolean isRegisteredUser(String session) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                return false;
            }

            if (roleRecord.getName().equals(configuration.getString(Constants.ROLE_REGISTERED))) {
                return true;
            }
            return false;
        }

        public boolean isUser(String session, String roleName) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                return false;
            }
            if (roleRecord.getName().equals(roleName)) {
                return true;
            }
            return false;
        }

        public boolean hasDocumentPermission(String session, String collectionName, String documentId, int action) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            Collection collectionTable = Tables.COLLECTION.as("collectionTable");
            DocumentUserPrivacy documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
            DocumentRolePrivacy documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }

            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }

            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collectionName)).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                return false;
            }

            Integer count = jdbcTemplate.queryForObject("select count(*) from `" + collectionRecord.getName() + "` where " + collectionRecord.getName() + "_id = ?", Integer.class, documentId);
            if (count <= 0) {
                return false;
            }

            DocumentUserPrivacyRecord documentUserPrivacyRecord = context.select(documentUserPrivacyTable.fields())
                    .from(documentUserPrivacyTable)
                    .where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                    .and(documentUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                    .and(documentUserPrivacyTable.DOCUMENT_ID.eq(documentId))
                    .fetchOneInto(documentUserPrivacyTable);

            if (documentUserPrivacyRecord != null && (documentUserPrivacyRecord.getPermisson() & action) == action) {
                return true;
            }

            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);

            DocumentRolePrivacyRecord documentRolePrivacyRecord = context.select(documentRolePrivacyTable.fields())
                    .from(documentRolePrivacyTable)
                    .where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                    .and(documentRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                    .and(documentRolePrivacyTable.DOCUMENT_ID.eq(documentId))
                    .fetchOneInto(documentRolePrivacyTable);

            if (documentRolePrivacyRecord != null && (documentRolePrivacyRecord.getPermisson() & action) == action) {
                return true;
            }

            return false;
        }

        public boolean hasCollectionPermission(String session, String collection, int action) {
            Session sessionTable = Tables.SESSION.as("sessionTable");
            Role roleTable = Tables.ROLE.as("roleTable");
            User userTable = Tables.USER.as("userTable");
            Collection collectionTable = Tables.COLLECTION.as("collectionTable");
            CollectionUserPrivacy tableUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("tableUserPrivacyTable");
            CollectionRolePrivacy tableRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("tableRolePrivacyTable");

            SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
            if (sessionRecord == null) {
                return false;
            }

            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                return false;
            }

            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                return false;
            }

            CollectionUserPrivacyRecord collectionUserPrivacyRecord = context.select(tableUserPrivacyTable.fields())
                    .from(tableUserPrivacyTable)
                    .where(tableUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                    .and(tableUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                    .fetchOneInto(tableUserPrivacyTable);

            if (collectionUserPrivacyRecord != null && (collectionUserPrivacyRecord.getPermisson() & action) == action) {
                return true;
            }

            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);

            CollectionRolePrivacyRecord collectionRolePrivacyRecord = context.select(tableRolePrivacyTable.fields())
                    .from(tableRolePrivacyTable)
                    .where(tableRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                    .and(tableRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                    .fetchOneInto(tableRolePrivacyTable);

            if (collectionRolePrivacyRecord != null && (collectionRolePrivacyRecord.getPermisson() & action) == action) {
                return true;
            }

            return false;
        }
    }

}

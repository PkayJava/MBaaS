package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

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

        public boolean hasCollectionAccess(String session, String collection, int action) {
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

            return (tableUserPrivacyRecord.getPermisson() & action) == action || (tableRolePrivacyRecord.getPermisson() & action) == action;
        }
    }

}

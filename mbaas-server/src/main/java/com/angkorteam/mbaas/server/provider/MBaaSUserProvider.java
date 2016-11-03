//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.MbaasRoleTable;
//import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
//import org.jooq.Condition;
//import org.jooq.Field;
//import org.jooq.TableLike;
//
//import java.util.List;
//
///**
// * Created by socheat on 3/1/16.
// */
//public class MBaaSUserProvider extends JooqProvider {
//
//    private TableLike<?> from;
//
//    private MbaasUserTable userTable;
//
//    private MbaasRoleTable roleTable;
//
//    public MBaaSUserProvider() {
//        this.userTable = Tables.MBAAS_USER.as("userTable");
//        this.roleTable = Tables.MBAAS_ROLE.as("roleTable");
//        this.from = userTable.join(roleTable).on(userTable.MBAAS_ROLE_ID.eq(roleTable.MBAAS_ROLE_ID));
//    }
//
//    public Field<String> getLogin() {
//        return this.userTable.LOGIN;
//    }
//
//    public Field<String> getMbaasUserId() {
//        return this.userTable.MBAAS_USER_ID;
//    }
//
//    public Field<String> getFullName() {
//        return this.userTable.FULL_NAME;
//    }
//
//    public Field<String> getRoleName() {
//        return this.roleTable.NAME;
//    }
//
//    public Field<String> getStatus() {
//        return this.userTable.STATUS;
//    }
//
//    public Field<Boolean> getSystem() {
//        return this.userTable.SYSTEM;
//    }
//
//    @Override
//    protected TableLike<?> from() {
//        return this.from;
//    }
//
//    @Override
//    protected List<Condition> where() {
//        return null;
//    }
//
//    @Override
//    protected List<Condition> having() {
//        return null;
//    }
//}

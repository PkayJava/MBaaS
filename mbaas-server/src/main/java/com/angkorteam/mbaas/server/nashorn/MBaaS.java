package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MobilePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.ScriptException;

/**
 * Created by socheat on 3/12/16.
 */
public class MBaaS {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    public final Console Console;

    public final Database Database;

    public final Permission Permission;

    private final Request request;

    private final DSLContext context;

    private boolean sudo;

    private boolean authenticated;

    private UserPojo userPojo;

    private RolePojo rolePojo;

    public MBaaS(DSLContext context, JdbcTemplate jdbcTemplate, Request request) {
        Console = new Console(LOGGER);
        Database = new Database(context, jdbcTemplate, this);
        Permission = new Permission(this, context, jdbcTemplate);
        this.context = context;
        this.request = request;

    }

    public void authenticate() throws ScriptException {
        String mobileId = request.getHeader("X-MBAAS-SESSION");
        authenticate(mobileId);
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void authenticate(String mobileId) throws ScriptException {

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");

        MobilePojo mobilePojo = null;
        if (mobileId != null && !"".equals(mobileId)) {
            mobilePojo = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(mobileId)).fetchOneInto(MobilePojo.class);
        }

        UserTable userTable = Tables.USER.as("userTable");
        if (mobilePojo != null) {
            userPojo = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobilePojo.getOwnerUserId())).fetchOneInto(UserPojo.class);
        }

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        if (userPojo != null) {
            rolePojo = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userPojo.getRoleId())).fetchOneInto(RolePojo.class);
        }

        if (mobilePojo != null && userPojo != null && rolePojo != null) {
            this.authenticated = true;
        } else {
            throw new ScriptException("authentication was failed");
        }
    }

    public void authenticate(String username, String password) throws ScriptException {
        UserTable userTable = Tables.USER.as("userTable");
        userPojo = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(UserPojo.class);

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        if (userPojo != null) {
            rolePojo = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userPojo.getRoleId())).fetchOneInto(RolePojo.class);
        }

        if (userPojo != null && rolePojo != null) {
            this.authenticated = true;
        } else {
            throw new ScriptException("authentication was failed");
        }
    }

    public void sudo() throws ScriptException {
        if (!this.authenticated) {
            throw new ScriptException("you are not yet authenticated");
        }
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String internalAdminRole = configuration.getString(Constants.USER_INTERNAL_ADMIN_ROLE);
        if (rolePojo != null && rolePojo.getName().equals(internalAdminRole)) {
            this.sudo = true;
        }
        if (!this.sudo) {
            throw new ScriptException("root authentication was failed");
        }
    }

    public boolean isSudo() {
        return sudo;
    }

    public String getCurrentUserId() throws ScriptException {
        if (!this.authenticated) {
            throw new ScriptException("you are not yet authenticated");
        }
        return this.userPojo.getUserId();
    }

}

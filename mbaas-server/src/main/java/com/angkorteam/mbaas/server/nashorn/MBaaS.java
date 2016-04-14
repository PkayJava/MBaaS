package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MobilePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.plain.Identity;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

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

    public final Identity Identity;

    public MBaaS(DSLContext context, Identity identity, JdbcTemplate jdbcTemplate, Request request) {
        this.Console = new Console(LOGGER);
        this.Identity = identity;
        this.Database = new Database(context, identity, jdbcTemplate, this);
        this.Permission = new Permission(this, identity, context, jdbcTemplate);
        this.request = request;
    }

    public void proptLogin() {
        throw new BadCredentialsException("authentication is need");
    }

}

package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.plain.response.RestResponse;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by socheat on 11/4/16.
 */
public interface RestService {

    ResponseEntity<?> service(HttpServletRequest request, Map<String, String> pathVariables) throws Throwable;

    default String lookupUserRole(HttpServletRequest request, Map<String, String> pathVariables) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Basic ")) {
            return null;
        }

        byte[] base64Token = null;
        try {
            base64Token = authorization.substring(6).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            return null;
        }

        String token = null;
        try {
            token = new String(decoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        int delim = token.indexOf(":");

        if (delim == -1) {
            return null;
        }
        String secrets[] = new String[]{token.substring(0, delim), token.substring(delim + 1)};
        String username = secrets[0];
        String password = secrets[1];

        DSLContext context = Spring.getBean(DSLContext.class);

        UserTable userTable = Tables.USER.as("userTable");
        UserPojo userPojo = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(UserPojo.class);
        if (userPojo == null) {
            return null;
        }
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        RolePojo rolePojo = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userPojo.getRoleId())).fetchOneInto(RolePojo.class);
        if (rolePojo == null) {
            return null;
        }
        return rolePojo.getName();
    }

    String getRestUUID();

}

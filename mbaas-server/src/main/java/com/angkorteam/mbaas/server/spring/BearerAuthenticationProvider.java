package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import org.jooq.DSLContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/27/16.
 */
public class BearerAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private DSLContext context;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = ((BearerAuthenticationToken) authentication).getToken();


        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");

        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(token)).fetchOneInto(mobileTable);
        if (mobileRecord == null) {
            throw new BadCredentialsException("bearer token " + token + " is not valid");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Date dateSeen = new Date();
        mobileRecord.setDateSeen(dateSeen);
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        mobileRecord.setClientIp(request.getRemoteAddr());
        mobileRecord.update();

        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getUserId())).fetchOneInto(userTable);

        if (userRecord == null) {
            throw new BadCredentialsException("bearer token " + token + " is not valid");
        }

        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(mobileRecord.getClientId())).fetchOneInto(clientTable);
        if (clientRecord == null) {
            throw new BadCredentialsException("bearer token " + token + " is not valid");
        }

        ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        if (applicationRecord == null) {
            throw new BadCredentialsException("bearer token " + token + " is not valid");
        }

        String username = userRecord.getLogin();

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roleRecord != null) {
            authorities.add(new SimpleGrantedAuthority(roleRecord.getName()));
        }

        return new BearerAuthenticationToken(token, username, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (BearerAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public DSLContext getContext() {
        return context;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }
}

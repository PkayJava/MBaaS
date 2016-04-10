package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.jooq.enums.UserStatusEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.GrantTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
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
        String accessToken = ((BearerAuthenticationToken) authentication).getToken();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");

        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
        if (mobileRecord == null) {
            throw new BadCredentialsException("bearer " + accessToken + " is not valid");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Date dateSeen = new Date();
        mobileRecord.setDateSeen(dateSeen);
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        String clientId = request.getHeader("X-FORWARDED-FOR");
        if (clientId == null && !"".equals(clientId)) {
            clientId = request.getRemoteAddr();
        }
        mobileRecord.setClientIp(clientId);
        mobileRecord.update();

        DateTime dateTime = new DateTime(mobileRecord.getDateTokenIssued());
        dateTime = dateTime.plusSeconds(mobileRecord.getTimeToLive());
        if (dateTime.isBeforeNow()) {
            throw new CredentialsExpiredException("bearer " + accessToken + " is expired");
        }

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        String principal = null;

        List<GrantedAuthority> authorities = new ArrayList<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (GrantTypeEnum.Authorization.getLiteral().equals(mobileRecord.getGrantType())) {
            UserTable userTable = Tables.USER.as("userTable");
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(userRecord.getStatus())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = userRecord.getLogin();
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord != null) {
                authorities.add(new SimpleGrantedAuthority(roleRecord.getName()));
            }
            ClientTable clientTable = Tables.CLIENT.as("clientTable");
            ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(mobileRecord.getClientId())).fetchOneInto(clientTable);
            if (clientRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(clientRecord.getSecurity())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(applicationRecord.getSecurity())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            authorities.add(new SimpleGrantedAuthority(configuration.getString(Constants.ROLE_OAUTH2_AUTHORIZATION)));
        } else if (GrantTypeEnum.Password.getLiteral().equals(mobileRecord.getGrantType())) {
            UserTable userTable = Tables.USER.as("userTable");
            UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(userRecord.getStatus())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = userRecord.getLogin();
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            if (roleRecord != null) {
                authorities.add(new SimpleGrantedAuthority(roleRecord.getName()));
            }
            authorities.add(new SimpleGrantedAuthority(configuration.getString(Constants.ROLE_OAUTH2_PASSWORD)));
        } else if (GrantTypeEnum.Implicit.getLiteral().equals(mobileRecord.getGrantType())) {
            ClientTable clientTable = Tables.CLIENT.as("clientTable");
            ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(mobileRecord.getClientId())).fetchOneInto(clientTable);
            if (clientRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(clientRecord.getSecurity())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(applicationRecord.getSecurity())) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = configuration.getString(Constants.ROLE_OAUTH2_IMPLICIT);
            authorities.add(new SimpleGrantedAuthority(principal));
        } else if (GrantTypeEnum.Client.getLiteral().equals(mobileRecord.getGrantType())) {
            principal = configuration.getString(Constants.ROLE_OAUTH2_CLIENT);
            authorities.add(new SimpleGrantedAuthority(principal));
        }

        return new BearerAuthenticationToken(accessToken, principal, null, authorities);
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

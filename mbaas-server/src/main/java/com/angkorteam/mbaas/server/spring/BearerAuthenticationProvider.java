package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.plain.enums.GrantTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Map;

/**
 * Created by socheat on 3/27/16.
 */
public class BearerAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private DSLContext context = null;

    private ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource = null;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        JdbcTemplate jdbcTemplate = null;
        String applicationSecret = request.getHeader("Application-Secret");
        {
            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.SECRET.eq(applicationSecret)).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                throw new BadCredentialsException("application secret " + applicationSecret + " is not valid");
            }
            jdbcTemplate = applicationDataSource.getJdbcTemplate(applicationRecord.getCode());
        }

        String accessToken = ((BearerAuthenticationToken) authentication).getToken();

        Map<String, Object> mobileRecord = null;
        mobileRecord = jdbcTemplate.queryForMap("SELECT * FROM mobile WHERE access_token = ?", accessToken);
        if (mobileRecord == null) {
            throw new BadCredentialsException("bearer " + accessToken + " is not valid");
        }

        Date dateSeen = new Date();
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null && !"".equals(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        jdbcTemplate.update("UPDATE mobile SET date_seen = ?, client_ip = ?, user_agent = ? WHERE mobile_id = ?", dateSeen, clientIp, userAgent, mobileRecord.get("mobile_id"));

        DateTime dateTime = new DateTime(((Date) mobileRecord.get("access_token_issued_date")).getTime());
        dateTime = dateTime.plusSeconds((int) mobileRecord.get("time_to_live"));
        if (dateTime.isBeforeNow()) {
            throw new CredentialsExpiredException("bearer " + accessToken + " is expired");
        }
        String principal = null;

        List<GrantedAuthority> authorities = new ArrayList<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String grantType = (String) mobileRecord.get("grant_type");
        if (GrantTypeEnum.Authorization.getLiteral().equals(grantType)) {
            Map<String, Object> userRecord = null;
            userRecord = jdbcTemplate.queryForMap("SELECT * FROM application_user WHERE application_user_id = ?", mobileRecord.get("application_user_id"));
            if (userRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(userRecord.get("status"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = (String) userRecord.get("login");
            Map<String, Object> roleRecord = null;
            roleRecord = jdbcTemplate.queryForMap("SELECT * FROM role WHERE role_id = ?", userRecord.get("role_id"));
            if (roleRecord != null) {
                authorities.add(new SimpleGrantedAuthority((String) roleRecord.get("name")));
            }

            Map<String, Object> clientRecord = null;
            clientRecord = jdbcTemplate.queryForMap("SELECT * FROM client WHERE client_id = ?", mobileRecord.get("client_id"));
            if (clientRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(clientRecord.get("security"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq((String) clientRecord.get("application_id"))).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(applicationRecord.get("security"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            authorities.add(new SimpleGrantedAuthority(configuration.getString(Constants.ROLE_OAUTH2_AUTHORIZATION)));
        } else if (GrantTypeEnum.Password.getLiteral().equals(grantType)) {
            Map<String, Object> userRecord = null;
            userRecord = jdbcTemplate.queryForMap("SELECT * FROM application_user WHERE application_user_id = ?", mobileRecord.get("application_user_id"));
            if (userRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(userRecord.get("status"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = (String) userRecord.get("login");
            Map<String, Object> roleRecord = null;
            roleRecord = jdbcTemplate.queryForMap("SELECT * FROM role WHERE role_id = ?", userRecord.get("role_id"));
            if (roleRecord != null) {
                authorities.add(new SimpleGrantedAuthority((String) roleRecord.get("name")));
            }
            authorities.add(new SimpleGrantedAuthority(configuration.getString(Constants.ROLE_OAUTH2_PASSWORD)));
        } else if (GrantTypeEnum.Implicit.getLiteral().equals(grantType)) {
            Map<String, Object> clientRecord = null;
            clientRecord = jdbcTemplate.queryForMap("SELECT * FROM client WHERE client_id = ?", mobileRecord.get("client_id"));
            if (clientRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(clientRecord.get("security"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq((String) clientRecord.get("application_id"))).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            if (SecurityEnum.Denied.getLiteral().equals(applicationRecord.get("security"))) {
                throw new BadCredentialsException("bearer " + accessToken + " is not valid");
            }
            principal = configuration.getString(Constants.ROLE_OAUTH2_IMPLICIT);
            authorities.add(new SimpleGrantedAuthority(principal));
        } else if (GrantTypeEnum.Client.getLiteral().equals(grantType)) {
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

    public ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        return applicationDataSource;
    }

    public void setApplicationDataSource(ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource) {
        this.applicationDataSource = applicationDataSource;
    }
}

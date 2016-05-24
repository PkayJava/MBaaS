package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import org.jooq.DSLContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by socheat on 4/10/16.
 */
public class IdentityHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private DSLContext context = null;

    private ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(com.angkorteam.mbaas.plain.Identity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String applicationSecret = webRequest.getHeader("Application-Secret");
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String userAgent = webRequest.getHeader(HttpHeaders.USER_AGENT);
        String clientId = webRequest.getHeader("client_id");
        String clientSecret = webRequest.getHeader("client_secret");
        String remoteIp;
        String mobileId = null;
        String mbaasUserId = null;
        String applicationCode = null;
        String applicationUserId = null;
        String applicationId = null;
        String accessToken = null;
        String appVersion = webRequest.getHeader("app_version");
        String sdkVersion = webRequest.getHeader("sdk_version");

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        JdbcTemplate jdbcTemplate = null;
        {
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.SECRET.eq(applicationSecret)).fetchOneInto(applicationTable);
            if (applicationRecord != null) {
                jdbcTemplate = this.applicationDataSource.getJdbcTemplate(applicationRecord.getCode());
                applicationCode = applicationRecord.getCode();
            }
        }

        if (authorization != null && !"".equals(authorization) && authorization.length() >= 7 && authorization.substring(0, 6).toLowerCase().equals("bearer")) {
            accessToken = authorization.substring(7);
        }

        Map<String, Object> mobileRecord = null;
        if (jdbcTemplate != null && accessToken != null && !"".equals(accessToken)) {
            mobileRecord = jdbcTemplate.queryForMap("SELECT * FROM mobile WHERE access_token = ?", accessToken);
        }

        remoteIp = webRequest.getHeader("X-FORWARDED-FOR");
        if (remoteIp == null) {
            remoteIp = webRequest.getNativeRequest(HttpServletRequest.class).getRemoteAddr();
        }

        if (mobileRecord != null) {
            mobileId = (String) mobileRecord.get("mobile_id");
            if (clientId != null && !"".equals(clientId)) {
                clientId = (String) mobileRecord.get("client_id");
            }
            mbaasUserId = (String) mobileRecord.get("mbaas_user_id");
            applicationId = (String) mobileRecord.get("application_id");
            applicationUserId = (String) mobileRecord.get("application_user_id");
        }

        if (mbaasUserId == null || "".equals(mbaasUserId)) {
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(applicationId)).fetchOneInto(applicationTable);
            if (applicationRecord != null) {
                mbaasUserId = applicationRecord.getMbaasUserId();
            }
        }

        Identity identity = new Identity(mbaasUserId, applicationUserId, applicationId, applicationCode, clientId, clientSecret, mobileId, userAgent, remoteIp, accessToken, appVersion, sdkVersion);
        return identity;
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

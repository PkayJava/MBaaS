package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import org.jooq.DSLContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 4/10/16.
 */
public class IdentityHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private DSLContext context;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(com.angkorteam.mbaas.plain.Identity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String userAgent = webRequest.getHeader(HttpHeaders.USER_AGENT);
        String clientId = webRequest.getHeader("client_id");
        String clientSecret = webRequest.getHeader("client_secret");
        String remoteIp;
        String mobileId = null;
        String userId = null;
        String applicationId = null;
        String accessToken = null;
        String appVersion = webRequest.getHeader("app_version");
        String sdkVersion = webRequest.getHeader("sdk_version");
        if (authorization != null && !"".equals(authorization) && authorization.length() >= 7 && authorization.substring(0, 6).toLowerCase().equals("bearer")) {
            accessToken = authorization.substring(7);
        }
        MobileRecord mobileRecord = null;
        if (accessToken != null && !"".equals(accessToken)) {
            mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
        }

        remoteIp = webRequest.getHeader("X-FORWARDED-FOR");
        if (remoteIp == null) {
            remoteIp = webRequest.getNativeRequest(HttpServletRequest.class).getRemoteAddr();
        }

        if (mobileRecord != null) {
            mobileId = mobileRecord.getMobileId();
            if (clientId != null && !"".equals(clientId)) {
                clientId = mobileRecord.getClientId();
            }
            userId = mobileRecord.getOwnerUserId();
            applicationId = mobileRecord.getApplicationId();
        }

        if (userId == null || "".equals(userId)) {
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(applicationId)).fetchOneInto(applicationTable);
            if (applicationRecord != null) {
                userId = applicationRecord.getOwnerUserId();
            }
        }

        Identity identity = new Identity(userId, applicationId, clientId, clientSecret, mobileId, userAgent, remoteIp, accessToken, appVersion, sdkVersion);
        return identity;
    }

    public DSLContext getContext() {
        return context;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }
}

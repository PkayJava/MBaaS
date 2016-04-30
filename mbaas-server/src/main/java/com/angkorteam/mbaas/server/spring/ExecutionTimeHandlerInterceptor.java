package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.model.entity.tables.records.NetworkRecord;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.server.MBaaS;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ExecutionTimeHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    private Gson gson;

    private DSLContext context;

    private final Map<String, Long> executions = new java.util.HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Boolean maintenance = configuration.getBoolean(Constants.MAINTENANCE, false);
        String id = request.getSession(true).getId();
        if (maintenance) {
            UnknownResponse responseBody = ResponseUtils.unknownResponse(request, HttpStatus.SERVICE_UNAVAILABLE);
            byte[] json = gson.toJson(responseBody).getBytes();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setContentLength(json.length);
            response.getOutputStream().write(json);
            return false;
        }
        executions.put(id, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String id = request.getSession(true).getId();
        Long execution = executions.get(id);
        executions.put(id, System.currentTimeMillis() - execution);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String id = request.getSession(true).getId();
        double consume = executions.remove(id);
        LOGGER.info("{} consumed {} ss", request.getRequestURI(), consume / 1000f);
        NetworkRecord networkRecord = context.newRecord(Tables.NETWORK);
        networkRecord.setNetworkId(java.util.UUID.randomUUID().toString());
        networkRecord.setDateCreated(new Date());
        networkRecord.setUri(request.getRequestURI());
        networkRecord.setConsume(consume);
        networkRecord.setRemoteIp(request.getRemoteAddr());
        networkRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.toUpperCase().startsWith("BEARER ")) {
            String accessToken = authorization.substring(7);
            MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
            MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
            if (mobileRecord != null) {
                networkRecord.setMobileId(mobileRecord.getMobileId());
                networkRecord.setUserId(mobileRecord.getOwnerUserId());
                networkRecord.setApplicationId(mobileRecord.getApplicationId());
                networkRecord.setClientId(mobileRecord.getClientId());
            }
        }
        networkRecord.store();
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }

    public DSLContext getContext() {
        return context;
    }
}

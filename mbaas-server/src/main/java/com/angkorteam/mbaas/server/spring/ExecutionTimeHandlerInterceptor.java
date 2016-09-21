package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.NetworkRecord;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.server.MBaaS;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ExecutionTimeHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    private Gson gson;

    private DSLContext context;

    private ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

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
        if (ex != null) {
            ex.printStackTrace();
            throw ex;
        }
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
        String applicationSecret = request.getHeader("Application-Secret");
        ApplicationRecord applicationRecord = null;
        if (applicationSecret != null && !"".equals(applicationSecret)) {
            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
            applicationRecord = this.context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.SECRET.eq(applicationSecret)).fetchOneInto(applicationTable);
        }
        JdbcTemplate jdbcTemplate = null;
        if (applicationRecord != null) {
            String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
            jdbcTemplate = this.applicationDataSource.getJdbcTemplate(applicationRecord.getCode(), jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jdbcTemplate != null && authorization != null && authorization.toUpperCase().startsWith("BEARER ")) {
            String accessToken = authorization.substring(7);
            Map<String, Object> mobileRecord = null;
            mobileRecord = jdbcTemplate.queryForMap("SELECT * FROM mobile WHERE access_token = ?", accessToken);
            if (mobileRecord != null) {
                networkRecord.setMobileId((String) mobileRecord.get("mobile_id"));
                networkRecord.setMbaasUserId((String) mobileRecord.get("mbaas_user_id"));
                networkRecord.setApplicationUserId((String) mobileRecord.get("application_user_id"));
                networkRecord.setApplicationId((String) mobileRecord.get("application_id"));
                networkRecord.setClientId((String) mobileRecord.get("client_id"));
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

    public ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        return applicationDataSource;
    }

    public void setApplicationDataSource(ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource) {
        this.applicationDataSource = applicationDataSource;
    }
}

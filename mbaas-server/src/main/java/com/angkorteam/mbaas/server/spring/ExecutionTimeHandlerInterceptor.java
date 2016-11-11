package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.server.MBaaS;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.System;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ExecutionTimeHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    private Gson gson;

    private final Map<String, Long> executions = new java.util.HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System system = Spring.getBean(System.class);
        Configuration configuration = system.getConfiguration();
        Boolean maintenance = configuration.getBoolean(Configuration.MAINTENANCE, false);
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
        executions.put(id, java.lang.System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String id = request.getSession(true).getId();
        Long execution = executions.get(id);
        executions.put(id, java.lang.System.currentTimeMillis() - execution);
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
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}

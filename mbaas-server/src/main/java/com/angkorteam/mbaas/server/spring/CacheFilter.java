package com.angkorteam.mbaas.server.spring;

import org.springframework.web.filter.ServletContextRequestLoggingFilter;

import javax.servlet.ServletException;

/**
 * Created by socheat on 8/29/16.
 */
public class CacheFilter extends ServletContextRequestLoggingFilter {

    @Override
    protected void initFilterBean() throws ServletException {
        this.setIncludePayload(true);
    }

}

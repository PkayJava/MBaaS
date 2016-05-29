package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.Jdbc;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by socheat on 5/28/16.
 */
public class LogicResourceStream implements IResourceStream {

    public LogicResourceStream() {
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public Bytes length() {
        String pageId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        Session session = (Session) Session.get();
        String applicationCode = session.getApplicationCode();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(applicationCode);
        String html = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.HTML + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", String.class, pageId);
        Bytes bytes = null;
        try {
            bytes = Bytes.bytes(html.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        String pageId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        Session session = (Session) Session.get();
        String applicationCode = session.getApplicationCode();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(applicationCode);
        String html = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.HTML + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", String.class, pageId);
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return inputStream;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public String getStyle() {
        return null;
    }

    @Override
    public void setStyle(String style) {

    }

    @Override
    public String getVariation() {
        return null;
    }

    @Override
    public void setVariation(String variation) {

    }

    @Override
    public Time lastModifiedTime() {
        String pageId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        Session session = (Session) Session.get();
        String applicationCode = session.getApplicationCode();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(applicationCode);
        Date dateModified = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.DATE_MODIFIED + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", Date.class, pageId);
        return Time.millis(dateModified.getTime());
    }
}

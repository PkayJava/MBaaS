package com.angkorteam.mbaas.server.nashorn.wicket.provider;

import com.angkorteam.framework.extension.wicket.markup.html.FullCalendarItem;
import com.angkorteam.framework.extension.wicket.markup.html.FullCalendarProvider;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import com.google.gson.Gson;
import org.apache.wicket.WicketRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 6/26/16.
 */
public class NashornFullCalendarProvider extends FullCalendarProvider {

    private final Factory factory;

    private final String id;

    private final String script;

    private final String applicationCode;

    public NashornFullCalendarProvider(Factory factory, String id, String script, String applicationCode) {
        this.factory = factory;
        this.id = id;
        this.script = script;
        this.applicationCode = applicationCode;
    }

    @Override
    public List<FullCalendarItem> query(Date date, Date date1) {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null && !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
                throw new WicketRuntimeException(e);
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            return (List<FullCalendarItem>) invocable.invokeFunction(this.id + "__query", this.factory, jdbcTemplate);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__query(factory, jdbcTemplate){} is missing");
        } catch (EmptyResultDataAccessException e) {
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }
}

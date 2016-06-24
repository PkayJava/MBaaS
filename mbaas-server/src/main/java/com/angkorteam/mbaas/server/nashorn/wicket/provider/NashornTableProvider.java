package com.angkorteam.mbaas.server.nashorn.wicket.provider;

import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.TableLike;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornTableProvider extends SqlProvider {

    private final Factory factory;

    private final String id;

    private final String script;

    private final String applicationCode;

    public NashornTableProvider(Factory factory, String id, String script, String applicationCode) {
        this.script = script;
        this.id = id;
        this.factory = factory;
        this.applicationCode = applicationCode;
    }

    @Override
    protected TableLike<?> from() {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

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
            return (TableLike<?>) invocable.invokeFunction(this.id + "__from", jdbcTemplate);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__from(jdbcTemplate){} is missing");
        } catch (EmptyResultDataAccessException e) {
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    protected List<Condition> where() {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

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
            return (List<Condition>) invocable.invokeFunction(this.id + "__where", jdbcTemplate);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__where(jdbcTemplate){} is missing");
        } catch (EmptyResultDataAccessException e) {
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    protected List<Condition> having() {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

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
            return (List<Condition>) invocable.invokeFunction(this.id + "__having", jdbcTemplate);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__having(jdbcTemplate){} is missing");
        } catch (EmptyResultDataAccessException e) {
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    protected DSLContext getDSLContext() {
        return ApplicationUtils.getApplication().getDSLContext(this.applicationCode);
    }
}

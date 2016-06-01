package com.angkorteam.mbaas.server.page.flow;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
@Mount("/flow")
public class FlowPage extends MasterPage {

    private String pageId;

    private Factory factory;

    private Map<String, Object> userModel;

    private String script;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        this.userModel = new HashMap<>();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);
        this.script = (String) pageRecord.get(Jdbc.Page.JAVASCRIPT);
        this.factory = new Factory(this, getApplicationCode(), this.script, this.userModel);
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        IOnInitialize iOnInitialize = invocable.getInterface(IOnInitialize.class);
        if (iOnInitialize != null) {
            iOnInitialize.onInitialize(RequestCycle.get(), jdbcTemplate, this.factory, this.userModel);
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        IOnBeforeRender iOnBeforeRender = invocable.getInterface(IOnBeforeRender.class);
        if (iOnBeforeRender != null) {
            JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
            iOnBeforeRender.onBeforeRender(RequestCycle.get(), jdbcTemplate, this.factory, this.userModel);
        }
    }

    @Override
    public String getVariation() {
        return this.pageId;
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> userModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> userModel);

    }
}

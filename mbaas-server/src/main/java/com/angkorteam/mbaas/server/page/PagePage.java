package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
@Mount("/page")
public class PagePage extends MasterPage {

    private Factory factory;

    private Map<String, Object> userModel;

    private Disk disk;

    private String script;

    private boolean stage;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.stage = getPageParameters().get("stage").toBoolean(false);
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        String pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        request.setAttribute("pageId", pageId);
        this.userModel = new HashMap<>();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", pageId);
        this.script = (String) (stage ? pageRecord.get(Jdbc.Page.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.Page.JAVASCRIPT));
        this.disk = new Disk(getApplicationCode(), getSession().getApplicationUserId());
        this.factory = new Factory(this, this.disk, getApplicationCode(), this.script, this.userModel);
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        IOnInitialize iOnInitialize = invocable.getInterface(IOnInitialize.class);
        if (iOnInitialize != null) {
            iOnInitialize.onInitialize(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.userModel);
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
            iOnBeforeRender.onBeforeRender(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.userModel);
        }
    }

    @Override
    public String getVariation() {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        if (this.stage) {
            return request.getAttribute("pageId") + "-stage";
        } else {
            return (String) request.getAttribute("pageId");
        }
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> userModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> userModel);

    }
}

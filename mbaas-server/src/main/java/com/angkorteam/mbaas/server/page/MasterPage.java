package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
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
@Mount("/master")
public class MasterPage extends com.angkorteam.mbaas.server.wicket.MasterPage {

    private Factory factory;

    private Map<String, Object> pageModel;

    private Disk disk;

    private String script;

    private boolean stage;

    private String masterPageId;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.pageModel = new HashMap<>();
        this.stage = getPageParameters().get("stage").toBoolean(false);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if (this instanceof PagePage) {
            String pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
            this.masterPageId = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.MASTER_PAGE_ID + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", String.class, pageId);
        } else {
            this.masterPageId = getRequest().getQueryParameters().getParameterValue("masterPageId").toString("");
        }
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", this.masterPageId);
        this.script = (String) (this.stage ? pageRecord.get(Jdbc.MasterPage.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.MasterPage.JAVASCRIPT));
        this.disk = new Disk(getApplicationCode(), getSession().getApplicationUserId());
        this.factory = new Factory(this, this.disk, getApplicationCode(), this.script, this.pageModel);
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        IOnInitialize iOnInitialize = invocable.getInterface(IOnInitialize.class);
        if (iOnInitialize != null) {
            iOnInitialize.onInitialize(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel);
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
            iOnBeforeRender.onBeforeRender(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel);
        }
    }

    @Override
    public String getVariation() {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        if (this.stage) {
            request.setAttribute("masterPageId", this.masterPageId);
            return this.masterPageId + "-stage";
        } else {
            request.setAttribute("masterPageId", this.masterPageId);
            return this.masterPageId;
        }
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }
}

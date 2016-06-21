package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
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
@Mount("/page")
public class PagePage extends MasterPage implements IMarkupResourceStreamProvider {

    private Factory factory;

    private Map<String, Object> pageModel;

    private Disk disk;

    private String pageId;

    private boolean stage;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Session session = getSession();
        String applicationUserId = session.getApplicationUserId();
        this.stage = getPageParameters().get("stage").toBoolean(false);
        this.pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        this.pageModel = new HashMap<>();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);
        String script = (String) (stage ? pageRecord.get(Jdbc.Page.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.Page.JAVASCRIPT));
        this.disk = new Disk(getApplicationCode(), getSession().getApplicationUserId());
        this.factory = new Factory(applicationUserId, this, this.disk, getApplicationCode(), script, this.stage, this.pageModel);
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(script);
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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);
        String script = (String) (stage ? pageRecord.get(Jdbc.Page.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.Page.JAVASCRIPT));
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        IOnBeforeRender iOnBeforeRender = invocable.getInterface(IOnBeforeRender.class);
        if (iOnBeforeRender != null) {
            iOnBeforeRender.onBeforeRender(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel);
        }
    }

    @Override
    public String getVariation() {
        if (this.stage) {
            return this.pageId + "-stage";
        } else {
            return this.pageId;
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (containerClass == PagePage.class) {
            JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
            Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);
            String html = (String) (this.stage ? pageRecord.get(Jdbc.Page.STAGE_HTML) : pageRecord.get(Jdbc.Page.HTML));
            StringResourceStream stream = new StringResourceStream(html);
            return stream;
        } else {
            return super.getMarkupResourceStream(container, containerClass);
        }
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }
}

package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
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
@Mount("/master")
public class MasterPage extends com.angkorteam.mbaas.server.wicket.MasterPage implements IMarkupResourceStreamProvider {

    private Factory factory;

    private Map<String, Object> pageModel;

    private Disk disk;

    private boolean stage;

    private String masterPageId;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Session session = (Session) getSession();
        String applicationUserId = session.getApplicationUserId();
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
        String script = (String) (this.stage ? pageRecord.get(Jdbc.MasterPage.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.MasterPage.JAVASCRIPT));
        this.disk = new Disk(getApplicationCode(), getSession().getApplicationUserId());
        this.factory = new Factory(applicationUserId, this, this.disk, getApplicationCode(), script, this.stage, this.pageModel);
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
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
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", this.masterPageId);
        String script = (String) (this.stage ? pageRecord.get(Jdbc.MasterPage.STAGE_JAVASCRIPT) : pageRecord.get(Jdbc.MasterPage.JAVASCRIPT));
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
            return this.masterPageId + "-stage";
        } else {
            return this.masterPageId;
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (containerClass == MasterPage.class) {
            JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
            Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", this.masterPageId);
            String html = (String) (this.stage ? pageRecord.get(Jdbc.MasterPage.STAGE_HTML) : pageRecord.get(Jdbc.MasterPage.HTML));
            StringResourceStream stream = new StringResourceStream(html);
            return stream;
        } else {
            throw new WicketRuntimeException("markup not found");
        }
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel);

    }
}

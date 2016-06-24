package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/16/16.
 */
public class BlockPanel extends Panel implements IMarkupResourceStreamProvider {

    private final String code;

    private String blockId;

    private final Map<String, Object> pageModel;

    private MapModel<String, Object> blockModel;

    private Disk disk;

    private boolean stage;

    private String script;

    private Factory factory;

    public BlockPanel(String id, String code, boolean stage, Map<String, Object> pageModel, MapModel<String, Object> blockModel) {
        super(id, blockModel);
        this.code = code;
        this.stage = stage;
        this.pageModel = pageModel;
        this.blockModel = blockModel;
        Session session = (Session) getSession();
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(session.getApplicationCode());
        Map<String, Object> blockRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.BLOCK + " WHERE " + Jdbc.Block.CODE + " = ?", this.code);
        this.blockId = (String) blockRecord.get(Jdbc.Block.BLOCK_ID);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Session session = (Session) getSession();
        String applicationCode = session.getApplicationCode();
        String applicationUserId = session.getApplicationUserId();
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(session.getApplicationCode());
        Map<String, Object> blockRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.BLOCK + " WHERE " + Jdbc.Block.CODE + " = ?", this.code);
        this.script = (String) (this.stage ? blockRecord.get(Jdbc.Block.STAGE_JAVASCRIPT) : blockRecord.get(Jdbc.Block.JAVASCRIPT));
        this.disk = new Disk(applicationCode, applicationUserId);
        this.factory = new Factory(applicationUserId, this, this.disk, applicationCode, this.script, this.stage, this.pageModel);
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        IOnInitialize iOnInitialize = invocable.getInterface(IOnInitialize.class);
        if (iOnInitialize != null) {
            iOnInitialize.onInitialize(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, this.blockModel.getObject());
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        Session session = (Session) getSession();
        IOnBeforeRender iOnBeforeRender = invocable.getInterface(IOnBeforeRender.class);
        if (iOnBeforeRender != null) {
            JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(session.getApplicationCode());
            iOnBeforeRender.onBeforeRender(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, this.blockModel.getObject());
        }
    }

    @Override
    public String getVariation() {
        if (this.stage) {
            return this.blockId + "-stage";
        } else {
            return this.blockId;
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (containerClass == BlockPanel.class) {
            Session session = (Session) getSession();
            JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(session.getApplicationCode());
            Map<String, Object> blockRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.BLOCK + " WHERE " + Jdbc.Block.BLOCK_ID + " = ?", this.blockId);
            String html = (String) (this.stage ? blockRecord.get(Jdbc.Block.STAGE_HTML) : blockRecord.get(Jdbc.Block.HTML));
            StringResourceStream stream = new StringResourceStream(html);
            return stream;
        } else {
            throw new WicketRuntimeException("markup not found");
        }
    }

    public interface IOnBeforeRender extends Serializable {

        void onBeforeRender(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, Map<String, Object> blockModel);

    }

    public interface IOnInitialize extends Serializable {

        void onInitialize(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, Map<String, Object> blockModel);

    }
}

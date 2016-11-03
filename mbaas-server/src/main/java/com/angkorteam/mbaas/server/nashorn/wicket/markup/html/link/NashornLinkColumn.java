//package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link;
//
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.angkorteam.mbaas.server.wicket.Session;
//import org.apache.wicket.WicketRuntimeException;
//import org.apache.wicket.markup.html.link.Link;
//import org.apache.wicket.request.cycle.RequestCycle;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.util.Map;
//
///**
// * Created by socheat on 6/12/16.
// */
//public class NashornLinkColumn extends Link<Map<String, Object>> {
//
//    private String script;
//
//    private Disk disk;
//
//    private Factory factory;
//
//    private String eventId;
//
//    private Map<String, Object> pageModel;
//
//    private Map<String, Object> itemModel;
//
//    public NashornLinkColumn(String id, Map<String, Object> pageModel, Map<String, Object> itemModel) {
//        this(id, id, pageModel, itemModel);
//    }
//
//    public NashornLinkColumn(String id, String eventId, Map<String, Object> pageModel, Map<String, Object> itemModel) {
//        super(id);
//        this.eventId = eventId;
//        this.pageModel = pageModel;
//        this.itemModel = itemModel;
//    }
//
//    @Override
//    public void onClick() {
//        Session session = (Session) Session.get();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
//
//        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
//        if (this.script != null && !"".equals(this.script)) {
//            try {
//                scriptEngine.eval(this.script);
//            } catch (ScriptException e) {
//                throw new WicketRuntimeException(e);
//            }
//        }
//        Invocable invocable = (Invocable) scriptEngine;
//        try {
//            invocable.invokeFunction(this.eventId + "__on_click", RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, this.itemModel);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.eventId + "__on_click(requestCycle, disk, jdbcTemplate, factory, pageModel, itemModel){} is missing");
//        }
//    }
//
//    public String getScript() {
//        return script;
//    }
//
//    public void setScript(String script) {
//        this.script = script;
//    }
//
//    public Disk getDisk() {
//        return disk;
//    }
//
//    public void setDisk(Disk disk) {
//        this.disk = disk;
//    }
//
//    public Factory getFactory() {
//        return factory;
//    }
//
//    public void setFactory(Factory factory) {
//        this.factory = factory;
//    }
//}

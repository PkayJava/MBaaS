//package com.angkorteam.mbaas.server.nashorn.wicket.ajax.form;
//
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.angkorteam.mbaas.server.wicket.Session;
//import org.apache.wicket.WicketRuntimeException;
//import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
//import org.apache.wicket.request.cycle.RequestCycle;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.util.Map;
//
///**
// * Created by socheat on 7/6/16.
// */
//public class NashornOnChangeAjaxBehavior extends OnChangeAjaxBehavior {
//
//    private String script;
//
//    private Factory factory;
//
//    private Disk disk;
//
//    private Map<String, Object> pageModel;
//
//    private String id;
//
//    private String eventId;
//
//    public NashornOnChangeAjaxBehavior(String id, String eventId) {
//        this.id = id;
//        this.eventId = eventId;
//    }
//
//    @Override
//    protected void onUpdate(AjaxRequestTarget target) {
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
//            invocable.invokeFunction(this.id + "__" + this.eventId, RequestCycle.get(), this.disk, jdbcTemplate, this.factory, target, this.pageModel);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__" + this.eventId + "(requestCycle, disk, jdbcTemplate, factory, target, pageModel){} is missing");
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
//    public Factory getFactory() {
//        return factory;
//    }
//
//    public void setFactory(Factory factory) {
//        this.factory = factory;
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
//    public Map<String, Object> getPageModel() {
//        return pageModel;
//    }
//
//    public void setPageModel(Map<String, Object> pageModel) {
//        this.pageModel = pageModel;
//    }
//}

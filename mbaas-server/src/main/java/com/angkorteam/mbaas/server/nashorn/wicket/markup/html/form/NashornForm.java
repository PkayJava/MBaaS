//package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;
//
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornFormValidator;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.angkorteam.mbaas.server.wicket.Session;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//import org.apache.wicket.WicketRuntimeException;
//import org.apache.wicket.markup.html.form.FormComponent;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.request.cycle.RequestCycle;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by socheat on 5/31/16.
// */
//public class NashornForm<T> extends org.apache.wicket.markup.html.form.Form<T> {
//
//    private String script;
//    private Map<String, Object> pageModel;
//    private Disk disk;
//    private Factory factory;
//
//    public NashornForm(String id) {
//        super(id);
//        setOutputMarkupId(true);
//    }
//
//    public NashornForm(String id, IModel<T> model) {
//        super(id, model);
//        setOutputMarkupId(true);
//    }
//
//    @Override
//    protected final void onSubmit() {
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
//            invocable.invokeFunction(getId() + "__on_submit", RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + getId() + "__on_submit(requestCycle, disk, jdbcTemplate, factory, pageModel){} is missing");
//        }
//    }
//
//    @Override
//    protected final void onError() {
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
//            invocable.invokeFunction(getId() + "__on_error", RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + getId() + "__on_error(requestCycle, disk, jdbcTemplate, factory, pageModel){} is missing");
//        }
//    }
//
//    public void registerValidator(String event, FormComponent<?>... formComponent) {
//        registerValidator(event, new HashMap<>(), formComponent);
//    }
//
//    public void registerValidator(String event, ScriptObjectMirror jsobject, FormComponent<?>... formComponent) {
//        Map<String, Object> params = new HashMap<>();
//        if (jsobject != null && !jsobject.isEmpty()) {
//            for (Map.Entry<String, Object> param : jsobject.entrySet()) {
//                params.put(param.getKey(), param.getValue());
//            }
//        }
//        registerValidator(event, params, formComponent);
//    }
//
//    public void registerValidator(String event, Map<String, Object> params, FormComponent<?>... formComponent) {
//        NashornFormValidator validator = new NashornFormValidator(getId(), event, this.script, formComponent, params);
//        super.add(validator);
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
//    public Map<String, Object> getPageModel() {
//        return pageModel;
//    }
//
//    public void setPageModel(Map<String, Object> pageModel) {
//        this.pageModel = pageModel;
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

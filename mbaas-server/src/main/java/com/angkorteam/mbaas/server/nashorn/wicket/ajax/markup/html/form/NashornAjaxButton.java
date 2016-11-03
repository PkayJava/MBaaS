//package com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.form;
//
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.angkorteam.mbaas.server.wicket.Session;
//import org.apache.wicket.WicketRuntimeException;
//import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.markup.html.form.Form;
//import org.apache.wicket.request.cycle.RequestCycle;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.util.Map;
//
///**
// * Created by socheat on 5/31/16.
// */
//public class NashornAjaxButton extends org.apache.wicket.ajax.markup.html.form.AjaxButton {
//
//    private String script;
//
//    private Map<String, Object> pageModel;
//
//    private Factory factory;
//
//    private Disk disk;
//
//    public NashornAjaxButton(String id) {
//        super(id);
//    }
//
//    public NashornAjaxButton(String id, Form<?> form) {
//        super(id, form);
//    }
//
//    @Override
//    protected final void onSubmit(AjaxRequestTarget target, Form<?> form) {
//        Session session = (Session) Session.get();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
//
//        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
//        if (this.script != null && !"".equals(this.script)) {
//            try {
//                scriptEngine.eval(this.script);
//            } catch (ScriptException e) {
//            }
//        }
//        Invocable invocable = (Invocable) scriptEngine;
//        try {
//            invocable.invokeFunction(getId() + "__on_submit", RequestCycle.get(), this.disk, jdbcTemplate, this.factory, target, form, this.pageModel);
//        } catch (ScriptException e) {
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + getId() + "__on_submit(requestCycle, disk, jdbcTemplate, factory, target, form, pageModel){} is missing");
//        } catch (EmptyResultDataAccessException e) {
//        }
//    }
//
//    @Override
//    protected final void onError(AjaxRequestTarget target, Form<?> form) {
//        Session session = (Session) Session.get();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
//
//        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
//        if (this.script != null && !"".equals(this.script)) {
//            try {
//                scriptEngine.eval(this.script);
//            } catch (ScriptException e) {
//            }
//        }
//        Invocable invocable = (Invocable) scriptEngine;
//        try {
//            invocable.invokeFunction(getId() + "__on_error", RequestCycle.get(), this.disk, jdbcTemplate, this.factory, target, form, this.pageModel);
//        } catch (ScriptException e) {
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + getId() + "__on_error(requestCycle, disk, jdbcTemplate, factory, target, form, pageModel){} is missing");
//        } catch (EmptyResultDataAccessException e) {
//        }
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
//}

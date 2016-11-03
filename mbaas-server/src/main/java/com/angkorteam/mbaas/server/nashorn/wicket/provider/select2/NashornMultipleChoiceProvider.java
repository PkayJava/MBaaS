//package com.angkorteam.mbaas.server.nashorn.wicket.provider.select2;
//
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.angkorteam.mbaas.server.wicket.Session;
//import com.google.gson.Gson;
//import org.apache.wicket.WicketRuntimeException;
//import org.apache.wicket.model.IModel;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 6/1/16.
// */
//public class NashornMultipleChoiceProvider extends MultipleChoiceProvider<Map<String, Object>> {
//
//    private final Factory factory;
//
//    private final String id;
//
//    private final String script;
//
//    private NashornChoiceRenderer renderer;
//
//    public NashornMultipleChoiceProvider(Factory factory, String id, String script, String idField, String displayField) {
//        this.factory = factory;
//        this.id = id;
//        this.script = script;
//        this.renderer = new NashornChoiceRenderer(idField, displayField);
//    }
//
//    @Override
//    public final List<Map<String, Object>> toChoices(List<String> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return null;
//        }
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
//            return (List<Map<String, Object>>) invocable.invokeFunction(this.id + "__to_choices", jdbcTemplate, ids.toArray());
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__to_choices(jdbcTemplate, ids){} is missing");
//        } catch (EmptyResultDataAccessException e) {
//            throw new WicketRuntimeException(e);
//        }
//    }
//
//    @Override
//    public final List<Option> query(String term, int page) {
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
//            return (List<Option>) invocable.invokeFunction(this.id + "__query", jdbcTemplate, this.factory, term, page);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__query(jdbcTemplate, factory, term, page){} is missing");
//        } catch (EmptyResultDataAccessException e) {
//            throw new WicketRuntimeException(e);
//        }
//    }
//
//    @Override
//    public final boolean hasMore(String term, int page) {
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
//            return (Boolean) invocable.invokeFunction(this.id + "__has_more", jdbcTemplate, term, page);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__has_more(jdbcTemplate, term, page){} is missing");
//        }
//    }
//
//    @Override
//    public final Gson getGson() {
//        return ApplicationUtils.getApplication().getGson();
//    }
//
//    @Override
//    public int size() {
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
//            return (int) invocable.invokeFunction(this.id + "__size", jdbcTemplate);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__size(jdbcTemplate){} is missing");
//        }
//    }
//
//    @Override
//    public Map<String, Object> get(int index) {
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
//            return (Map<String, Object>) invocable.invokeFunction(this.id + "__get", jdbcTemplate, index);
//        } catch (ScriptException e) {
//            throw new WicketRuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WicketRuntimeException("function " + this.id + "__get(jdbcTemplate, index){} is missing");
//        }
//    }
//
//    @Override
//    public Object getDisplayValue(Map<String, Object> object) {
//        return this.renderer.getDisplayValue(object);
//    }
//
//    @Override
//    public String getIdValue(Map<String, Object> object, int index) {
//        return this.renderer.getIdValue(object, index);
//    }
//
//    @Override
//    public Map<String, Object> getObject(String id, IModel<? extends List<? extends Map<String, Object>>> choices) {
//        return this.renderer.getObject(id, choices);
//    }
//
//}

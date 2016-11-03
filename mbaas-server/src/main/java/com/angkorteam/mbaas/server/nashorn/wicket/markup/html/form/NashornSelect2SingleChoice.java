//package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;
//
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
//import com.angkorteam.mbaas.server.nashorn.Disk;
//import com.angkorteam.mbaas.server.nashorn.Factory;
//import com.angkorteam.mbaas.server.nashorn.wicket.ajax.form.NashornOnChangeAjaxBehavior;
//import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//import org.apache.wicket.model.IModel;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by socheat on 6/1/16.
// */
//public class NashornSelect2SingleChoice extends Select2SingleChoice<Map<String, Object>> {
//
//    private String script;
//
//    private Factory factory;
//
//    private Disk disk;
//
//    private Map<String, Object> pageModel;
//
//    public NashornSelect2SingleChoice(String id, IModel<Map<String, Object>> model, SingleChoiceProvider<Map<String, Object>> provider) {
//        super(id, model, provider);
//        setOutputMarkupId(true);
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
//    public void registerValidator(String event) {
//        registerValidator(event, new HashMap<>());
//    }
//
//    public void registerValidator(String event, ScriptObjectMirror js) {
//        Map<String, Object> params = new HashMap<>();
//        if (js != null && !js.isEmpty()) {
//            for (Map.Entry<String, Object> param : js.entrySet()) {
//                params.put(param.getKey(), param.getValue());
//            }
//        }
//        registerValidator(event, params);
//    }
//
//    public void registerUpdateBehavior(String event) {
//        NashornOnChangeAjaxBehavior behavior = new NashornOnChangeAjaxBehavior(getId(), event);
//        behavior.setDisk(this.disk);
//        behavior.setFactory(this.factory);
//        behavior.setPageModel(this.pageModel);
//        behavior.setScript(this.script);
//        super.add(behavior);
//    }
//
//    public void registerValidator(String event, Map<String, Object> params) {
//        NashornValidator validator = new NashornValidator(getId(), event, this.script, params);
//        super.add(validator);
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

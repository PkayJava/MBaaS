//package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel;
//
//import org.apache.wicket.MarkupContainer;
//import org.apache.wicket.markup.IMarkupResourceStreamProvider;
//import org.apache.wicket.markup.html.form.CheckBox;
//import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.model.PropertyModel;
//import org.apache.wicket.util.resource.IResourceStream;
//import org.apache.wicket.util.resource.StringResourceStream;
//
//import java.util.Map;
//
///**
// * Created by socheat on 6/18/16.
// */
//public class CheckBoxPanel extends Panel implements IMarkupResourceStreamProvider {
//
//    private CheckBox checkBox;
//
//    private Map<String, Object> checksModel;
//    private String identity;
//
//    public CheckBoxPanel(String id, String identity, Map<String, Object> checksModel) {
//        super(id);
//        this.identity = identity;
//        this.checksModel = checksModel;
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//        if (!checksModel.containsKey(this.identity)) {
//            checksModel.put(this.identity, false);
//        }
//        this.checkBox = new CheckBox("checkBox", new PropertyModel<>(this.checksModel, this.identity));
//        this.add(this.checkBox);
//    }
//
//    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
//        return new StringResourceStream("<wicket:panel><input wicket:id='checkBox' type='checkbox'/></wicket:panel>");
//    }
//}

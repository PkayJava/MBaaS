//package com.angkorteam.mbaas.server.renderer;
//
//import org.apache.wicket.markup.html.form.IChoiceRenderer;
//import org.apache.wicket.model.IModel;
//
//import java.util.List;
//
///**
// * Created by socheat on 5/25/16.
// */
//public class TableRenderer implements IChoiceRenderer<String> {
//
//    @Override
//    public Object getDisplayValue(String object) {
//        return object;
//    }
//
//    @Override
//    public String getIdValue(String object, int index) {
//        return object;
//    }
//
//    @Override
//    public String getObject(String id, IModel<? extends List<? extends String>> choices) {
//        for (String choice : choices.getObject()) {
//            if (choice.equals(id)) {
//                return id;
//            }
//        }
//        return null;
//    }
//}

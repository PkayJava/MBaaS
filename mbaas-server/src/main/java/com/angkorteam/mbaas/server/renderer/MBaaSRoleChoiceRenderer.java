//package com.angkorteam.mbaas.server.renderer;
//
//import com.angkorteam.mbaas.model.entity.tables.pojos.MbaasRolePojo;
//import org.apache.wicket.markup.html.form.IChoiceRenderer;
//import org.apache.wicket.model.IModel;
//
//import java.util.List;
//
///**
// * Created by socheat on 3/3/16.
// */
//public class MBaaSRoleChoiceRenderer implements IChoiceRenderer<MbaasRolePojo> {
//
//    @Override
//    public Object getDisplayValue(MbaasRolePojo object) {
//        return object.getName();
//    }
//
//    @Override
//    public String getIdValue(MbaasRolePojo object, int index) {
//        return object.getMbaasRoleId();
//    }
//
//    @Override
//    public MbaasRolePojo getObject(String id, IModel<? extends List<? extends MbaasRolePojo>> choices) {
//        for (MbaasRolePojo choice : choices.getObject()) {
//            if (choice.getMbaasRoleId().equals(id)) {
//                return choice;
//            }
//        }
//        return null;
//    }
//}

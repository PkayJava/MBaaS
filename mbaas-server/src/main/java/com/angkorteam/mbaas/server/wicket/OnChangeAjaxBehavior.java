//package com.angkorteam.mbaas.server.wicket;
//
//import org.apache.wicket.ajax.AjaxRequestTarget;
//
//import java.io.Serializable;
//
///**
// * Created by socheat on 7/14/16.
// */
//public class OnChangeAjaxBehavior extends org.apache.wicket.ajax.form.OnChangeAjaxBehavior {
//
//    private IOnUpdate onUpdate;
//
//    public OnChangeAjaxBehavior() {
//    }
//
//    public OnChangeAjaxBehavior(IOnUpdate onUpdate) {
//        this.onUpdate = onUpdate;
//    }
//
//    public IOnUpdate getOnUpdate() {
//        return onUpdate;
//    }
//
//    public void setOnUpdate(IOnUpdate onUpdate) {
//        this.onUpdate = onUpdate;
//    }
//
//    @Override
//    protected void onUpdate(AjaxRequestTarget target) {
//        if (this.onUpdate != null) {
//            this.onUpdate.onUpdate(target);
//        }
//    }
//
//    public interface IOnUpdate extends Serializable {
//        void onUpdate(AjaxRequestTarget target);
//    }
//}
package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Created by socheat on 7/14/16.
 */
public class OnChangeAjaxBehavior extends org.apache.wicket.ajax.form.OnChangeAjaxBehavior {

    private IOnUpdate iOnUpdate;

    private String message;

    public OnChangeAjaxBehavior() {
    }

    public IOnUpdate getiOnUpdate() {
        return iOnUpdate;
    }

    public void setiOnUpdate(IOnUpdate iOnUpdate) {
        this.iOnUpdate = iOnUpdate;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        if (this.iOnUpdate != null) {
            this.iOnUpdate.onUpdate(target, this.message);
        }
    }

    public interface IOnUpdate extends Serializable {
        void onUpdate(AjaxRequestTarget target, String message);
    }
}
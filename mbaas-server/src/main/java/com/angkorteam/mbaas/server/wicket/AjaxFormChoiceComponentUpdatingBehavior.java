package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Created by socheat on 8/21/16.
 */
public class AjaxFormChoiceComponentUpdatingBehavior extends org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior {

    private IOnUpdate onUpdate;

    public AjaxFormChoiceComponentUpdatingBehavior() {
    }

    public AjaxFormChoiceComponentUpdatingBehavior(IOnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    public IOnUpdate getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(IOnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        if (this.onUpdate != null) {
            this.onUpdate.onUpdate(target);
        }
    }

    public interface IOnUpdate extends Serializable {
        void onUpdate(AjaxRequestTarget target);
    }

}

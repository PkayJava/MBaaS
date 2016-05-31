package com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornAjaxButton extends org.apache.wicket.ajax.markup.html.form.AjaxButton {

    private OnSubmit onSubmit;

    private OnError onError;

    private Map<String, Object> userModel;

    public NashornAjaxButton(String id) {
        super(id);
    }

    public NashornAjaxButton(String id, IModel<String> model) {
        super(id, model);
    }

    public NashornAjaxButton(String id, Form<?> form) {
        super(id, form);
    }

    public NashornAjaxButton(String id, IModel<String> model, Form<?> form) {
        super(id, model, form);
    }

    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }

    @Override
    protected final void onSubmit(AjaxRequestTarget target, Form<?> form) {
        if (this.onSubmit != null) {
            this.onSubmit.doOnSubmit(this, target, form, this.userModel);
        }
    }

    @Override
    protected final void onError(AjaxRequestTarget target, Form<?> form) {
        if (this.onError != null) {
            this.onError.doOnError(this, target, form, this.userModel);
        }
    }

    public void setOnSubmit(OnSubmit onSubmit) {
        this.onSubmit = onSubmit;
    }

    public void setOnError(OnError onError) {
        this.onError = onError;
    }

    public interface OnSubmit extends Serializable {
        void doOnSubmit(NashornAjaxButton ajaxButton, AjaxRequestTarget target, Form<?> form, Map<String, Object> userModel);
    }

    public interface OnError extends Serializable {
        void doOnError(NashornAjaxButton ajaxButton, AjaxRequestTarget target, Form<?> form, Map<String, Object> userModel);
    }
}

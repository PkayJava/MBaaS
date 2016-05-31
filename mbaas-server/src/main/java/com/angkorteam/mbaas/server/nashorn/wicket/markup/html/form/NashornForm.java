package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornForm<T> extends org.apache.wicket.markup.html.form.Form<T> {

    private OnSubmit<T> onSubmit;
    private OnError<T> onError;
    private Map<String, Object> userModel;

    public NashornForm(String id) {
        super(id);
    }

    public NashornForm(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected final void onSubmit() {
        if (this.onSubmit != null) {
            this.onSubmit.doOnSubmit(this, this.userModel);
        }
    }

    @Override
    protected final void onError() {
        if (this.onError != null) {
            this.onError.doOnError(this, this.userModel);
        }
    }

    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }

    public OnSubmit<T> getOnSubmit() {
        return onSubmit;
    }

    public void setOnSubmit(OnSubmit<T> onSubmit) {
        this.onSubmit = onSubmit;
    }

    public OnError<T> getOnError() {
        return onError;
    }

    public void setOnError(OnError<T> onError) {
        this.onError = onError;
    }

    public interface OnSubmit<T> extends Serializable {
        void doOnSubmit(NashornForm<T> form, Map<String, Object> userModel);
    }

    public interface OnError<T> extends Serializable {
        void doOnError(NashornForm<T> form, Map<String, Object> userModel);
    }
}

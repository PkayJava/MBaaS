package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornButton extends org.apache.wicket.markup.html.form.Button {

    private OnSubmit onSubmit;

    private OnError onError;

    private Map<String, Object> userModel;

    public NashornButton(String id) {
        super(id);
    }

    public NashornButton(String id, IModel<String> model) {
        super(id, model);
    }

    public void setOnSubmit(OnSubmit onSubmit) {
        this.onSubmit = onSubmit;
    }

    public void setOnError(OnError onError) {
        this.onError = onError;
    }

    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }

    @Override
    public final void onSubmit() {
        if (this.onSubmit != null) {
            this.onSubmit.doOnSubmit(this, this.userModel);
        }
    }

    @Override
    public final void onError() {
        if (this.onError != null) {
            this.onError.doOnError(this, this.userModel);
        }
    }

    public interface OnSubmit extends Serializable {
        void doOnSubmit(NashornButton button, Map<String, Object> userModel);
    }

    public interface OnError extends Serializable {
        void doOnError(NashornButton button, Map<String, Object> userModel);
    }
}

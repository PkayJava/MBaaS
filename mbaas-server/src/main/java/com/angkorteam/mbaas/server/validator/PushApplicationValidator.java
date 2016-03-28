package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 3/27/16.
 */
public class PushApplicationValidator extends JooqFormValidator {

    private TextField<String> serverUrlField;
    private TextField<String> pushApplicationIdField;
    private TextField<String> masterSecretField;

    private FormComponent<?>[] formComponents;

    public PushApplicationValidator(TextField<String> serverUrlField, TextField<String> pushApplicationIdField, TextField<String> masterSecretField) {
        this.serverUrlField = serverUrlField;
        this.pushApplicationIdField = pushApplicationIdField;
        this.masterSecretField = masterSecretField;
        this.formComponents = new FormComponent[]{this.serverUrlField, this.pushApplicationIdField, this.masterSecretField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        String serverUrl = this.serverUrlField.getConvertedInput();
        String pushApplicationId = this.pushApplicationIdField.getConvertedInput();
        String masterSecret = this.masterSecretField.getConvertedInput();
        if ((serverUrl == null || "".equals(serverUrl))
                && (pushApplicationId == null || "".equals(pushApplicationId))
                && (masterSecret == null || "".equals(masterSecret))) {
        } else {
            if (serverUrl == null || "".equals(serverUrl)) {
                this.serverUrlField.error(new ValidationError("Required"));
            } else {

            }
            if (pushApplicationId == null || "".equals(pushApplicationId)) {
                this.pushApplicationIdField.error(new ValidationError("Required"));
            }
            if (masterSecret == null || "".equals(masterSecret)) {
                this.masterSecretField.error(new ValidationError("Required"));
            }
        }
    }
}

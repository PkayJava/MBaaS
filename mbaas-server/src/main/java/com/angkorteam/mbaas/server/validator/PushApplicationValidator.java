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

    private TextField<String> pushServerUrlField;
    private TextField<String> pushApplicationIdField;
    private TextField<String> pushMasterSecretField;

    private FormComponent<?>[] formComponents;

    public PushApplicationValidator(TextField<String> pushServerUrlField, TextField<String> pushApplicationIdField, TextField<String> pushMasterSecretField) {
        this.pushServerUrlField = pushServerUrlField;
        this.pushApplicationIdField = pushApplicationIdField;
        this.pushMasterSecretField = pushMasterSecretField;
        this.formComponents = new FormComponent[]{this.pushServerUrlField, this.pushApplicationIdField, this.pushMasterSecretField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        String pushServerUrl = this.pushServerUrlField.getConvertedInput();
        String pushApplicationId = this.pushApplicationIdField.getConvertedInput();
        String pushMasterSecret = this.pushMasterSecretField.getConvertedInput();
        if ((pushServerUrl == null || "".equals(pushServerUrl))
                && (pushApplicationId == null || "".equals(pushApplicationId))
                && (pushMasterSecret == null || "".equals(pushMasterSecret))) {
        } else {
            if (pushServerUrl == null || "".equals(pushServerUrl)) {
                this.pushServerUrlField.error(new ValidationError("Required"));
            }
            if (pushApplicationId == null || "".equals(pushApplicationId)) {
                this.pushApplicationIdField.error(new ValidationError("Required"));
            }
            if (pushMasterSecret == null || "".equals(pushMasterSecret)) {
                this.pushMasterSecretField.error(new ValidationError("Required"));
            }
        }
    }
}

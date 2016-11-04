package com.angkorteam.mbaas.server.validator;

import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 3/3/16.
 */
public class QueryScriptValidator implements IValidator<String> {


    public QueryScriptValidator() {
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String script = validatable.getValue();
        if (!Strings.isNullOrEmpty(script)) {
            if (script.length() >= "SELECT".length() && script.substring(0, "SELECT".length()).toLowerCase().equals("select")) {
            } else {
                validatable.error(new ValidationError(this, "format"));
            }
        }
    }
}

package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 8/22/16.
 */
public class RestNameValidator extends JooqFormValidator {

    private String applicationCode;

    private String restId;

    private TextField<String> requestPathField;

    private DropDownChoice<String> methodField;

    private FormComponent<?>[] formComponents;

    public RestNameValidator(String applicationCode, TextField<String> requestPathField, DropDownChoice<String> methodField) {
        this(applicationCode, requestPathField, methodField, null);
    }

    public RestNameValidator(String applicationCode, TextField<String> requestPathField, DropDownChoice<String> methodField, String restId) {
        this.applicationCode = applicationCode;
        this.requestPathField = requestPathField;
        this.methodField = methodField;
        this.restId = restId;
        this.formComponents = new FormComponent[]{methodField, requestPathField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        String requestPath = this.requestPathField.getConvertedInput();
        String method = this.methodField.getConvertedInput();
        if (requestPath != null && !"".equals(requestPath) && method != null && !"".equals(method)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = 0;
            if (this.restId != null && !"".equals(this.restId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.REST + " WHERE " + Jdbc.Rest.REST_ID + " != ? AND " + Jdbc.Rest.PATH + " = ? AND " + Jdbc.Rest.METHOD + " = ?", int.class, this.restId, requestPath, method);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.REST + " WHERE " + Jdbc.Rest.PATH + " = ? AND " + Jdbc.Rest.METHOD + " = ?", int.class, requestPath, method);
            }
            if (count > 0) {
                for (FormComponent<?> formComponent : this.formComponents) {
                    ValidationError error = new ValidationError();
                    error.addKey("Duplicated");
                    formComponent.error(error);
                }
            }
        }
    }
}

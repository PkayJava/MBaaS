package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/3/16.
 */
public class UserLoginFormValidator extends JooqFormValidator {

    private TextField<String> hostnameField;
    private TextField<String> loginField;

    private FormComponent<?>[] components;

    public UserLoginFormValidator(TextField<String> hostnameField, TextField<String> loginField) {
        this.hostnameField = hostnameField;
        this.loginField = loginField;
        this.components = new FormComponent<?>[]{hostnameField, loginField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.components;
    }

    @Override
    public void validate(Form<?> form) {
        String hostname = this.hostnameField.getConvertedInput();
        String login = this.loginField.getConvertedInput();
        if (hostname != null && !"".equals(hostname) && login != null && !"".equals(login)) {
            DSLContext context = getDSLContext();
            HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
            HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);
            if (hostnameRecord != null) {
                ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
                ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);
                String applicationCode = applicationRecord.getCode();
                Application application = ApplicationUtils.getApplication();
                JdbcTemplate jdbcTemplate = application.getJdbcTemplate(applicationCode);
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.USER + " WHERE " + Jdbc.User.LOGIN + " = ?", int.class, login);
                if (count > 0) {
                    ValidationError error = new ValidationError();
                    error.addKey(Classes.simpleName(UserLoginFormValidator.class) + ".duplicated");
                    loginField.error(error);
                }
            }
        }
    }
}

package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/26/16.
 */
public class ClientNameValidator extends JooqValidator<String> {

    private String applicationCode;

    private String clientId;

    public ClientNameValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public ClientNameValidator(String applicationCode, String clientId) {
        this.applicationCode = applicationCode;
        this.clientId = clientId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = 0;
            if (this.clientId == null || "".equals(this.clientId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.CLIENT + " WHERE " + Jdbc.Client.NAME + " = ?", int.class, name);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.CLIENT + " WHERE " + Jdbc.Client.NAME + " = ? AND " + Jdbc.Client.CLIENT_ID + " != ?", int.class, name, this.clientId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

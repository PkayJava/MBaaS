package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 8/6/16.
 */
public class HttpHeaderNameValidator implements IValidator<String> {

    private String httpHeaderId;
    private String applicationCode;

    public HttpHeaderNameValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public HttpHeaderNameValidator(String applicationCode, String httpHeaderId) {
        this.applicationCode = applicationCode;
        this.httpHeaderId = httpHeaderId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

            int count = 0;
            if (this.httpHeaderId == null || "".equals(this.httpHeaderId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.NAME + " = ?", int.class, name);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.NAME + " = ? AND " + Jdbc.HttpHeader.HTTP_HEADER_ID + " != ?", int.class, name, this.httpHeaderId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

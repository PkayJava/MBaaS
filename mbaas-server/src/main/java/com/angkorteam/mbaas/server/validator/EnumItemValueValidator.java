package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.ParseException;

/**
 * Created by socheat on 8/6/16.
 */
public class EnumItemValueValidator implements IValidator<String> {

    private TypeEnum type;
    private String enumItemId;
    private String applicationCode;

    public EnumItemValueValidator(String applicationCode, TypeEnum type) {
        this.type = type;
        this.applicationCode = applicationCode;
    }

    public EnumItemValueValidator(String applicationCode, TypeEnum type, String enumItemId) {
        this.type = type;
        this.applicationCode = applicationCode;
        this.enumItemId = enumItemId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String value = validatable.getValue();
        if (value != null && !"".equals(value)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

            int count = 0;
            if (this.enumItemId == null || "".equals(this.enumItemId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.VALUE + " = ?", int.class, value);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.VALUE + " = ? AND " + Jdbc.EnumItem.ENUM_ITEM_ID + " != ?", int.class, value, this.enumItemId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
                return;
            }

            if (type == TypeEnum.Boolean) {
                if (!"yes".equalsIgnoreCase(value) && !"no".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value) && !"0".equals(value) && !"1".equals(value)) {
                    ValidationError error = new ValidationError(this, "boolean");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.Character) {
                if (value.length() > 1) {
                    ValidationError error = new ValidationError(this, "character");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.String) {
            } else if (type == TypeEnum.Long) {
                try {
                    Long.valueOf(value);
                } catch (NumberFormatException e) {
                    ValidationError error = new ValidationError(this, "long");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.Double) {
                try {
                    Double.valueOf(value);
                } catch (NumberFormatException e) {
                    ValidationError error = new ValidationError(this, "long");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.Time) {
                try {
                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                } catch (ParseException e) {
                    ValidationError error = new ValidationError(this, "time");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.Date) {
                try {
                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                } catch (ParseException e) {
                    ValidationError error = new ValidationError(this, "date");
                    validatable.error(error);
                }
            } else if (type == TypeEnum.DateTime) {
                try {
                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                } catch (ParseException e) {
                    ValidationError error = new ValidationError(this, "datetime");
                    validatable.error(error);
                }
            }
        }
    }
}

package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.select2.Item;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 12/6/16.
 */
public class UniqueRecordValidator<T> implements IValidator<T> {

    private final String tableName;

    private final String fieldName;

    private String idFieldName;

    private Object idFieldValue;

    public UniqueRecordValidator(String tableName, String fieldName) {
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    public UniqueRecordValidator(String tableName, String fieldName, String idFieldName, T idFieldValue) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.idFieldName = idFieldName;
        if (idFieldValue instanceof Item) {
            this.idFieldValue = ((Item) idFieldValue).getId();
        } else {
            this.idFieldValue = idFieldValue;
        }
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        T value = validatable.getValue();
        Object newValue = null;
        if (value != null) {
            if (value instanceof Item) {
                newValue = ((Item) value).getId();
            } else {
                newValue = value;
            }
        }
        if (newValue != null) {
            try {
                JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
                int count = 0;
                if (this.idFieldValue == null) {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + this.tableName + " WHERE " + this.fieldName + " = ?", int.class, newValue);
                } else {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + this.tableName + " WHERE " + this.fieldName + " = ? AND " + this.idFieldName + " != ?", int.class, newValue, this.idFieldValue);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            } catch (BadSqlGrammarException e) {
                validatable.error(new ValidationError(this, "error").setVariable("message", e.getMessage()));
            }
        }
    }

}

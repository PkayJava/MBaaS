package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.select2.Item;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

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
            Sql2o sql2o = Spring.getBean(Sql2o.class);
            try (Connection connection = sql2o.open()) {
                int count = 0;
                if (this.idFieldValue == null) {
                    Query query = connection.createQuery("SELECT COUNT(*) FROM " + this.tableName + " WHERE " + this.fieldName + " = :newValue");
                    query.addParameter("newValue", newValue);
                    count = query.executeAndFetchFirst(int.class);
                } else {
                    Query query = connection.createQuery("SELECT COUNT(*) FROM " + this.tableName + " WHERE " + this.fieldName + " = :newValue AND " + this.idFieldName + " != :id");
                    query.addParameter("newValue", newValue);
                    query.addParameter("id", this.idFieldValue);
                    count = query.executeAndFetchFirst(int.class);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            }
        }
    }

}

package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.regex.Pattern;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionNameValidator extends JooqValidator<String> {

    private final String applicationCode;

    private String collectionId;

    public CollectionNameValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public CollectionNameValidator(String applicationCode, String collectionId) {
        this.collectionId = collectionId;
        this.applicationCode = applicationCode;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Pattern patternCollectionName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_COLLECTION_NAME));
            if (!patternCollectionName.matcher(name).matches()) {
                validatable.error(new ValidationError(this, "format"));
            } else {
                Application application = ApplicationUtils.getApplication();
                Schema schema = application.getSchema(this.applicationCode);
                if (schema.getTable(name).exists()) {
                    validatable.error(new ValidationError(this, "duplicated"));
                    return;
                }
                JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
                int count = 0;
                if (collectionId == null || "".equals(collectionId)) {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.NAME + " = ?", int.class, name);
                } else {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.NAME + " = ? AND " + Jdbc.Collection.COLLECTION_ID + " != ?", int.class, name, this.collectionId);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            }
        }
    }
}

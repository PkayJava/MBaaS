package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.regex.Pattern;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeNameValidator extends JooqValidator<String> {

    private final String applicationCode;

    private String collectionId;

    private String attributeId;

    public AttributeNameValidator(String applicationCode, String collectionId) {
        this.collectionId = collectionId;
        this.applicationCode = applicationCode;
    }

    public AttributeNameValidator(String applicationCode, String collectionId, String attributeId) {
        this.collectionId = collectionId;
        this.attributeId = attributeId;
        this.applicationCode = applicationCode;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Pattern patternAttributeName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_ATTRIBUTE_NAME));
            if (!patternAttributeName.matcher(name).matches()) {
                validatable.error(new ValidationError(this, "format"));
            } else {
                Application application = ApplicationUtils.getApplication();
                JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
                int count = 0;
                if (attributeId == null || "".equals(attributeId)) {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.NAME + " = ? AND " + Jdbc.Attribute.COLLECTION_ID + " = ?", int.class, name, this.collectionId);
                } else {
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.NAME + " = ? AND " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.ATTRIBUTE_ID + " != ?", int.class, name, this.collectionId, this.attributeId);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            }
        }
    }
}

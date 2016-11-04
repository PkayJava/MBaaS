package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeNameValidator implements IValidator<String> {

    private final String collectionId;

    private String attributeId;

    public AttributeNameValidator(String collectionId) {
        this.collectionId = collectionId;
    }

    public AttributeNameValidator(String collectionId, String attributeId) {
        this.collectionId = collectionId;
        this.attributeId = attributeId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (!Strings.isNullOrEmpty(name)) {
            if (!Application.CHARACTERS.contains(name.charAt(0))) {
                validatable.error(new ValidationError(this, "format"));
                return;
            }
            if (name.length() > 1) {
                for (int i = 1; i < name.length(); i++) {
                    char ch = name.charAt(i);
                    if (ch != '_' && !Application.CHARACTERS.contains(ch) && !Application.NUMBERS.contains(ch)) {
                        validatable.error(new ValidationError(this, "format"));
                        return;
                    }
                }
            }
            DSLContext context = Spring.getBean(DSLContext.class);
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            int count = 0;
            if (Strings.isNullOrEmpty(this.attributeId)) {
                count = context.selectCount().from(attributeTable).where(attributeTable.NAME.eq(name)).and(attributeTable.COLLECTION_ID.eq(this.collectionId)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(attributeTable).where(attributeTable.NAME.eq(name)).and(attributeTable.COLLECTION_ID.eq(this.collectionId)).and(attributeTable.ATTRIBUTE_ID.notEqual(this.attributeId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

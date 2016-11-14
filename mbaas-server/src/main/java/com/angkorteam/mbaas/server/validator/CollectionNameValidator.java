package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionNameValidator implements IValidator<String> {

    private String documentId;

    public CollectionNameValidator() {
    }

    public CollectionNameValidator(String documentId) {
        this.documentId = documentId;
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
            int count = 0;
            DSLContext context = Spring.getBean(DSLContext.class);
            CollectionTable table = Tables.COLLECTION.as("table");
            if (Strings.isNullOrEmpty(this.documentId)) {
                count = context.selectCount().from(table).where(table.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(table).where(table.NAME.eq(name)).and(table.COLLECTION_ID.notEqual(this.documentId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

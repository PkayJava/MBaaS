package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionNameValidator extends JooqValidator<String> {

    private String collectionId;

    public CollectionNameValidator() {
    }

    public CollectionNameValidator(String collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (collectionId == null || "".equals(collectionId)) {
                count = context.selectCount().from(collectionTable).where(collectionTable.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(collectionTable).where(collectionTable.NAME.eq(name)).and(collectionTable.COLLECTION_ID.ne(this.collectionId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

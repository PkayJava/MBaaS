package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 11/13/16.
 */
public class LayoutTitleValidator implements IValidator<String> {

    private String documentId;

    public LayoutTitleValidator() {
    }

    public LayoutTitleValidator(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String title = validatable.getValue();
        if (!Strings.isNullOrEmpty(title)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            LayoutTable table = Tables.LAYOUT.as("table");
            int count = 0;
            if (Strings.isNullOrEmpty(this.documentId)) {
                count = context.selectCount().from(table).where(table.TITLE.eq(title)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(table).where(table.TITLE.eq(title)).and(table.LAYOUT_ID.notEqual(this.documentId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }

}

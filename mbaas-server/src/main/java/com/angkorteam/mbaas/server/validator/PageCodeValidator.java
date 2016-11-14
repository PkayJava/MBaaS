package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 11/13/16.
 */
public class PageCodeValidator implements IValidator<String> {

    private String documentId;

    public PageCodeValidator() {
    }

    public PageCodeValidator(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String title = validatable.getValue();
        if (!Strings.isNullOrEmpty(title)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            PageTable table = Tables.PAGE.as("table");
            int count = 0;
            if (Strings.isNullOrEmpty(this.documentId)) {
                count = context.selectCount().from(table).where(table.CODE.eq(title)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(table).where(table.CODE.eq(title)).and(table.PAGE_ID.notEqual(this.documentId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }

}

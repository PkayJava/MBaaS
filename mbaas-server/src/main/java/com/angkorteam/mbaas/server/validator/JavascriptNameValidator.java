package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class JavascriptNameValidator extends JooqValidator<String> {

    private String javascriptId;

    public JavascriptNameValidator() {
    }

    public JavascriptNameValidator(String javascriptId) {
        this.javascriptId = javascriptId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (javascriptId == null || "".equals(javascriptId)) {
                count = context.selectCount().from(javascriptTable).where(javascriptTable.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(javascriptTable).where(javascriptTable.NAME.eq(name)).and(javascriptTable.JAVASCRIPT_ID.ne(this.javascriptId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

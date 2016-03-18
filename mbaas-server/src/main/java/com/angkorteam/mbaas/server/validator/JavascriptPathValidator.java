package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

import java.util.regex.Pattern;

/**
 * Created by socheat on 3/3/16.
 */
public class JavascriptPathValidator extends JooqValidator<String> {

    private String javascriptId;

    public JavascriptPathValidator() {
    }

    public JavascriptPathValidator(String javascriptId) {
        this.javascriptId = javascriptId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String path = validatable.getValue();
        if (path != null && !"".equals(path)) {
            Pattern patternNaming = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_PATH));
            if (!patternNaming.matcher(path).matches()) {
                validatable.error(new ValidationError(this, "format"));
            } else {
                JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
                DSLContext context = getDSLContext();
                int count = 0;
                if (javascriptId == null || "".equals(javascriptId)) {
                    count = context.selectCount().from(javascriptTable).where(javascriptTable.PATH.eq(path)).fetchOneInto(int.class);
                } else {
                    count = context.selectCount().from(javascriptTable).where(javascriptTable.PATH.eq(path)).and(javascriptTable.JAVASCRIPT_ID.ne(this.javascriptId)).fetchOneInto(int.class);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            }
        }
    }
}

package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

import java.util.regex.Pattern;

/**
 * Created by socheat on 3/3/16.
 */
public class QueryPathValidator extends JooqValidator<String> {

    private String query;

    public QueryPathValidator() {
    }

    public QueryPathValidator(String query) {
        this.query = query;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String path = validatable.getValue();
        if (path != null && !"".equals(path)) {
            Pattern patternNaming = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_PATH));
            if (!patternNaming.matcher(path).matches()) {
                validatable.error(new ValidationError(this, "format"));
            } else {
                DSLContext context = getDSLContext();
                int count = 0;
                if (query == null || "".equals(query)) {
                    count = context.selectCount().from(Tables.QUERY).where(Tables.QUERY.PATH.eq(path)).fetchOneInto(int.class);
                } else {
                    count = context.selectCount().from(Tables.QUERY).where(Tables.QUERY.PATH.eq(path)).and(Tables.QUERY.QUERY_ID.ne(this.query)).fetchOneInto(int.class);
                }
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            }
        }
    }
}

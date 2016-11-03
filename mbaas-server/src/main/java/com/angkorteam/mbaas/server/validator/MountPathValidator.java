package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 11/3/16.
 */
public class MountPathValidator implements IValidator<String> {

    private String pageId;

    public MountPathValidator() {
    }

    public MountPathValidator(String pageId) {
        this.pageId = pageId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String path = validatable.getValue();
        if (path != null && !"".equals(path)) {
            if (path.startsWith("/api")) {
                validatable.error(new ValidationError(this, "format"));
                return;
            }

            if (path.charAt(0) != '/') {
                validatable.error(new ValidationError(this, "format"));
                return;
            }

            if (path.length() > 1) {
                for (int i = 1; i < path.length(); i++) {
                    char ch = path.charAt(i);
                    if (ch != '/' && !Application.CHARACTERS.contains(ch) && !Application.NUMBERS.contains(ch)) {
                        validatable.error(new ValidationError(this, "format"));
                        return;
                    }
                }
            }
            if (path.contains("//")) {
                validatable.error(new ValidationError(this, "format"));
                return;
            }
            DSLContext context = Spring.getBean(DSLContext.class);
            PageTable pageTable = Tables.PAGE.as("pageTable");
            int count = 0;
            if (this.pageId == null || "".equals(this.pageId)) {
                count = context.selectCount().from(pageTable).where(pageTable.PATH.eq(path)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(pageTable).where(pageTable.PATH.eq(path)).and(pageTable.PAGE_ID.notEqual(this.pageId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}

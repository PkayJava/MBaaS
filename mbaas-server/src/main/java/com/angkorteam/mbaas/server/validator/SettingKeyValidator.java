package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SettingTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/10/16.
 */
public class SettingKeyValidator extends JooqValidator<String> {

    private String key;

    public SettingKeyValidator() {
    }

    public SettingKeyValidator(String key) {
        this.key = key;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String newKey = validatable.getValue();
        SettingTable settingTable = Tables.SETTING.as("settingTable");
        DSLContext context = getDSLContext();
        if (newKey != null && !"".equals(newKey)) {
            if (key == null || "".equals(key)) {
                int count = context.selectCount().from(settingTable).where(settingTable.SETTING_ID.eq(newKey)).fetchOneInto(int.class);
                if (count > 0) {
                    validatable.error(new ValidationError(this, "duplicated"));
                }
            } else {
                if (!key.equals(newKey)) {
                    int count = context.selectCount().from(settingTable).where(settingTable.SETTING_ID.eq(newKey)).fetchOneInto(int.class);
                    if (count > 0) {
                        validatable.error(new ValidationError(this, "duplicated"));
                    }
                }
            }
        }
    }
}

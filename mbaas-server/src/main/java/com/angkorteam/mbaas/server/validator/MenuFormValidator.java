package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.server.Spring;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 10/28/16.
 */
public class MenuFormValidator extends JooqFormValidator {

    private DropDownChoice<MenuPojo> menuField;

    private DropDownChoice<SectionPojo> sectionField;

    private FormComponent<?>[] formComponents;

    public MenuFormValidator(DropDownChoice<MenuPojo> menuField, DropDownChoice<SectionPojo> sectionField) {
        this.menuField = menuField;
        this.sectionField = sectionField;
        this.formComponents = new FormComponent<?>[]{this.menuField, this.sectionField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        if (this.menuField.getConvertedInput() != null && this.sectionField.getConvertedInput() != null) {
            {
                ValidationError error = new ValidationError();
                error.addKey("ambiguous");
                this.menuField.error(error);
            }
            {
                ValidationError error = new ValidationError();
                error.addKey("ambiguous");
                this.sectionField.error(error);
            }
        } else if (this.menuField.getConvertedInput() == null && this.sectionField.getConvertedInput() == null) {
            {
                ValidationError error = new ValidationError();
                error.addKey("Required");
                this.menuField.error(error);
            }
            {
                ValidationError error = new ValidationError();
                error.addKey("Required");
                this.sectionField.error(error);
            }
        }
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }
}

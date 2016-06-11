
package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */

public class TestPage extends AdminLTEPage {

    private Map<String, Object> userModel;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.userModel = new HashMap<>();
        Form<Void> form = new Form<>("form");
        add(form);

        List<String> choices = Arrays.asList("A", "B", "C");
        DropDownChoice<String> dropDownChoice = new DropDownChoice<>("dropDownChoice", new PropertyModel<>(this.userModel, "dropDownChoice"), choices);
        dropDownChoice.setRequired(true);
        form.add(dropDownChoice);

        ListMultipleChoice<String> listMultipleChoice = new ListMultipleChoice<>("listMultipleChoice", new PropertyModel<>(this.userModel, "listMultipleChoice"), Arrays.asList("A", "B", "C"));
        form.add(listMultipleChoice);

        CheckBoxMultipleChoice<String> checkBoxMultipleChoice = new CheckBoxMultipleChoice<>("checkBoxMultipleChoice", new PropertyModel<>(this.userModel, "checkBoxMultipleChoice"), Arrays.asList("A", "B", "C"));
        form.add(checkBoxMultipleChoice);

        ListChoice<String> listChoice = new ListChoice<>("listChoice", new PropertyModel<>(this.userModel, "listChoice"), Arrays.asList("A", "B", "C"));
        form.add(listChoice);

        RadioChoice<String> radioChoice = new RadioChoice<>("radioChoice", new PropertyModel<>(this.userModel, "radioChoice"), Arrays.asList("A", "B", "C"));
        form.add(radioChoice);

        CheckBox checkBox = new CheckBox("checkBox", new PropertyModel<>(this.userModel, "checkBox"));
        form.add(checkBox);

        Button okayButton = new Button("okayButton");
        form.add(okayButton);
    }
}

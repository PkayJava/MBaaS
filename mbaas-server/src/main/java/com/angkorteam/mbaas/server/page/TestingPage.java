package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.mbaas.server.renderer.TableRenderer;
import com.angkorteam.mbaas.server.select2.TableProvider;
import com.angkorteam.mbaas.server.select2.TableProvider1;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Created by socheat on 5/24/16.
 */
public class TestingPage extends AdminLTEPage {

    private String oppp;

    private List<String> ppp;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = new Form<>("form");
        add(form);
        Select2SingleChoice<String> test = new Select2SingleChoice<>("test", new PropertyModel<>(this, "oppp"), new TableProvider(), new TableRenderer());
        form.add(test);

        Select2MultipleChoice<String> pp = new Select2MultipleChoice<>("pp", new PropertyModel<>(this, "ppp"), new TableProvider1(), new TableRenderer());
        form.add(pp);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }
}

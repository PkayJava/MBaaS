package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by socheat on 3/27/16.
 */
@Mount("/bearer/token")
public class BearerTokenPage extends AdminLTEPage {

    private String token;
    private Label tokenLabel;

    private Button okayButton;

    private Form<Void> form;

    public BearerTokenPage(String token) {
        this.token = token;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.tokenLabel = new Label("tokenLabel", new PropertyModel<>(this, "token"));
        add(this.tokenLabel);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }
}

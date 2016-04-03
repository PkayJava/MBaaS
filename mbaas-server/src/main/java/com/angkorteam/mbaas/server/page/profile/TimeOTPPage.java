package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.image.ExternalImage;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by socheat on 4/3/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice", "registered"})
@Mount("/profile/otp")
public class TimeOTPPage extends MasterPage {

    private Form<Void> form;

    private ExternalImage secretImage;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();


        this.form = new Form<>("form");
        add(this.form);

        String secret = UUID.randomUUID().toString();
        String api = HttpFunction.getHttpAddress(request) + "/api/qr?secret=" + secret;

        this.secretImage = new ExternalImage("secretImage", api);
        this.form.add(secretImage);
    }

}

package com.angkorteam.mbaas.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by socheat on 3/29/16.
 */
@Controller
@RequestMapping(path = "/oauth")
public class OAuthController {

    @RequestMapping(method = RequestMethod.GET, path = "/authorize")
    public void authorize(
            @RequestParam("response_type") String responseType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state) {

        // http://brentertainment.com/oauth2/client/receive_authcode?code=d7d1c1b0eda2c5804b50d13aad4bd54ba360de12&state=1363b9dc58f6bac2a8b2389d29076d6f
    }

}

package com.angkorteam.mbaas.server.controller;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by socheat on 4/3/16.
 */
@Controller
@RequestMapping("/qr")
public class QRController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRController.class);

    @RequestMapping(
            method = RequestMethod.GET
    )
    public void doGet(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "secret", required = true) String secret,
            @RequestParam(value = "size", required = false) Integer size) throws IOException {
        LOGGER.info("{} secret=>{}", request.getRequestURL(), secret);

        int s = 200;
        if (size != null) {
            s = size;
        }

        ByteArrayOutputStream bytes = QRCode.from(secret).withSize(s, s).to(ImageType.JPG).stream();
        response.getOutputStream().write(bytes.toByteArray());
    }

}

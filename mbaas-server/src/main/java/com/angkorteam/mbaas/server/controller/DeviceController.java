package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.plain.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.plain.response.document.DocumentCreateResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 3/28/16.
 */

@Controller("/rest/registry")
public class DeviceController {

    @RequestMapping(
            method = RequestMethod.POST, path = "/device",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentCreateResponse> register(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody DeviceRegisterRequest requestBody
    ) {
        return null;
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/device/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentCreateResponse> unregister(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @PathVariable("id") String id
    ) {
        return null;
    }
}

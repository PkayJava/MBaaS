package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.ResultEnum;
import com.angkorteam.mbaas.plain.enums.UserTotpStatusEnum;
import com.angkorteam.mbaas.plain.request.otp.OtpRequest;
import com.angkorteam.mbaas.plain.response.otp.OtpResponse;
import com.angkorteam.mbaas.plain.security.otp.Totp;
import com.google.gson.Gson;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by socheat on 4/3/16.
 */
@Controller
@RequestMapping("/otp")
public class OtpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRController.class);

    @Autowired
    private Gson gson;

    @Autowired
    private DSLContext context;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OtpResponse> doPost(
            HttpServletRequest request,
            @RequestBody OtpRequest requestBody) throws IOException {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));

        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");

        UserRecord userRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is required");
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.TOTP_SECRET.eq(requestBody.getSecret())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("secret", "is bad");
            } else {
                if (UserTotpStatusEnum.Granted.getLiteral().equals(userRecord.getTotpStatus())) {
                    errorMessages.put("secret", "is bad");
                }
            }
        }

        if (requestBody.getOtp() == null || "".equals(requestBody.getOtp())) {
            errorMessages.put("otp", "is required");
        }

        if (!errorMessages.isEmpty()) {
            OtpResponse response = new OtpResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        Totp totp = new Totp(userRecord.getTotpHash());
        if (totp.verify(requestBody.getOtp())) {
            userRecord.setTotpStatus(UserTotpStatusEnum.Granted.getLiteral());
            userRecord.setAuthentication(AuthenticationEnum.TOTP.getLiteral());
            userRecord.update();
            OtpResponse response = new OtpResponse();
            response.setData(ResultEnum.OK.getLiteral());
            return ResponseEntity.ok(response);
        } else {
            OtpResponse response = new OtpResponse();
            response.setData(ResultEnum.ERROR.getLiteral());
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }
    }

}
package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.plain.request.device.DevicePushMessageRequest;
import com.angkorteam.mbaas.plain.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.plain.response.device.DevicePushMessageResponse;
import com.angkorteam.mbaas.plain.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.plain.response.device.DeviceUnregisterResponse;
import com.google.gson.Gson;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/28/16.
 */

@Controller("/rest/registry")
public class DeviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/device",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeviceRegisterResponse> register(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody DeviceRegisterRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ClientRecord clientRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is required");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_SECRET.eq(requestBody.getSecret())).fetchOneInto(clientTable);
        }

        ApplicationRecord applicationRecord = null;
        if (clientRecord == null) {
            errorMessages.put("secret", "is invalid");
        } else {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("secret", "is invalid");
        }

        if (requestBody.getDeviceToken() == null || "".equals(requestBody.getDeviceToken())) {
            errorMessages.put("deviceToken", "is required");
        }

        if (requestBody.getDeviceType() == null || "".equals(requestBody.getDeviceType())) {
            errorMessages.put("deviceType", "is required");
        }

        if (!errorMessages.isEmpty()) {
            DeviceRegisterResponse response = new DeviceRegisterResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(requestBody.getDeviceToken())).and(mobileTable.DEVICE_TYPE.eq(requestBody.getDeviceType())).fetchOneInto(mobileTable);
        if (mobileRecord == null) {
            mobileRecord = context.newRecord(mobileTable);
            mobileRecord.setMobileId(UUID.randomUUID().toString());
            mobileRecord.setApplicationId(applicationRecord.getApplicationId());
            mobileRecord.setClientId(clientRecord.getClientId());
            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            mobileRecord.setClientIp(request.getRemoteAddr());
            mobileRecord.setDateCreated(new Date());
            mobileRecord.setDeviceType(requestBody.getDeviceType());
            mobileRecord.setDeviceToken(requestBody.getDeviceToken());
            mobileRecord.setDeviceAlias(requestBody.getAlias());
            mobileRecord.setDeviceOsVersion(requestBody.getOsVersion());
            mobileRecord.setDeviceOperatingSystem(requestBody.getOperatingSystem());
            mobileRecord.store();
        } else {
            mobileRecord.setApplicationId(applicationRecord.getApplicationId());
            mobileRecord.setClientId(clientRecord.getClientId());
            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            mobileRecord.setClientIp(request.getRemoteAddr());
            mobileRecord.setDeviceAlias(requestBody.getAlias());
            mobileRecord.setDeviceOsVersion(requestBody.getOsVersion());
            mobileRecord.setDeviceOperatingSystem(requestBody.getOperatingSystem());
            mobileRecord.update();
        }

        DeviceRegisterResponse response = new DeviceRegisterResponse();
        response.getData().setBearer(mobileRecord.getMobileId());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/device/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeviceUnregisterResponse> unregister(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @PathVariable("id") String id
    ) {
        DeviceUnregisterResponse response = new DeviceUnregisterResponse();
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(id)).fetchOneInto(mobileTable);
        mobileRecord.delete();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/device/pushMessage/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DevicePushMessageResponse> pushMessage(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @PathVariable("id") String id,
            @RequestBody DevicePushMessageRequest requestBody
    ) {
        DevicePushMessageResponse response = new DevicePushMessageResponse();

        return ResponseEntity.ok(response);
    }


}

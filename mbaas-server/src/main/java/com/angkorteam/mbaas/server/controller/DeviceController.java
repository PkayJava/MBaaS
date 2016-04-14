package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.GrantTypeEnum;
import com.angkorteam.mbaas.plain.request.device.DeviceRegisterRequest;
import com.angkorteam.mbaas.plain.response.device.DeviceMetricsResponse;
import com.angkorteam.mbaas.plain.response.device.DeviceRegisterResponse;
import com.angkorteam.mbaas.plain.response.device.DeviceUnregisterResponse;
import com.angkorteam.mbaas.server.service.*;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/28/16.
 */

@Controller
@RequestMapping("/rest/registry")
public class DeviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private PusherClient pusherClient;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/device",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeviceRegisterResponse> register(
            HttpServletRequest request,
            Identity identity,
            @RequestBody DeviceRegisterRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ClientRecord clientRecord = null;
        if (requestBody.getClientId() == null || "".equals(requestBody.getClientId())) {
            errorMessages.put("clientId", "is required");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(requestBody.getClientId())).fetchOneInto(clientTable);
        }

        if (requestBody.getClientSecret() == null || "".equals(requestBody.getClientSecret())) {
            errorMessages.put("clientSecret", "is required");
        } else {
            if (clientRecord != null && !clientRecord.getClientSecret().equals(requestBody.getClientSecret())) {
                errorMessages.put("clientSecret", "is expired");
            }
        }

        ApplicationRecord applicationRecord = null;
        if (clientRecord == null) {
            errorMessages.put("clientSecret", "is invalid");
        } else {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("clientSecret", "is invalid");
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
        MobileRecord mobileRecord = null;

        mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(identity.getMobileId())).fetchOneInto(mobileTable);
        if (mobileRecord != null) {
            if (identity.getAccessToken() != null && !"".equals(identity.getAccessToken())) {
                mobileRecord.setAccessToken(identity.getAccessToken());
            } else {
                mobileRecord.setAccessToken(UUID.randomUUID().toString());
            }
            mobileRecord.setDateTokenIssued(new Date());
            mobileRecord.setApplicationId(applicationRecord.getApplicationId());
            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            mobileRecord.setClientIp(request.getRemoteAddr());
            mobileRecord.setClientId(clientRecord.getClientId());

            mobileRecord.setDeviceType(requestBody.getDeviceType());
            mobileRecord.setDeviceToken(requestBody.getDeviceToken());
            mobileRecord.setDeviceAlias(requestBody.getAlias());
            mobileRecord.setDeviceOsVersion(requestBody.getOsVersion());
            mobileRecord.setDeviceOperatingSystem(requestBody.getOperatingSystem());

            mobileRecord.update();
        } else {
            mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(requestBody.getDeviceToken())).and(mobileTable.DEVICE_TYPE.eq(requestBody.getDeviceType())).fetchOneInto(mobileTable);
            if (mobileRecord == null) {
                XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                mobileRecord = context.newRecord(mobileTable);
                mobileRecord.setMobileId(UUID.randomUUID().toString());
                mobileRecord.setGrantType(GrantTypeEnum.Client.getLiteral());
                mobileRecord.setApplicationId(applicationRecord.getApplicationId());
                mobileRecord.setAccessToken(UUID.randomUUID().toString());
                mobileRecord.setTimeToLive(configuration.getInt(Constants.ACCESS_TOKEN_TIME_TO_LIVE));
                mobileRecord.setDateTokenIssued(new Date());
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
                if (identity.getAccessToken() != null && !"".equals(identity.getAccessToken())) {
                    mobileRecord.setAccessToken(identity.getAccessToken());
                } else {
                    mobileRecord.setAccessToken(UUID.randomUUID().toString());
                }
                mobileRecord.setDateTokenIssued(new Date());
                mobileRecord.setApplicationId(applicationRecord.getApplicationId());
                mobileRecord.setClientId(clientRecord.getClientId());
                mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                mobileRecord.setClientIp(request.getRemoteAddr());

                mobileRecord.setDeviceAlias(requestBody.getAlias());
                mobileRecord.setDeviceOsVersion(requestBody.getOsVersion());
                mobileRecord.setDeviceOperatingSystem(requestBody.getOperatingSystem());
                mobileRecord.update();
            }
        }

        String credential = "Basic " + Base64.encodeBase64String((clientRecord.getPushVariantId() + ":" + clientRecord.getPushSecret()).getBytes());

        PusherDTORequest dto = new PusherDTORequest();
        dto.setDeviceToken(requestBody.getDeviceToken());
        dto.setDeviceType(requestBody.getDeviceType());
        dto.setAlias(requestBody.getAlias());
        dto.setOperatingSystem(requestBody.getOperatingSystem());
        dto.setOsVersion(requestBody.getOsVersion());
        dto.setCategories(requestBody.getCategories());

        Call<PusherDTOResponse> responseCall = pusherClient.register(credential, dto);
        Response<PusherDTOResponse> responseBody = null;
        try {
            responseBody = responseCall.execute();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        DeviceRegisterResponse response = new DeviceRegisterResponse();
        response.getData().setAccessToken(mobileRecord.getAccessToken());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/device/{accessToken}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeviceUnregisterResponse> unregister(
            HttpServletRequest request,
            @PathVariable("accessToken") String accessToken
    ) {
        LOGGER.info("{}", request.getRequestURL());
        Map<String, String> errorMessages = new LinkedHashMap<>();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = null;
        if (accessToken == null || "".equals(accessToken)) {
            errorMessages.put("accessToken", "is required.");
        } else {
            mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
            if (mobileRecord == null) {
                errorMessages.put("accessToken", "is expired.");
            }
        }

        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ClientRecord clientRecord = null;
        if (mobileRecord != null) {
            errorMessages.put("accessToken", "is expired.");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(mobileRecord.getClientId())).fetchOneInto(clientTable);
        }

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = null;
        if (clientRecord == null) {
            errorMessages.put("accessToken", "is expired.");
        } else {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("accessToken", "is expired.");
        }

        if (!errorMessages.isEmpty()) {
            DeviceUnregisterResponse response = new DeviceUnregisterResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        mobileRecord.delete();

        String credential = "Basic " + Base64.encodeBase64String((clientRecord.getPushVariantId() + ":" + clientRecord.getPushSecret()).getBytes());
        Call<RevokerDTOResponse> responseCall = pusherClient.unregister(credential, mobileRecord.getDeviceToken());
        Response<RevokerDTOResponse> responseBody = null;
        try {
            responseBody = responseCall.execute();
        } catch (Throwable e) {
        }

        DeviceUnregisterResponse response = new DeviceUnregisterResponse();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/device/pushMessage/{messageId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeviceMetricsResponse> sendMetrics(
            HttpServletRequest request,
            Identity identity,
            @PathVariable("messageId") String messageId
    ) {
        LOGGER.info("{}", request.getRequestURL());
        Map<String, String> errorMessages = new LinkedHashMap<>();

        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(identity.getClientId())).fetchOneInto(clientTable);

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = null;
        if (clientRecord == null) {
            errorMessages.put("clientId", "is invalid.");
        } else {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("clientId", "is invalid.");
        }

        if (!errorMessages.isEmpty()) {
            DeviceMetricsResponse response = new DeviceMetricsResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        String credential = "Basic " + Base64.encodeBase64String((clientRecord.getPushVariantId() + ":" + clientRecord.getPushSecret()).getBytes());
        Call<MetricsDTOResponse> responseCall = this.pusherClient.sendMetrics(credential, messageId);
        Response<MetricsDTOResponse> responseBody = null;
        try {
            responseBody = responseCall.execute();
        } catch (Throwable e) {
        }

        DeviceMetricsResponse response = new DeviceMetricsResponse();
        return ResponseEntity.ok(response);
    }


}

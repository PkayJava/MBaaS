package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutSessionResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import com.angkorteam.mbaas.server.MBaaS;
import com.angkorteam.mbaas.server.function.UserFunction;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping(path = "/security")
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    //region /security/signup

    @RequestMapping(
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecuritySignUpResponse> signup(
            HttpServletRequest request,
            Identity identity,
            @RequestBody SecuritySignUpRequest requestBody
    ) {
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        UserTable userTable = Tables.USER.as("userTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ClientRecord clientRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is request");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_SECRET.eq(requestBody.getSecret())).fetchOneInto(clientTable);
        }
        if (clientRecord == null) {
            errorMessages.put("secret", "is bad");
        }
        ApplicationRecord applicationRecord = null;
        if (clientRecord != null) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("secret", "is bad");
        }

        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("username", "is required");
        } else {
            int count = context.selectCount().from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(Integer.class);
            if (count > 0) {
                errorMessages.put("username", "is not available");
            }
        }

        if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
            errorMessages.put("password", "is required");
        }

        // finish type validation

        if (!errorMessages.isEmpty()) {
            SecuritySignUpResponse response = new SecuritySignUpResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        String userId = UUID.randomUUID().toString();
        boolean good = UserFunction.createUser(userId, context, jdbcTemplate, request, requestBody);
        if (!good) {
            SecuritySignUpResponse response = new SecuritySignUpResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResult(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            return ResponseEntity.ok(response);
        }

        SecuritySignUpResponse responseBody = new SecuritySignUpResponse();

        responseBody.getData().setUserId(userId);
        responseBody.getData().setLogin(requestBody.getUsername());

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/login

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLoginResponse> login(
            HttpServletRequest request,
            @RequestBody SecurityLoginRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");

        ClientRecord clientRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is required");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_SECRET.eq(requestBody.getSecret())).fetchOneInto(clientTable);
        }
        if (clientRecord == null) {
            errorMessages.put("credential", "bad credential");
        }

        ApplicationRecord applicationRecord = null;
        if (clientRecord != null) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }
        if (applicationRecord == null) {
            errorMessages.put("credential", "bad credential");
        }

        UserRecord userRecord = null;
        MobileRecord mobileRecord = null;
        if (applicationRecord != null) {
            if (requestBody.getUsername() != null && !"".equals(requestBody.getUsername())
                    && requestBody.getPassword() != null && !"".equals(requestBody.getPassword())) {
                List<Condition> where = new ArrayList<>();
                where.add(userTable.LOGIN.eq(requestBody.getUsername()));
                where.add(userTable.PASSWORD.eq(requestBody.getPassword()));
                userRecord = context.select(userTable.fields()).from(userTable).where(where).fetchOneInto(userTable);
                if (userRecord == null) {
                    errorMessages.put("credential", "bad credential");
                } else {
                    mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(requestBody.getDeviceToken())).and(mobileTable.DEVICE_TYPE.eq(requestBody.getDeviceType())).fetchOneInto(mobileTable);
                    if (mobileRecord == null) {
                        mobileRecord = context.newRecord(mobileTable);
                        mobileRecord.setMobileId(UUID.randomUUID().toString());
                        mobileRecord.setDateCreated(new Date());
                        mobileRecord.setClientId(clientRecord.getClientId());
                        mobileRecord.setDeviceToken(requestBody.getDeviceToken());
                        mobileRecord.setApplicationId(clientRecord.getApplicationId());
                        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                        mobileRecord.setClientIp(request.getRemoteAddr());
                        mobileRecord.store();
                    }
                }
            } else {
                if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
                    errorMessages.put("username", "is required");
                }
                if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
                    errorMessages.put("password", "is required");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            SecurityLoginResponse response = new SecurityLoginResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        SecurityLoginResponse responseBody = new SecurityLoginResponse();

        if (mobileRecord != null) {
            responseBody.getData().setBearer(mobileRecord.getMobileId());
            responseBody.getData().setDateCreated(mobileRecord.getDateCreated());
        }
        if (userRecord != null) {
            responseBody.getData().setLogin(userRecord.getLogin());
        }

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutResponse> logout(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody SecurityLogoutRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));

        SecurityLogoutResponse responseBody = new SecurityLogoutResponse();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
        String userId = mobileRecord.getOwnerUserId();

        context.delete(mobileTable).where(mobileTable.OWNER_USER_ID.eq(userId)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout/{session}

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{session}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutSessionResponse> logoutSession(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @PathVariable("session") String session

    ) {
        LOGGER.info("{} appCode=>{} session=>{}", request.getRequestURL(), appCode, request.getHeader("X-MBAAS-MOBILE"));

        SecurityLogoutSessionResponse responseBody = new SecurityLogoutSessionResponse();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        context.delete(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

}

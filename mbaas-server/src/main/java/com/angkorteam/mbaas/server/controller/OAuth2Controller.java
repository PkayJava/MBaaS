package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.GrantTypeEnum;
import com.angkorteam.mbaas.plain.request.oauth2.OAuth2RefreshRequest;
import com.angkorteam.mbaas.plain.request.oauth2.OAuth2TokenRequest;
import com.angkorteam.mbaas.plain.response.oauth2.*;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by socheat on 3/30/16.
 */
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private Gson gson;

    @RequestMapping(
            path = "/authorize",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuth2AuthorizeResponse> authorize(
            HttpServletRequest request,
            Identity identity,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "code", required = false) String code) {
        LOGGER.info("{} client_id=>{} client_secret=>{} grant_type=>{} redirect_uri=>{} code=>{} state=>{}", request.getRequestURL(), clientId, clientSecret, grantType, redirectUri, code, state);
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");

        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(clientId)).and(clientTable.CLIENT_SECRET.eq(clientSecret)).fetchOneInto(clientTable);
        if (clientRecord == null) {
            OAuth2AuthorizeResponse response = new OAuth2AuthorizeResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        if (applicationRecord == null) {
            OAuth2AuthorizeResponse response = new OAuth2AuthorizeResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }
        if (!"authorization_code".equals(grantType)) {
            OAuth2AuthorizeResponse response = new OAuth2AuthorizeResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }

        AuthorizationTable authorizationTable = Tables.AUTHORIZATION.as("authorizationTable");
        List<Condition> where = new ArrayList<>();
        where.add(authorizationTable.CLIENT_ID.eq(clientRecord.getClientId()));
        where.add(authorizationTable.APPLICATION_ID.eq(applicationRecord.getApplicationId()));
        where.add(authorizationTable.AUTHORIZATION_ID.eq(code));
        where.add(authorizationTable.STATE.eq(state));
        AuthorizationRecord authorizationRecord = context.select(authorizationTable.fields()).from(authorizationTable).where(where).fetchOneInto(authorizationTable);
        if (authorizationRecord == null) {
            OAuth2AuthorizeResponse response = new OAuth2AuthorizeResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }
        authorizationRecord.delete();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(identity.getAccessToken())).fetchOneInto(mobileTable);
        if (mobileRecord == null) {
            mobileRecord = context.newRecord(mobileTable);
            mobileRecord.setMobileId(UUID.randomUUID().toString());
            mobileRecord.setApplicationId(applicationRecord.getApplicationId());
            mobileRecord.setClientId(clientRecord.getClientId());
            mobileRecord.setOwnerUserId(authorizationRecord.getOwnerUserId());
            mobileRecord.setClientIp(request.getRemoteAddr());
            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            mobileRecord.setDateCreated(new Date());
            mobileRecord.setTimeToLive(configuration.getInt(Constants.ACCESS_TOKEN_TIME_TO_LIVE));
            mobileRecord.setDateTokenIssued(new Date());
            String accessToken = UUID.randomUUID().toString();
            while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
                accessToken = UUID.randomUUID().toString();
            }
            mobileRecord.setAccessToken(accessToken);
            mobileRecord.setGrantType(GrantTypeEnum.Authorization.getLiteral());
            mobileRecord.store();
        } else {
            mobileRecord.setMobileId(UUID.randomUUID().toString());
            mobileRecord.setApplicationId(applicationRecord.getApplicationId());
            mobileRecord.setClientId(clientRecord.getClientId());
            mobileRecord.setOwnerUserId(authorizationRecord.getOwnerUserId());
            mobileRecord.setClientIp(request.getRemoteAddr());
            mobileRecord.setGrantType(GrantTypeEnum.Authorization.getLiteral());
            mobileRecord.setDateTokenIssued(new Date());
            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            String accessToken = UUID.randomUUID().toString();
            while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
                accessToken = UUID.randomUUID().toString();
            }
            mobileRecord.setAccessToken(accessToken);
            mobileRecord.update();
        }

        OAuth2AuthorizeResponse response = new OAuth2AuthorizeResponse();
        response.setAccessToken(mobileRecord.getAccessToken());
        response.setRefreshToken(mobileRecord.getMobileId());
        response.setExpiresIn(mobileRecord.getTimeToLive());
        response.setTokenType("bearer");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/implicit",
            method = {RequestMethod.POST, RequestMethod.GET}
    )
    public ResponseEntity<Void> implicit(
            HttpServletRequest request,
            @RequestParam(value = "response_type", required = false) String responseType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state
    ) {
        LOGGER.info("{} response_type=>{} client_id=>{} redirect_uri=>{} scope=>{} state=>{}", request.getRequestURL(), responseType, clientId, redirectUri, scope, state);
        Map<String, String> errorMessages = new LinkedHashMap<>();

        if (!"code".equals(responseType)) {
            errorMessages.put("response_type", "bad value");
        }
        if (redirectUri == null || "".equals(redirectUri)) {
            errorMessages.put("redirect_uri", "is required");
        }
        ClientRecord clientRecord = null;
        if (clientId == null || "".equals(clientId)) {
            errorMessages.put("redirect_uri", "is required");
        } else {
            ClientTable clientTable = Tables.CLIENT.as("clientTable");
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(clientId)).fetchOneInto(clientTable);
            if (clientRecord == null) {
                errorMessages.put("redirect_uri", "is required");
            }
        }
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = null;
        applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        if (applicationRecord == null) {
            errorMessages.put("redirect_uri", "is required");
        }

        if (!errorMessages.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            List<String> errorParams = new ArrayList<>();
            errorParams.add("error=" + HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorParams.add("error_description=" + HttpStatus.INTERNAL_SERVER_ERROR.getDeclaringClass());
            errorParams.add("error_uri=" + redirectUri);
            if (state != null && !"".equals(state)) {
                errorParams.add("state=" + state);
            }
            headers.add(HttpHeaders.LOCATION, redirectUri + StringUtils.join(errorParams, "&"));
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.newRecord(mobileTable);
        mobileRecord.setMobileId(UUID.randomUUID().toString());
        mobileRecord.setApplicationId(applicationRecord.getApplicationId());
        mobileRecord.setClientId(clientRecord.getClientId());
        mobileRecord.setClientIp(request.getRemoteAddr());
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        mobileRecord.setDateCreated(new Date());
        mobileRecord.setTimeToLive(configuration.getInt(Constants.ACCESS_TOKEN_TIME_TO_LIVE));
        mobileRecord.setDateTokenIssued(new Date());
        String accessToken = UUID.randomUUID().toString();
        while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
            accessToken = UUID.randomUUID().toString();
        }
        mobileRecord.setAccessToken(accessToken);
        mobileRecord.setGrantType(GrantTypeEnum.Implicit.getLiteral());
        mobileRecord.store();

        List<String> params = new ArrayList<>();
        params.add("access_token=" + mobileRecord.getAccessToken());
        params.add("token_type=bearer");
        params.add("expires_in=" + mobileRecord.getTimeToLive());
        if (scope != null && !"".equals(scope)) {
            params.add("scope=" + scope);
        }
        if (state != null && !"".equals(state)) {
            params.add("state=" + state);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUri + "?" + StringUtils.join(params, "&"));
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();
    }

    @RequestMapping(
            path = "/password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuth2PasswordResponse> password(
            HttpServletRequest request,
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "scope", required = false) String scope
    ) {
        LOGGER.info("{} grant_type=>{} username=>{} password=>{} scope=>{}", request.getRequestURL(), grantType, username, password, scope);
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        if (!"password".equals(grantType)) {
            errorMessages.put("grant_type", "incorrect");
        }

        if (password == null || "".equals(password)) {
            errorMessages.put("credential", "incorrect");
        }
        if (username == null || "".equals(username)) {
            errorMessages.put("credential", "incorrect");
        }

        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = null;
        if (username != null && !"".equals(username) && password != null && !"".equals(password)) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("credential", "incorrect");
            }
        }

        if (!errorMessages.isEmpty()) {
            OAuth2PasswordResponse response = new OAuth2PasswordResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.newRecord(mobileTable);
        mobileRecord.setMobileId(UUID.randomUUID().toString());
        mobileRecord.setOwnerUserId(userRecord.getUserId());
        mobileRecord.setClientIp(request.getRemoteAddr());
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        mobileRecord.setDateCreated(new Date());
        mobileRecord.setTimeToLive(configuration.getInt(Constants.ACCESS_TOKEN_TIME_TO_LIVE));
        mobileRecord.setDateTokenIssued(new Date());
        String accessToken = UUID.randomUUID().toString();
        while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
            accessToken = UUID.randomUUID().toString();
        }
        mobileRecord.setAccessToken(accessToken);
        mobileRecord.setGrantType(GrantTypeEnum.Password.getLiteral());
        mobileRecord.store();

        OAuth2PasswordResponse response = new OAuth2PasswordResponse();
        response.setTokenType("bearer");
        response.setAccessToken(mobileRecord.getAccessToken());
        response.setExpiresIn(mobileRecord.getTimeToLive());
        response.setRefreshToken(mobileRecord.getMobileId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/client",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuth2ClientResponse> client(
            HttpServletRequest request,
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "scope", required = false) String scope
    ) {
        LOGGER.info("{} grant_type=>{} scope=>{}", request.getRequestURL(), grantType, scope);
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        if (!"client_credentials".equals(grantType)) {
            errorMessages.put("grant_type", "incorrect");
        }

        if (!errorMessages.isEmpty()) {
            OAuth2ClientResponse response = new OAuth2ClientResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.newRecord(mobileTable);
        mobileRecord.setMobileId(UUID.randomUUID().toString());
        mobileRecord.setClientIp(request.getRemoteAddr());
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        mobileRecord.setDateCreated(new Date());
        mobileRecord.setTimeToLive(configuration.getInt(Constants.ACCESS_TOKEN_TIME_TO_LIVE));
        mobileRecord.setDateTokenIssued(new Date());
        String accessToken = UUID.randomUUID().toString();
        while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
            accessToken = UUID.randomUUID().toString();
        }
        mobileRecord.setAccessToken(accessToken);
        mobileRecord.setGrantType(GrantTypeEnum.Client.getLiteral());
        mobileRecord.store();

        OAuth2ClientResponse response = new OAuth2ClientResponse();
        response.setTokenType("bearer");
        response.setAccessToken(mobileRecord.getAccessToken());
        response.setExpiresIn(mobileRecord.getTimeToLive());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/token",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuth2TokenResponse> token(
            HttpServletRequest request,
            @RequestBody OAuth2TokenRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        if (requestBody.getAccessToken() == null || "".equals(requestBody.getAccessToken())) {
            errorMessages.put("accessToken", "error");
        } else {
            MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
            MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(requestBody.getAccessToken())).fetchOneInto(mobileTable);
            if (mobileRecord == null) {
                errorMessages.put("accessToken", "error");
            } else {
                DateTime dateTime = new DateTime(mobileRecord.getDateTokenIssued());
                dateTime = dateTime.plusSeconds(mobileRecord.getTimeToLive());
                if (dateTime.isBeforeNow()) {
                    OAuth2TokenResponse response = new OAuth2TokenResponse();
                    response.setHttpCode(HttpStatus.NOT_EXTENDED.value());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            OAuth2TokenResponse response = new OAuth2TokenResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }

        OAuth2TokenResponse response = new OAuth2TokenResponse();
        response.setScope("");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            path = "/refresh",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuth2RefreshResponse> refresh(
            HttpServletRequest request,
            @RequestBody OAuth2RefreshRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        MobileRecord mobileRecord = null;
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        if (requestBody.getRefreshToken() == null || "".equals(requestBody.getRefreshToken())) {
            errorMessages.put("refreshToken", "error");
        } else {
            mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(requestBody.getRefreshToken())).fetchOneInto(mobileTable);
            if (mobileRecord == null) {
                errorMessages.put("refreshToken", "error");
            }
        }

        if (!errorMessages.isEmpty()) {
            OAuth2RefreshResponse response = new OAuth2RefreshResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(response);
        }

        String accessToken = UUID.randomUUID().toString();
        while (context.selectCount().from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(int.class) > 0) {
            accessToken = UUID.randomUUID().toString();
        }
        mobileRecord.setAccessToken(accessToken);
        mobileRecord.setDateTokenIssued(new Date());
        mobileRecord.update();

        OAuth2RefreshResponse response = new OAuth2RefreshResponse();
        response.setTokenType("bearer");
        response.setExpiresIn(mobileRecord.getTimeToLive());
        response.setAccessToken(accessToken);

        return ResponseEntity.ok(response);
    }
}

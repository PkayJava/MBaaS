package com.angkorteam.mbaas.api;

import com.angkorteam.baasbox.sdk.java.json.*;
import com.angkorteam.baasbox.sdk.java.request.SendPushNotificationRequest;
import com.angkorteam.mbaas.Constants;
import com.angkorteam.mbaas.enums.ResultEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.Application;
import com.angkorteam.mbaas.model.entity.tables.Token;
import com.angkorteam.mbaas.model.entity.tables.User;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.TokenRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.response.Response;
import com.angkorteam.mbaas.service.RequestHeader;
import com.google.gson.Gson;
import javafx.scene.control.Tab;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> login(
            HttpServletRequest request,
            @RequestBody LoginRequest requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");

        List<Condition> where = new ArrayList<>();
        where.add(userTable.LOGIN.eq(requestBody.getUsername()));
        where.add(userTable.PASSWORD.eq(requestBody.getPassword()));

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(where).fetchOneInto(userTable);
        if (userRecord != null) {
            responseBody.setHttpCode(HttpStatus.OK.value());
            responseBody.setResult(ResultEnum.OK.getLiteral());

            Map<String, Object> loginResponse = new HashMap<>();
            String tokenId = UUID.randomUUID().toString();
            Date dateCreated = new Date();


            TokenRecord tokenRecord = context.newRecord(tokenTable);
            tokenRecord.setTokenId(tokenId);
            tokenRecord.setDateCreated(new Timestamp(dateCreated.getTime()));
            tokenRecord.setUserId(userRecord.getUserId());
            tokenRecord.setDeleted(false);
            tokenRecord.store();

            loginResponse.put("token", tokenId);
            loginResponse.put("dateCreated", dateCreated);
            loginResponse.put("login", userRecord.getLogin());
            responseBody.setData(loginResponse);
        } else {
            responseBody.setHttpCode(HttpStatus.BAD_REQUEST.value());
            responseBody.setResult(ResultEnum.ERROR.getLiteral());
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signup(
            HttpServletRequest request,
            @RequestBody SignupRequest requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");

        String appCode = requestBody.getAppCode();
        String login = requestBody.getUsername();
        String password = requestBody.getPassword();

        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(appCode)).fetchOneInto(applicationTable);
        Integer applicationId = null;
        if (applicationRecord != null) {
            applicationId = applicationRecord.getApplicationId();
        }


        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setDeleted(false);
        userRecord.setAccountNonExpired(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setDisabled(false);
        userRecord.setLogin(login);
        userRecord.setPassword(password);
        userRecord.setApplicationId(applicationId);
        userRecord.store();

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(HttpStatus.OK.value());

        Map<String, Object> signupResponse = new HashMap<>();
        String tokenId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        TokenRecord tokenRecord = context.newRecord(tokenTable);
        tokenRecord.setTokenId(tokenId);
        tokenRecord.setDateCreated(new Timestamp(dateCreated.getTime()));
        tokenRecord.setUserId(userRecord.getUserId());
        tokenRecord.setDeleted(false);
        tokenRecord.store();

        signupResponse.put("token", tokenId);
        signupResponse.put("dateCreated", dateCreated);
        signupResponse.put("login", userRecord.getLogin());
        responseBody.setData(signupResponse);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        Integer userId = tokenRecord.getUserId();
        context.delete(tokenTable).where(tokenTable.USER_ID.eq(userId)).execute();

        responseBody.setHttpCode(200);
        responseBody.setResult(ResultEnum.OK.getLiteral());

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        context.delete(tokenTable).where(tokenTable.TOKEN_ID.eq(token)).execute();

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(200);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/suspend",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        User userTable = Tables.USER.as("userTable");

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = null;

        if (tokenRecord != null) {
            context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        }

        if (userRecord != null) {
            userRecord.setAccountNonLocked(false);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/suspend/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord != null) {
            userRecord.setAccountNonLocked(false);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/activate/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activateUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord != null) {
            userRecord.setAccountNonLocked(true);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loggedUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUsers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchUsersRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changePassword(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangePasswordJson request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changeUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangeUsernameRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/user/{username}/password/reset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> passwordReset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/social",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> social(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLogin(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLoginRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialUnlink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/following/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowing(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/followers/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("os") String os,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/disable/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> disablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/push/message",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> sendPushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody SendPushNotificationRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody CreateDocumentRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentByQuery(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody RetrieveDocumentByQueryRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}/count",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> countDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody ModifyDocumentRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/role/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/link/{sourceId}/{label}/{destinationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("sourceId") String sourceId,
            @PathVariable("label") String label,
            @PathVariable("destinationId") String destinationId,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> uploadFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UploadFileRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFileDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFilesDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveFilesDetailRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/asset",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> createAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CreateAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> deleteAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/asset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/dump.json",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchCurrentSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/{section}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchSectionSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/configuration/{section}/{key}/{value}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> updateValueSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @PathVariable("key") String key,
            @PathVariable("value") String value,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> listGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> readSpecificGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> enableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> disableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }
}

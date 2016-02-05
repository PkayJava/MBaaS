package com.angkorteam.mbaas.api;

import com.angkorteam.baasbox.sdk.java.json.*;
import com.angkorteam.baasbox.sdk.java.request.SendPushNotificationRequest;
import com.angkorteam.baasbox.sdk.java.response.StringResponse;
import com.angkorteam.baasbox.sdk.java.response.SuccessResponse;
import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.response.Response;
import com.google.gson.Gson;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import retrofit.http.*;

import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @RequestMapping(method = RequestMethod.POST, path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signup",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout/{pushToken}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("pushToken") String pushToken,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/suspend",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/admin/user/suspend/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/admin/user/activate/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activateUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loggedUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUsers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchUsersRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/password",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changePassword(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangePasswordJson request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/username",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changeUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangeUsernameRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{username}/password/reset",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> passwordReset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/social",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> social(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLogin(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLinkRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialUnlink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/follow/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/follow/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/following/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowing(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/followers/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("os") String os,
            @PathVariable("token") String token,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/push/disable/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> disablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/push/message",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> sendPushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody SendPushNotificationRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/admin/collection/{collection}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/collection/{collection}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/document/{collection}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody CreateDocumentRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/document/{collection}/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/document/{collection}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentByQuery(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody RetrieveDocumentByQueryRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/document/{collection}/count",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> countDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/document/{collection}/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody ModifyDocumentRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/document/{collection}/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/role/{rolename}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{rolename}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/link/{sourceId}/{label}/{destinationId}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("sourceId") String sourceId,
            @PathVariable("label") String label,
            @PathVariable("destinationId") String destinationId,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/link/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

}

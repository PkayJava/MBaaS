package com.angkorteam.mbaas.api;

import com.angkorteam.baasbox.sdk.java.json.*;
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
    public ResponseEntity<Response> loginPost(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signup",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signupPost(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logoutPost(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout/{pushToken}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logoutPost(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("pushToken") String pushToken,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/suspend",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUserPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/admin/user/suspend/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUserPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/admin/user/activate/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activateUserPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loggedUserProfileGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfilePut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfileGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUsersGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchUsersRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/password",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changePasswordPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangePasswordJson request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/me/username",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changeUsernamePut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangeUsernameRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{username}/password/reset",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> passwordResetGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/social",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLoginPost(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLinkPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLinkRequest request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/social/{socialNetwork}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialUnlinkDelete(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/follow/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUserPost(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/follow/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUserDelete(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/following/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowingGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/followers/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowersGet(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotificationPut(
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
    public ResponseEntity<Response> disablePushNotificationPut(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request request) {
        return ResponseEntity.ok(null);
    }

//    @POST("/push/message")
//    public StringResponse sendPushNotification(@Header("X-BB-SESSION") String session, @Body SendPushNotificationJson json);
}

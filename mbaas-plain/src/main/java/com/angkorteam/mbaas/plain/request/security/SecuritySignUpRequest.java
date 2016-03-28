package com.angkorteam.mbaas.plain.request.security;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
public class SecuritySignUpRequest extends Request {

    @Expose
    @SerializedName("secret")
    private String secret;

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("password")
    private String password;

    @Expose
    @SerializedName("visibleByTheUser")
    private Map<String, Object> visibleByTheUser = new HashMap<>();

    @Expose
    @SerializedName("visibleByFriends")
    private Map<String, Object> visibleByFriends = new HashMap<>();

    @Expose
    @SerializedName("visibleByRegisteredUsers")
    private Map<String, Object> visibleByRegisteredUsers = new HashMap<>();

    @Expose
    @SerializedName("visibleByAnonymousUsers")
    private Map<String, Object> visibleByAnonymousUsers = new HashMap<>();

    @Expose
    @SerializedName("deviceToken")
    private String deviceToken;

    @Expose
    @SerializedName("deviceType")
    private String deviceType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getVisibleByTheUser() {
        return visibleByTheUser;
    }

    public void setVisibleByTheUser(Map<String, Object> visibleByTheUser) {
        this.visibleByTheUser = visibleByTheUser;
    }

    public Map<String, Object> getVisibleByFriends() {
        return visibleByFriends;
    }

    public void setVisibleByFriends(Map<String, Object> visibleByFriends) {
        this.visibleByFriends = visibleByFriends;
    }

    public Map<String, Object> getVisibleByRegisteredUsers() {
        return visibleByRegisteredUsers;
    }

    public void setVisibleByRegisteredUsers(Map<String, Object> visibleByRegisteredUsers) {
        this.visibleByRegisteredUsers = visibleByRegisteredUsers;
    }

    public Map<String, Object> getVisibleByAnonymousUsers() {
        return visibleByAnonymousUsers;
    }

    public void setVisibleByAnonymousUsers(Map<String, Object> visibleByAnonymousUsers) {
        this.visibleByAnonymousUsers = visibleByAnonymousUsers;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}

package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
public class SignupRequest extends Request {

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("password")
    private String password;

    @Expose
    @SerializedName("appcode")
    private String appCode;

    @Expose
    @SerializedName("token")
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}

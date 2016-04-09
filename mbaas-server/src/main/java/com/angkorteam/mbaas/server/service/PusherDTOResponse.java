package com.angkorteam.mbaas.server.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/31/16.
 */
public class PusherDTOResponse implements Serializable {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("enabled")
    private boolean enabled;

    @Expose
    @SerializedName("deviceType")
    private String deviceType;

    @Expose
    @SerializedName("deviceToken")
    private String deviceToken;

    @Expose
    @SerializedName("alias")
    private String alias;

    @Expose
    @SerializedName("operatingSystem")
    private String operatingSystem;

    @Expose
    @SerializedName("osVersion")
    private String osVersion;

    @Expose
    @SerializedName("platform")
    private String platform;

    @Expose
    @SerializedName("categories")
    private List<Map<String, String>> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public List<Map<String, String>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, String>> categories) {
        this.categories = categories;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}

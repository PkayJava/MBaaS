package com.angkorteam.mbaas.plain.request.device;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/28/16.
 */
public class DeviceRegisterRequest extends Request {

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
    @SerializedName("categories")
    private List<String> categories = new ArrayList<>();

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

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}

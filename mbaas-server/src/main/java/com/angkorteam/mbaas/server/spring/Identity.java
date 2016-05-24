package com.angkorteam.mbaas.server.spring;

/**
 * Created by socheat on 4/10/16.
 */
public final class Identity implements com.angkorteam.mbaas.plain.Identity {

    private final String mbaasUserId;

    private final String applicationCode;

    private final String applicationUserId;

    private final String applicationId;

    private final String clientId;

    private final String clientSecret;

    private final String mobileId;

    private final String userAgent;

    private final String remoteIp;

    private final String accessToken;

    private final String appVersion;

    private final String SDKVersion;

    public Identity(String mbaasUserId, String applicationUserId, String applicationId, String applicationCode, String clientId, String clientSecret, String mobileId, String userAgent, String remoteIp, String accessToken, String appVersion, String sdkVersion) {
        this.mbaasUserId = mbaasUserId;
        this.applicationCode = applicationCode;
        this.applicationUserId = applicationUserId;
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.mobileId = mobileId;
        this.userAgent = userAgent;
        this.remoteIp = remoteIp;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        this.appVersion = appVersion;
        this.SDKVersion = sdkVersion;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getApplicationUserId() {
        return this.applicationUserId;
    }

    @Override
    public String getMBaasId() {
        return this.mobileId;
    }

    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public String getMobileId() {
        return this.mobileId;
    }

    @Override
    public String getRemoteIp() {
        return this.remoteIp;
    }

    @Override
    public String getUserAgent() {
        return this.userAgent;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getAppVersion() {
        return this.appVersion;
    }

    @Override
    public String getSDKVersion() {
        return this.SDKVersion;
    }

    @Override
    public String getApplicationCode() {
        return applicationCode;
    }

    public String getMbaasUserId() {
        return mbaasUserId;
    }
}

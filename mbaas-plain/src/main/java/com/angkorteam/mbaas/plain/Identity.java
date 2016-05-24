package com.angkorteam.mbaas.plain;

import java.io.Serializable;

/**
 * Created by socheat on 4/10/16.
 */
public interface Identity extends Serializable {

    String getApplicationId();

    String getApplicationCode();

    String getApplicationUserId();

    String getMBaasId();

    String getClientId();

    String getClientSecret();

    String getMobileId();

    String getAccessToken();

    String getRemoteIp();

    String getUserAgent();

    String getAppVersion();

    String getSDKVersion();

}

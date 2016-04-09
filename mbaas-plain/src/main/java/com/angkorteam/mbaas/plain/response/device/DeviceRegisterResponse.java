package com.angkorteam.mbaas.plain.response.device;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 3/28/16.
 */
public class DeviceRegisterResponse extends Response<DeviceRegisterResponse.Body> {

    public DeviceRegisterResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("accessToken")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}

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
        @SerializedName("bearer")
        private String bearer;

        public String getBearer() {
            return bearer;
        }

        public void setBearer(String bearer) {
            this.bearer = bearer;
        }

    }
}

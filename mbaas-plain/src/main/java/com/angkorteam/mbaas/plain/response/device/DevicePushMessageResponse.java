package com.angkorteam.mbaas.plain.response.device;

import com.angkorteam.mbaas.plain.response.Response;

/**
 * Created by socheat on 3/28/16.
 */
public class DevicePushMessageResponse extends Response<DevicePushMessageResponse.Body> {

    public DevicePushMessageResponse() {
        this.data = new Body();
    }

    public static class Body {

    }
}

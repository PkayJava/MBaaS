package com.angkorteam.mbaas.plain.response.security;

import com.angkorteam.mbaas.plain.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by socheat on 2/16/16.
 */
public class SecurityLogoutResponse extends Response<Map<String, Object>> {

    public SecurityLogoutResponse() {
        this.data = new LinkedHashMap<>();
    }

}

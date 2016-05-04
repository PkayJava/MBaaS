package com.angkorteam.mbaas.plain.response.me;

import com.angkorteam.mbaas.plain.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 2/17/16.
 */
public class MeResponse extends Response<Map<String, Object>> {

    public MeResponse() {
        this.data = new HashMap<>();
    }

}

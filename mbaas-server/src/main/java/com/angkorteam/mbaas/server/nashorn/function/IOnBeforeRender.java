package com.angkorteam.mbaas.server.nashorn.function;

import com.angkorteam.mbaas.server.nashorn.Factory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
public interface IOnBeforeRender extends Serializable {

    void onBeforeRender(Factory factory, Map<String, Object> userModel);

}

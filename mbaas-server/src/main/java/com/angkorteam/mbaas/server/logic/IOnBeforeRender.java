package com.angkorteam.mbaas.server.logic;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
public interface IOnBeforeRender extends Serializable {

    void onBeforeRender(Page page, Map<String, Object> model);

}

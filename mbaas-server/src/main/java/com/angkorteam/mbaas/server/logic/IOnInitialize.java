package com.angkorteam.mbaas.server.logic;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
public interface IOnInitialize extends Serializable {

    void onInitialize(Page page, Map<String, Object> model);

}

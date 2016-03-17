package com.angkorteam.mbaas.server.wicket;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Created by socheat on 3/12/16.
 */
public class JavaScript {

    public static String test(ScriptObjectMirror a, ScriptObjectMirror b) {
        return "";
    }

    public static void fun3(Object object) {
        if (object instanceof JSObject) {
            JSObject js = (JSObject) object;
            if (js.isFunction() || js.isStrictFunction()) {
                js.call(null, "undefined");
            } else if (js.isArray()) {
                for (Object value : js.values()) {
                    System.out.println(value + " - ");
                }
            } else {
                for (String key : js.keySet()) {
                    System.out.println(key + " : " + js.getMember(key));
                }
            }
        } else if (object instanceof String) {
            System.out.println(object);
        }
    }

}

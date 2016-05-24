package com.angkorteam.mbaas.server;

import com.angkorteam.mbaas.plain.enums.UserStatusEnum;

import java.util.Map;

/**
 * Created by socheat on 5/22/16.
 */
public class StaticCommon {
    public static boolean hasAccess(String link, Map<String, Object> object) {
        String status = (String) object.get("status");
        Boolean system = (Boolean) object.get("system");
        if ("Suspend".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Activate".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Active.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Edit".equals(link)) {
            if (system) {
                return false;
            }
        }
        return true;
    }

    public static String onCSSLink(String link, Map<String, Object> object) {
        if ("Suspend".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Activate".equals(link)) {
            return "btn-xs btn-warning";
        }
        if ("Change PWD".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}

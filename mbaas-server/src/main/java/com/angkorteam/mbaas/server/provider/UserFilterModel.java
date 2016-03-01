package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.model.FilterModel;

/**
 * Created by socheat on 3/1/16.
 */
public class UserFilterModel implements FilterModel {

    private String userId;

    private String login;

    private String roleName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

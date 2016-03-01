package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.model.FilterModel;

/**
 * Created by socheat on 3/1/16.
 */
public class RoleFilterModel implements FilterModel {

    private String roleId;

    private String name;

    private String system;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}

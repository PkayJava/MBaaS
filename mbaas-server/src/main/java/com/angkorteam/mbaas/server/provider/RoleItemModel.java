package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.model.ItemModel;

/**
 * Created by socheat on 3/1/16.
 */
public class RoleItemModel implements ItemModel {

    private String roleId;

    private boolean system;

    private String name;

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

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}

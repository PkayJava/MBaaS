package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import org.jooq.DSLContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

/**
 * Created by socheat on 11/5/16.
 */
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private DSLContext context;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserTable userTable = Tables.USER.as("userTable");
        UserPojo userPojo = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(UserPojo.class);
        if (userPojo == null) {
            throw new UsernameNotFoundException(username + " is not found");
        }
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        RolePojo rolePojo = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userPojo.getRoleId())).fetchOneInto(RolePojo.class);
        User user = new User(username, userPojo.getPassword(), Arrays.asList(new SimpleGrantedAuthority(rolePojo.getName())));
        return user;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }
}

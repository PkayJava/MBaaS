package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class UserDetailsServiceFactoryBean implements FactoryBean<UserDetailsService>, UserDetailsService {

    private DSLContext context;

    @Override
    public UserDetailsService getObject() throws Exception {
        return this;
    }

    public DSLContext getContext() {
        return context;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }

    @Override
    public Class<?> getObjectType() {
        return UserDetailsService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = this.context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);
        if (userRecord == null) {
            throw new UsernameNotFoundException(username + " is not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = Arrays.asList("ABC");
        for (String role : roles) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            authorities.add(authority);
        }
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                username, userRecord.getPassword(),
                !userRecord.getDisabled(),
                userRecord.getAccountNonExpired(),
                userRecord.getCredentialsNonExpired(),
                userRecord.getAccountNonLocked(), authorities
        );
        return userDetails;
    }
}

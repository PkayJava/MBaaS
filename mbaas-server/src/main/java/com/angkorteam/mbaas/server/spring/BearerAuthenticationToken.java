package com.angkorteam.mbaas.server.spring;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by socheat on 3/26/16.
 */
public class BearerAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    private final String principal;

    private final String credentials;

    public BearerAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null;
        this.credentials = null;
    }

    public BearerAuthenticationToken(String token, String principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public final String getCredentials() {
        return this.credentials;
    }

    @Override
    public final String getPrincipal() {
        return this.principal;
    }

    public final String getToken() {
        return this.token;
    }
}

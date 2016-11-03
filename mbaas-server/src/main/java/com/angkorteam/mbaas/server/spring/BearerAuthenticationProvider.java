package com.angkorteam.mbaas.server.spring;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by socheat on 3/27/16.
 */
public class BearerAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        throw new BadCredentialsException("bearer is not support is not valid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (BearerAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

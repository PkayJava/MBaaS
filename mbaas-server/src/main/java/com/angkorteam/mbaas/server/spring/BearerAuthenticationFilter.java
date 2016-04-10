package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class BearerAuthenticationFilter extends OncePerRequestFilter {

    // ~ Instance fields
    // ================================================================================================

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AuthenticationManager authenticationManager;
    private boolean ignoreFailure = false;
    private String credentialsCharset = "UTF-8";
    private Gson gson;

    /**
     * Creates an instance which will authenticate against the supplied
     * {@code AuthenticationManager} and which will ignore failed authentication attempts,
     * allowing the request to proceed down the filter chain.
     *
     * @param authenticationManager the bean to submit authentication requests to
     */
    public BearerAuthenticationFilter(AuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        this.authenticationManager = authenticationManager;
        ignoreFailure = true;
    }

    /**
     * Creates an instance which will authenticate against the supplied
     * {@code AuthenticationManager} and use the supplied {@code AuthenticationEntryPoint}
     * to handle authentication failures.
     *
     * @param authenticationManager    the bean to submit authentication requests to
     * @param authenticationEntryPoint will be invoked when authentication fails.
     *                                 Typically an instance of {@link BasicAuthenticationEntryPoint}.
     */
    public BearerAuthenticationFilter(AuthenticationManager authenticationManager,
                                      AuthenticationEntryPoint authenticationEntryPoint) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.notNull(authenticationEntryPoint,
                "authenticationEntryPoint cannot be null");
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");

        if (!isIgnoreFailure()) {
            Assert.notNull(this.authenticationEntryPoint,
                    "An AuthenticationEntryPoint is required");
        }
    }

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final boolean debug = logger.isDebugEnabled();

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.toUpperCase().startsWith("BEARER ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String bearer = authorization.substring(7);

            BearerAuthenticationToken authRequest = new BearerAuthenticationToken(bearer);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication authResult = authenticationManager
                    .authenticate(authRequest);
            authResult.setAuthenticated(true);

            if (debug) {
                logger.debug("Authentication success: " + authResult);
            }
            SecurityContextHolder.getContext().setAuthentication(authResult);

            onSuccessfulAuthentication(request, response, authResult);

        } catch (CredentialsExpiredException e) {
            if (ignoreFailure) {
                chain.doFilter(request, response);
            } else {
                XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                UnknownResponse responseBody = new UnknownResponse();
                responseBody.setVersion(configuration.getString(Constants.APP_VERSION));
                responseBody.setMethod(request.getMethod());
                responseBody.setResult(org.springframework.http.HttpStatus.LOCKED.getReasonPhrase());
                responseBody.setHttpCode(org.springframework.http.HttpStatus.LOCKED.value());
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                this.gson.toJson(responseBody, response.getWriter());
            }
            return;
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();

            if (debug) {
                logger.debug("Authentication request for failed: " + failed);
            }

            onUnsuccessfulAuthentication(request, response, failed);

            if (ignoreFailure) {
                chain.doFilter(request, response);
            } else {
                authenticationEntryPoint.commence(request, response, failed);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    protected void onSuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, Authentication authResult) throws IOException {
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request,
                                                HttpServletResponse response, AuthenticationException failed)
            throws IOException {
    }

    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource,
                "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setCredentialsCharset(String credentialsCharset) {
        Assert.hasText(credentialsCharset, "credentialsCharset cannot be null or empty");
        this.credentialsCharset = credentialsCharset;
    }

    protected String getCredentialsCharset(HttpServletRequest httpRequest) {
        return credentialsCharset;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}

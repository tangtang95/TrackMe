package com.poianitibaldizhou.trackme.apigateway.filter.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.poianitibaldizhou.trackme.apigateway.entity.Api;
import com.poianitibaldizhou.trackme.apigateway.security.TokenAuthenticationFilter;
import com.poianitibaldizhou.trackme.apigateway.security.service.ThirdPartyAuthenticationService;
import com.poianitibaldizhou.trackme.apigateway.security.service.UserAuthenticationService;
import com.poianitibaldizhou.trackme.apigateway.util.ApiUtils;
import com.poianitibaldizhou.trackme.apigateway.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.security.AccessControlException;

/**
 * This filter controls the access of all the api calls. Indeed, it checks if the client is a third party
 * or a user and acts accordingly: api that are reserved for users, won't permit access to third party customers
 * and viceversa.
 */
public class AccessControlFilter extends ZuulFilter {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ThirdPartyAuthenticationService thirdPartyAuthenticationService;

    @Autowired
    private ApiUtils apiUtils;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

        // Find the api that is currently being accessed
        Api calledApi = apiUtils.getApiByUriWithNoPathVar(request.getRequestURI(), HttpMethod.resolve(request.getMethod()));

        if(calledApi == null) {
            throw new ZuulException(Constants.API_NOT_FOUND_EXCEPTION, HttpStatus.BAD_REQUEST.value(), Constants.API_NOT_FOUND_EXCEPTION);
        }

        // Check if the client accessing the api has real access to it
        final String token = tokenAuthenticationFilter.getToken(request);

        switch (calledApi.getPrivilege()) {
            case THIRD_PARTY:
                if(!thirdPartyAuthenticationService.findThirdPartyByToken(token).isPresent()) {
                    throw new AccessControlException(Constants.ACCESS_CONTROL_EXCEPTION_USER);
                }
                break;
            case USER:
                if(!userAuthenticationService.findUserByToken(token).isPresent()) {
                    throw new AccessControlException(Constants.ACCESS_CONTROL_EXCEPTION_TP);
                }
                break;
            case ALL:
                // Everything is fine nothing to do
                break;
        }

        return null;
    }
}

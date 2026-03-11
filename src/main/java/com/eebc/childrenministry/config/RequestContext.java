package com.eebc.childrenministry.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request-scoped bean populated by JwtAuthFilter on every authenticated request.
 * The AuditableEntityListener reads this to know WHO made the change.
 */
@Component
@RequestScope
public class RequestContext {

    private String userId;
    private String userName;       // display name e.g. "John Kim"
    private String ipAddress;
    private String userAgent;

    public String getUserId()    { return userId; }
    public String getUserName()  { return userName; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }

    public void setUserId(String userId)       { this.userId = userId; }
    public void setUserName(String userName)   { this.userName = userName; }
    public void setIpAddress(String ip)        { this.ipAddress = ip; }
    public void setUserAgent(String ua)        { this.userAgent = ua; }
}

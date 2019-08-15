package com.wlyuan.wangcan.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomTokenFilter extends ZuulFilter {
    /**
     * 白名单
     */
    @Value("${auth.path.ignore}")
    private String ignores;
    /**
     * 过期时间
     */
    @Value("${api.auth.expireMinutes}")
    private int expireMinutes;

    @Resource
    private JWTVerifier jWTVerifier;

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getHeader("Authorization");
        //如果没有 或者不是以Bearer 开头直接验证失败
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            return failed();
        }
        token = token.substring(7);
        try {
            DecodedJWT jwt = jWTVerifier.verify(token);
            Long userId = Long.valueOf(jwt.getSubject());
            Long userCompanyid = jwt.getClaim("userCompanyid").asLong();
            String userCompanyName = jwt.getClaim("userCompanyName").asString();
            String roleIds = jwt.getClaim("roleIds").asString();
            String rKey = RedisKey.getTmsUserJwtKey(userCompanyid, roleIds, userId);
            if (redisTemplate.opsForValue().get(rKey) == null) {
                return failed();
            }
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userId);
            userResponse.setUserName(jwt.getClaim("userName").asString());
            userResponse.setUserCompanyid(userCompanyid);
            userResponse.setUserCompanyName(userCompanyName);
            userResponse.setRoleIds(roleIds);
            userResponse.setUserPhone(jwt.getClaim("userPhone").asString());
            context.addZuulRequestHeader("Identity", URLEncoder.encode(JSON.toJSONString(userResponse), "UTF-8"));
            redisTemplate.expire(rKey, expireMinutes, TimeUnit.MINUTES);
            return null;
        } catch (Exception e) {
            log.error("message : [%s] , token : [%s]", e.getMessage(), token);
            return failed();
        }
    }

    private Object failed() {
        RequestContext context = RequestContext.getCurrentContext();
        context.setSendZuulResponse(false);
        HttpServletResponse response = context.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("info", "");
        context.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
    }
}

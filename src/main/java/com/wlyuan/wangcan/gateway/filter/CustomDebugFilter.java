package com.wlyuan.wangcan.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public class CustomDebugFilter extends ZuulFilter {
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
        RequestContext context=RequestContext.getCurrentContext();
        HttpServletRequest request= context.getRequest();
        request.getMethod();
        request.getRequestURI();
        String method = request.getParameter("method");
        String body = request.getParameter("content");
      Map<String, String[]>  requestParams= request.getParameterMap();
        Map<String, List<String>>  params=context.getRequestQueryParams();
        Map<String, String>  zuulHeaders =context.getZuulRequestHeaders();
        if(params!=null) {
            for (Map.Entry l : params.entrySet()) {
                System.out.println(l.getValue());
            }
        }
        if(zuulHeaders!=null) {
            for (Map.Entry l : zuulHeaders.entrySet()) {
                System.out.println(l.getKey() + ":" + l.getValue());
            }
        }
        return true;
    }
}

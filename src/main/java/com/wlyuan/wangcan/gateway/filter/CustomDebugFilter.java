package com.wlyuan.wangcan.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
public class CustomDebugFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
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
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        StringBuffer body = new StringBuffer();
        //请求参数
        StringJoiner params = new StringJoiner("&");
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams != null) {
            for (Map.Entry l : requestParams.entrySet()) {
                String[] values = (String[]) l.getValue();
                params.add(l.getKey() + "=" + values[0]);
            }
        }
        //处理body
        if("post".equalsIgnoreCase(request.getMethod())){
            try {
                BufferedReader br = request.getReader();
                String str = "";
                while ((str = br.readLine()) != null) {
                    body.append(str);
                }
            } catch (Exception e) {

            }
        }
        log.info("url:" + request.getRequestURI()+",params:"+params.toString()+",method:" + request.getMethod() + ",body:" + body.toString());
        return true;
    }
}

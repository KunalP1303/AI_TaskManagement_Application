package com.kunal.taskmanager.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiVersionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException{

        // 1.
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String uri = httpServletRequest.getRequestURI();
        if(uri.startsWith("/api/v1")) {
            chain.doFilter(request, response);
            return;
        }
        else{
            String version = httpServletRequest.getHeader("X-API-Version");
            if("1".equals(version)){
                String newPath = "api/v1" + uri;
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpServletRequest) {

                    @Override
                    public String getRequestURI() {
                        return newPath;
                    }

                    @Override
                    public String getServletPath(){
                        return newPath;
                    }
                };
                chain.doFilter(wrapper, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}

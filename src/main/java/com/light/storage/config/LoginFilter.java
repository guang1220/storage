package com.light.storage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.light.storage.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//表单验证首先进入UsernamePasswordAuthenticationFilter这个类的attemptAuthentication方法进行认证
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        } else {
            /*
             * 如果是自己验证用户名密码的话，spring security仅仅支持传统的form表单方式（form-data）登录。这是一个比较大的坑点。
             * 现在都流行使用前后端分离，前端发送的是json格式数据。
             * 如果是form表单方式登录，那么是可以正常获取得到用户名密码的。
             * 但是如果是json方式登录，也即登录数据是放在request body中的，这时候，通过request.getParameter方式获取到的都是null。
             * 所以解决方案是，我们可以考虑重写获取用户名和密码的方法，自定义一个过滤器，继承UsernamePasswordAuthenticationFilter类，然后重写其获取用户名密码的方法，
             * 再在spring配置中进行注册该自定义过滤器即可。直接从请求体中获取。
             * */
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String username = loginData.get(getUsernameParameter());
            username = username != null ? username : "";
            username = username.trim();
            String password = loginData.get(getPasswordParameter());
            password = password != null ? password : "";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
}

package com.mceil.order.interceptors;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.auth.utils.JwtUtils;
import com.mceil.common.utils.CookieUtils;
import com.mceil.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class UserInterceptor implements HandlerInterceptor {
    private JwtProperties prop;
    private static final ThreadLocal<UserInfo> tl=new ThreadLocal<>();

    public UserInterceptor(JwtProperties prop) {
        this.prop=prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        //String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzAsInJvbGUiOjIsInVzZXJuYW1lIjoiaW5vdmUiLCJleHAiOjE1NjUyNjEzMzZ9.Zpbr6V1X6t1Eg_EXwRj48J8SICOr8P7YL8OYhDL1ybfTy5mnCVPEVyh2tykLjUwyhUbJstBQLisut1fDV_9nA6z2JSwV6hAH-Aw_NIO9aBzrkZ29NfUUTMxxr6WB9IQ9Wv-U0-v8WtSuI4Sh5Qxj046CWoZQ6MnDevkHoBkOojs";

        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //传递userinfo
//            request.setAttribute("user",userInfo);
            tl.set(user);
            //放行
            return true;
        } catch (Exception e) {
           log.error("[订单服务] 解析用户身份失败.",e);
            return false;
        }
//        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
       //最后用完数据，清空数据
        tl.remove();
    }
    public static UserInfo getUser(){
        return tl.get();
    }
}

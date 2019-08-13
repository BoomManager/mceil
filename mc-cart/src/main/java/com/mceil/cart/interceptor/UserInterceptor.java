package com.mceil.cart.interceptor;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.auth.utils.JwtUtils;
import com.mceil.cart.config.JwtProperties;
import com.mceil.common.utils.CookieUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
        String token = CookieUtil.getCookieValue(request, prop.getCookieName());
        //String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzAsInJvbGUiOjIsInVzZXJuYW1lIjoiaW5vdmUiLCJleHAiOjE1NjQ1ODU3MjB9.kfZy1Lx1RY9NyyQYm-64BVbR41CZXO0u601JK0bokUSPnoHu22GujpLtplOIURdVjfqL4C2stfpatHsM9UlhGeYwIUUu2X-FbnDAiZJP5LStxmNpWPnpRD5V-P1iZhVInvkvsgYPJfb6UbMO_AZ0J3eTo3szu6r2qJ5qel8whiM";
        System.out.println("token数据:"+token);

        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //传递userinfo
//            request.setAttribute("user",userInfo);
            tl.set(user);
            //放行
            return true;
        } catch (Exception e) {
           log.error("[购物车服务] 解析用户身份失败.",e);
            return false;
        }
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

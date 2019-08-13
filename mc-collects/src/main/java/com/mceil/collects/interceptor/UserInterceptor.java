package com.mceil.collects.interceptor;

import com.mceil.auth.pojo.UserInfo;
import com.mceil.auth.utils.JwtUtils;

import com.mceil.collects.config.JwtProperties;
import com.mceil.common.utils.CookieUtil;
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
        String token = CookieUtil.getCookieValue(request, prop.getCookieName());
        //String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzAsInJvbGUiOjIsInVzZXJuYW1lIjoiaW5vdmUiLCJleHAiOjE1NjQ3Mjk4NjZ9.GkmMuKn2IbXGbR9tAkOeZrLItdSNzvrNzWO_Y3NGJlBT8AMnPzUeJqXj7ghd53kMWfZ9TYACAk1x2xLKBkSRMVwJAcqpeYE31o3nbDdIJ56_k3ySt5BLfHgKH-56p1QtHUzFPAQhvsJ6o2FQk30vT2IKIJxWXv3XZIV-xjb0lvY";
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

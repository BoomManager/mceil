package com.mceil.auth.service;

import com.mceil.auth.client.UserClient;
import com.mceil.auth.config.JwtProperties;
import com.mceil.auth.pojo.UserInfo;
import com.mceil.auth.utils.JwtUtils;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;

    public String login(String username, String password) {
        try {
            //校验用户名和密码
            User user = userClient.queryUserByUsernameAndPassword(username, password);
            //判断
            if (user==null){
               return null;
            }
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(),  user.getRole(), username), prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或密码有误，用户名称{}",username,e);
           e.printStackTrace();
           return null;

        }
    }


}

package com.mceil.auth.web;

import com.mceil.auth.config.JwtProperties;
import com.mceil.auth.pojo.UserInfo;
import com.mceil.auth.service.AuthService;
import com.mceil.auth.utils.JwtUtils;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.CookieUtils;

import com.mceil.common.utils.CookieUtils;
import com.mceil.user.pojo.User;
import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Api("授权接口")
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;


    @ApiOperation(value = "登录授权，接收用户和密码,校验，并将生成的token保存到浏览器中的cookie之中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", required = true, value = "账户的用户名"),
            @ApiImplicitParam(name = "password", required = true, value = "账户的密码")
    })
    @ApiResponse(code = 200, message = "授权登录成功，且无返回值")
    @PostMapping("accredit")
    public ResponseEntity<String> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request, HttpServletResponse response) {
        //登录
        String token = authService.login(username, password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //将token写入cookie，并指定httpOnly为true，防止通过js获取和修改
        CookieUtils.setCookie(request, response, prop.getCookieName(), token, prop.getCookieMaxAge(), true);


        return ResponseEntity.ok(token);
    }


    @ApiOperation("校验是否授权过,从本地的cookie中取出token，传送到后台校验")
    @ApiImplicitParam(name = "token", required = true, value = "从cookie中获取的token(加密后的载荷)，后台校验")
    @ApiResponses({
            @ApiResponse(code = 200, message = "校验成功"),
            @ApiResponse(code = 403, message = "未授权")
    })

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("MC_TOKEN") String token,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            //解析token
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //刷新token,重新生成token
            String newtoken = JwtUtils.generateToken(info, prop.getPrivateKey(), prop.getExpire());
            //写入cookie
            CookieUtils.setCookie(request,response,this.prop.getCookieName(),token,this.prop.getCookieMaxAge());
            //已登录，返回用户信息
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //5.出现异常,相应401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
/*    @ApiOperation("获取用户信息")
    @GetMapping("query")
    public ResponseEntity<User> queryUserById(@CookieValue("MC_TOKEN") String token,
                                              HttpServletRequest request, HttpServletResponse response){
        try {
            //解析token
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            User user = authService.queryUserById(info.getId());
            //已登录，返回用户信息
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //5.出现异常,相应401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }*/
}

package com.mceil.auth.utils;

import com.mceil.auth.pojo.UserInfo;

import org.junit.Before;
import org.junit.Test;


import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:\\mall\\keykey\\rsa.pub";

    private static final String priKeyPath = "D:\\mall\\keykey\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        System.out.println(privateKey);
        System.out.println(publicKey);
    }

    @Test
    public void testGenerateToken() throws Exception {
         //生成token
        byte[] privateKey = Files.readAllBytes(new File(priKeyPath).toPath());
        String token = JwtUtils.generateToken(new UserInfo(20L,2L, "林mceil"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoi5p6XU2FiZXIiLCJleHAiOjE1NDQ1Nzg0NDR9.LrqURg6pZ3dDogYi3g_m1lRDOEDnQqMe1H0hhABm2DHX7r3myqvfFRNtBPdsNAQl7HrZWPzvaHtHPt1eDv6UuzHgIGiMrrNHhuNubBp9zkcJwCrPWbR2Ulf2t9RJaMFC0ipoK0actiRlDnapNcsGWw-D9gcKSnJLUs71XwJNTvE";

        //解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}

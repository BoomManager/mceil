package com.mceil.user.api;

import com.mceil.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/query")
    User queryUserByUsernameAndPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    );
    @GetMapping("query/{userId}")
    ResponseEntity<User> perfectUser(@PathVariable("userId") Long userId);
}

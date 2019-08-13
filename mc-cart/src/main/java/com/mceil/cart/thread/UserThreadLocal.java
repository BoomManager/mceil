package com.mceil.cart.thread;

import com.mceil.auth.pojo.UserInfo;

public class UserThreadLocal {
    private static ThreadLocal<UserInfo> thread = new ThreadLocal<>();

    public static void set(UserInfo user) {
        thread.set(user);
    }
    public static UserInfo get() {
        return thread.get();
    }
    public static void remove() {
        thread.remove();
    }
}


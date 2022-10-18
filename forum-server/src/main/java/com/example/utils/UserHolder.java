package com.example.utils;

import com.example.domain.User;

/**
 * @author zhw
 */
public class UserHolder {

    private static final ThreadLocal<User> tl = new ThreadLocal<>();

    private static void saveUser(User user){
        tl.set(user);
    }

    public static User getUser(){
        return tl.get();
    }

    public static void remove(){
        tl.remove();
    }
}

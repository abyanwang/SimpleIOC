package com.fuyu.bean;

import com.fuyu.annotion.MyComponent;

@MyComponent
public class UserDao {

    public void findUser(String userName) {
        System.out.println("UserDao找到了一个用户名字叫:"+userName);
    }
}



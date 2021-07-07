package com.fuyu.bean.test;

import com.fuyu.annotion.MyComponent;

@MyComponent
public class PDao {

    public void findUser(String userName) {
        System.out.println("PDao找到了一个用户名字叫:"+userName);
    }
}
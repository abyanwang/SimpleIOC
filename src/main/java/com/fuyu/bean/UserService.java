package com.fuyu.bean;

import com.fuyu.annotion.MyAutowired;
import com.fuyu.annotion.MyComponent;
import com.fuyu.bean.test.PDao;

@MyComponent
public class UserService {

    @MyAutowired
    private UserDao userDao;

    @MyAutowired
    private PDao pDao;

    public void findUser(String userName) {
        System.out.println("UserService start");
        pDao.findUser(userName);
        userDao.findUser(userName);
    }


}


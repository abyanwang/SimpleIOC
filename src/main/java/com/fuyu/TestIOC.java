package com.fuyu;

import com.fuyu.bean.UserService;
import com.fuyu.exception.NotFountBeanException;

public class TestIOC {

    public static void main(String[] args) throws Exception, NotFountBeanException {
//        MyApplicationContext myApplicationContext=new MyApplicationContext();
//        System.out.println(myApplicationContext.beanMap);
//        UserService userService =(UserService)myApplicationContext.getBean("UserService");
//        userService.findUser("张三");
        MyApplicationContextv1 myApplicationContext = new MyApplicationContextv1();
        UserService userService =(UserService)myApplicationContext.getBean("userService");
        System.out.println(userService);

        userService.findUser("张三");
    }

}



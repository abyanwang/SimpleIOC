package com.fuyu.annotion;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //这个只能够放在字段上面 所以时field
@Documented
public @interface MyAutowired {

    String value() default  "";

}


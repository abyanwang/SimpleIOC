package com.fuyu.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IocUtil {

    /**
     * 根据配置文件名加载配置文件
     *
     * @param fileName
     * @return
     */
    public static Properties getPropertyByName(String fileName) {
        InputStream is = null;
        Properties pro = null;
        try {
            is = IocUtil.class.getClassLoader().getResourceAsStream(fileName);
            pro = new Properties();
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pro;
    }


    /**
     * 首字母转小写
     *
     * @return
     */
    public static String toLowercaseIndex(String name) {
        if (StringUtils.isNotEmpty(name)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name.substring(0, 1).toLowerCase());
            stringBuilder.append(name.substring(1, name.length()));
            return stringBuilder.toString();
        }
        return null;
    }



}

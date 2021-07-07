package com.fuyu.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IocUtilv1 {
    public static Properties readPropertiesByName(String name) throws IOException {
        Properties res = new Properties();
        InputStream in = IocUtilv1.class.getClassLoader().getResourceAsStream(name);
        if (in == null) return res;
        try {
            res.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            in.close();
        }
        return res;
    }

    public static String toLowercaseIndex(String name) {
        if (StringUtils.isNotEmpty(name)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name.substring(0, 1).toLowerCase());
            stringBuilder.append(name.substring(1));
            return stringBuilder.toString();
        }
        return null;
    }
}

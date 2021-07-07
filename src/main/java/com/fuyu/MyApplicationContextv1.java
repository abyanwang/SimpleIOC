package com.fuyu;

import com.fuyu.annotion.MyAutowired;
import com.fuyu.annotion.MyComponent;
import com.fuyu.util.IocUtil;
import com.fuyu.util.IocUtilv1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class MyApplicationContextv1 {

    private Map<String,Object> beanMap = new HashMap<String, Object>();

    public MyApplicationContextv1(){
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        Set<String> classNameSet = getClassNameSet("ioc.bean.scan");

        registerBean(classNameSet);

        System.out.println(beanMap);

        dependenceInjection();
    }

    public Object getBean(String name) {
        return beanMap.get(name);
    }

    //扫描指定的包

    private Set<String> getClassNameSet(String key) throws IOException {
        Set<String> classNameSet = new HashSet<String>();
        Properties properties = null;
        try {
            properties = IocUtilv1.readPropertiesByName("application.properties");
        } catch (IOException e) {
            System.out.println("properties 不存在");
            return classNameSet;
        }
        String path = properties.get(key).toString();
        if (StringUtils.isEmpty(path)) {
            return classNameSet;
        }

        String path1 = path.replace('.','/');

        URL url = this.getClass().getClassLoader().getResource(path1);
        if (url == null) {
            return classNameSet;
        }

        File file = new File(url.getFile());
        Queue<File> q = new LinkedList<File>();
        // 获取子package名
        Queue<String> qs = new LinkedList<String>();
        q.add(file);
        qs.add(path);
        while(!q.isEmpty()) {
            File tmpFile = q.poll();
            String tmpName = qs.poll();
            if (tmpFile.listFiles() != null && tmpFile.listFiles().length != 0) {
                for (File childFile : tmpFile.listFiles()) {
                    if (childFile.isDirectory()) {
                        q.add(childFile);
                        if (!tmpName.contains(childFile.getName())) {
                            qs.add(tmpName+"."+childFile.getName());
                        } else {
                            qs.add(tmpName);
                        }
                    } else {
                        if (childFile.getName().indexOf(".class") > 0) {
                            //保存class类
                            classNameSet.add(tmpName + "." + childFile.getName().replace(".class", ""));
                        }
                    }
                }
            }
        }
        System.out.println(classNameSet);
        return classNameSet;
    }

    private void registerBean(Set<String> classNameSet) throws Exception {

        if (CollectionUtils.isNotEmpty(classNameSet)) {
            for (String className : classNameSet) {
                //实例化对象放入beanMap
                Class clazz = Class.forName(className);

                MyComponent myComponent = (MyComponent) clazz.getAnnotation(MyComponent.class);
                if (myComponent == null) continue;

                //定义bean key名称
                String beanName = (StringUtils.isEmpty(myComponent.value())) ? IocUtil.toLowercaseIndex(clazz.getSimpleName()) : myComponent.value();
                beanMap.put(beanName, clazz.newInstance());

            }

        }


    }

    /**
     * 依赖注入
     */
    private void dependenceInjection() throws Exception {
        if (MapUtils.isEmpty(beanMap)) return;
        for (Object o : beanMap.values()) {
            doInjection(o);
        }
    }


    private void doInjection(Object o) throws Exception {
        //获取类的属性，进行依赖注入
        Field[] fields = o.getClass().getDeclaredFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field file : fields) {
                MyAutowired autowired = file.getAnnotation(MyAutowired.class);
                if (autowired != null) {
                    //得到beanName 验证该Bean是否已经实例化了
                    String beanName = (StringUtils.isEmpty(autowired.value()))? IocUtil.toLowercaseIndex(file.getType().getSimpleName()):autowired.value();
                    //如果bean已经被实例化了,否则创建对象
                    if (!beanMap.containsKey(beanName)) {
                        Class clazz = file.getType();
                        beanMap.put(beanName, clazz.newInstance());
                    }
                    //调用对象set方法注入属性
                    file.setAccessible(true);
                    file.set(o, beanMap.get(beanName));
                    //递归当前实例化的对象的属性注入
                    doInjection(beanMap.get(beanName));
                }

            }
        }

    }
}

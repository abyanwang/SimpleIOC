package com.fuyu;

import com.fuyu.annotion.MyAutowired;
import com.fuyu.annotion.MyComponent;
import com.fuyu.exception.NotFountBeanException;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {


    //存储实例后的Bean容器
    public Map<String, Object> beanMap = new ConcurrentHashMap<String, Object>();
    //需要实例化的对象Class全路径名
    public Set<String> classNameSet = new HashSet<String>();

    public MyApplicationContext() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据Bean名称获取对象实例
     *
     * @param name
     * @return
     */
    public Object getBean(String name) throws NotFountBeanException {
        return beanMap.get(name);
    }


    /**
     * 初始化IOC容器
     * @throws Exception
     */
    public void init() throws Exception {
        //1、定位资源
        String beanScanPath = getBeanScanPath("ioc.bean.scan");
        if (StringUtils.isEmpty(beanScanPath)) {
            throw new RuntimeException("bean error");
        }
        //2、加载需要实例化的Class
        loadBeanClass(beanScanPath);
        //3、实例化bean
        registerBean();
        //4、注入bean的属性
        dependenceInjection();

    }

    /**
     * 定位需要进行实例化JAVA类
     */
    private String getBeanScanPath(String key) {
        Properties properties = null;
        try {
            properties = IocUtilv1.readPropertiesByName("application.properties");
        } catch (IOException e) {
            System.out.println("读取错误");
        }
        if (properties == null || properties.get(key) == null) {
            return null;
        }
        return properties.get(key).toString();
    }

    /**
     * 加载JAVAClass
     */
    private void loadBeanClass(String packageName) {
        String filePath = packageName.replace(".", "/");
        URL url = this.getClass().getClassLoader().getResource(filePath);
        //得到根文件夹
        File rootFile = new File(url.getFile());
        //遍历所有文件夹
        if (rootFile != null) {
            for (File file : rootFile.listFiles()) {
                if (file.isDirectory()) {
                    //文件夹则递归
                    loadBeanClass(packageName + "." + file.getName());
                } else {
                    if (file.getName().indexOf(".class") > 0) {
                        //保存class类
                        classNameSet.add(packageName + "." + file.getName().replace(".class", ""));
                    }

                }

            }

        }

    }

    /**
     * 注册实例化Bean
     */
    private void registerBean() throws Exception {

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

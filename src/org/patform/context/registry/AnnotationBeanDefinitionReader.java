package org.patform.context.registry;

import org.patform.bean.RootBeanDefinition;
import org.patform.context.annotation.Component;
import org.patform.util.CusStringUtil;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author leber
 * @date 2019-12-01
 * 注解加载
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private String locationPaths[];

    public AnnotationBeanDefinitionReader() {
        super(null);
    }

    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        super(beanFactory);
    }

    public void setLocationPaths(String[] locationPaths) {
        this.locationPaths = locationPaths;
    }

    @Override
    public void loadBeanDefinition() {
        Objects.requireNonNull(locationPaths, "AnnotationBeanDefinitionReader 未指定加载路径.");
        for (String path : locationPaths) {
            if (path.startsWith("classpath:")) {
                path = path.replace("classpath:", "");
            }
            resolvePathBean(path);
        }
    }


    private void resolvePathBean(String path) {
        String classpath = getBeanClassLoader().getResource(".").getFile();
        String filePath = path.replaceAll("\\.", "/");
        File dir = new File(classpath + filePath);
        File[] files = dir.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        //加载包中的类
        for (File file : files) {
            if (file.isDirectory()) {
                String packagePath = file.getName().replaceAll("/", "\\.");
                resolvePathBean(packagePath);
            }
            //文件
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            //java类
            loadClass(path + "." + file.getName().substring(0, file.getName().lastIndexOf(".")));
        }


    }

    /**
     * 加载类 并注册到容器
     *
     * @param className
     */
    private void loadClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                if (annotation.annotationType() != Component.class) {
                    continue;
                }
                Component ano = (Component) annotation;
                String beanName = ano.value();
                String alias = "";
                if (beanName.trim().length() == 0) {
                    beanName = className;
                    String simpleName = className.substring(className.lastIndexOf(".") + 1);
                    alias = CusStringUtil.lowerFirstChar(simpleName);
                    getBeanFactory().registerAlias(beanName, alias);
                }
                RootBeanDefinition definition = new RootBeanDefinition(clazz, null);
                getBeanFactory().registerBeanDefinition(beanName, definition);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    /**
     * 实现注解的派生性语义
     */
    public Annotation[] getAnnotations(Class<?> clazz) {
        Objects.requireNonNull(clazz);


        return Collections.emptyList().toArray(new Annotation[0]);
    }

    public Annotation[] getAnnotations(Annotation srcAnnotation,Class<?> targetAnnotation) {
        Objects.requireNonNull(srcAnnotation);
        Objects.requireNonNull(targetAnnotation);
        Annotation[] ans = srcAnnotation.getClass().getAnnotations();
        for (Annotation an : ans) {

        }


        return Collections.emptyList().toArray(new Annotation[0]);
    }
}

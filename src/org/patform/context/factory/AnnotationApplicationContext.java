package org.patform.context.factory;

import org.patform.bean.RootBeanDefinition;
import org.patform.context.registry.AnnotationBeanDefinitionReader;
import org.patform.context.resource.StaticMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author leber
 * date 2019-12-01
 * 基于注解实现IOC
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {

    private static Logger logger = LoggerFactory.getLogger(AnnotationApplicationContext.class);
    private DefaultListableBeanFactory beanFactory;
    private String locations[];

    public AnnotationApplicationContext(String... path) {
        this(null, path);
    }

    public AnnotationApplicationContext(ApplicationContext parent, String... path) {
        super(parent);
        locations = path;
        this.beanFactory = new DefaultListableBeanFactory(parent);
        this.beanFactory.registerBeanDefinition("annotationBeanReader", new RootBeanDefinition(AnnotationBeanDefinitionReader.class, RootBeanDefinition.AUTOWIRE_AUTODETECT));
        refresh();
    }

    @Override
    protected void refreshBeanFactory() {
        //
        locations = Optional.ofNullable(locations).filter(obj -> obj.length > 0).orElse(new String[]{deduceMainApplicationClass().getPackage().getName()});
        //加载location下所有的类，查看是否有Component注解
        AnnotationBeanDefinitionReader definitionReader = new AnnotationBeanDefinitionReader(beanFactory);
        definitionReader.setBeanClassLoader(this.getClass().getClassLoader());
        definitionReader.setLocationPaths(locations);
        definitionReader.loadBeanDefinition();
        beanFactory.registerBeanDefinition(MESSAGE_SOURCE, new RootBeanDefinition(StaticMessageSource.class, null));
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = (new RuntimeException()).getStackTrace();
            StackTraceElement[] var2 = stackTrace;
            int var3 = stackTrace.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                StackTraceElement stackTraceElement = var2[var4];
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException var6) {
            logger.error("未找到启动类.");
        }
        throw new RuntimeException();
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public String[] getLocations() {
        return locations;
    }
}

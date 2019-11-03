package org.patform.context.factory;

import org.patform.context.event.ApplicationEventMulticaster;
import org.patform.context.event.ApplicationEventMulticasterImpl;
import org.patform.context.factory.exception.ApplicationContextException;
import org.patform.context.resource.*;
import org.patform.context.resource.exception.NoSuchMessageException;
import org.patform.context.util.ApplicationContextAwareProcessor;
import org.patform.context.util.BeanPostProcessor;
import org.patform.context.util.OrderedComparator;
import org.patform.exception.BeansException;
import org.patform.exception.NoSuchBeanDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 容器上下文的模板实现
 *
 * @author leber
 * date 2019-11-03
 * ApplicationContext 容器
 */
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {


    private static final String MESSAGE_SOURCE = "messageSource";

    private static Logger logger = LoggerFactory.getLogger(AbstractApplicationContext.class);
    //父容器引用
    ApplicationContext parent;
    private List beanFactoryPostProcessors = new ArrayList();
    private String displayName = getClass().getName() + ";hashcode=" + hashCode();
    //启动时间
    private long startupTime;
    //messageSource helper
    private MessageSource messageSource;

    //事件传播器(本身也实现了事件监听)
    private ApplicationEventMulticaster eventMulticaster = new ApplicationEventMulticasterImpl();


    @Override
    public void setParent(ApplicationContext parent) {
        this.parent = parent;
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public void refresh() throws BeansException {

        this.startupTime = System.currentTimeMillis();
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        //设置bean处理器
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyType(ResourceLoader.class);
        beanFactory.ignoreDependencyType(ApplicationContext.class);
        postProcessBeanFactory(beanFactory);

        //调用直接附加到Context容器上下文的后置处理
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : getBeanFactoryPostProcessors()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }

        //bean 数量
        if (getBeanDefinitionCount() == 0) {
            logger.warn("No beans defined in ApplicationContext [" + getDisplayName() + "]");
        } else {
            logger.info(getBeanDefinitionCount() + "beans defined in ApplicationContext [" + getDisplayName() + "]");
        }

        //调用注册到容器中的BeanFactoryProcessor 注意与上面的区别（来源）
        invokeBeanFactoryProcessors();

        //注册BeanPostProcessor
        registerBeanPostProcessor();

        //
    }

    /**
     * 将注册在容器中的BeanPostProcessor从容器中取出添加到附加集合中方便使用，避免每次都从容器这种获取
     */
    private void registerBeanPostProcessor() {
        //从容器中获取实例（单例）
        String[] beanDefinitionNames = getBeanDefinitionNames(BeanPostProcessor.class);
        BeanPostProcessor[] beanPostProcessors = new BeanPostProcessor[beanDefinitionNames.length];
        for (int i = 0; i < beanPostProcessors.length; i++) {
            beanPostProcessors[i] = getBean(beanDefinitionNames[i], BeanPostProcessor.class);
        }
        Arrays.sort(beanPostProcessors, new OrderedComparator<Object>());

        //添加到容器附加集合
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            getBeanFactory().addBeanPostProcessor(beanPostProcessor);
        }
    }

    /**
     * 调用注册到容器中的BeanFactoryProcessor[容器初始化后置处理器]
     */
    protected void invokeBeanFactoryProcessors() {

        String[] beanDefinitionNames = getBeanDefinitionNames(BeanFactoryPostProcessor.class);
        BeanFactoryPostProcessor[] processors = new BeanFactoryPostProcessor[beanDefinitionNames.length];
        //获取实例
        for (int i = 0; i < processors.length; i++) {
            BeanFactoryPostProcessor bean = getBean(beanDefinitionNames[i], BeanFactoryPostProcessor.class);
            processors[i] = bean;
        }
        //排序
        Arrays.sort(processors, new OrderedComparator<Object>());
        //调用
        for (BeanFactoryPostProcessor processor : processors) {
            processor.postProcessBeanFactory(getBeanFactory());
        }
    }


    protected List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }


    protected abstract void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);

    protected abstract void refreshBeanFactory();


    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return null;
    }

    @Override
    public void close() throws ApplicationContextException {

    }

    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        eventMulticaster.onApplicationEvent(event);
        if (Objects.nonNull(parent)) {
            parent.publishEvent(event);
        }
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, int dependencyCheck) {
        return null;
    }

    @Override
    public void autowireBeanProperties(Object existBean, int autowireMode, int dependencyCheck) {

    }

    @Override
    public Object applyPostProcessorBeforeInitialization(Object existBean, String name) {
        return null;
    }

    @Override
    public Object applyPostProcessorAfterInitialization(Object existBean, String name) {
        return null;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {
        return new String[0];
    }

    @Override
    public Map<String, Object> getBeansOfType(Class<?> requireType, boolean includeProtoType, boolean includeFactoryBeans) {
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) {

        return null;
    }

    @Override
    public boolean contains(String beanName) {
        return false;
    }

    @Override
    public boolean isSingleton(String beanName) {
        return false;
    }

    @Override
    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return new String[0];
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }
}

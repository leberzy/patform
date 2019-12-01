package org.patform.context.factory;

import org.patform.context.event.*;
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
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {


    public static final String MESSAGE_SOURCE = "messageSource";

    private static Logger logger = LoggerFactory.getLogger(AbstractApplicationContext.class);
    //父容器引用
    ApplicationContext parent;
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private String displayName = getClass().getName() + ";hashcode=" + hashCode();
    //启动时间
    private long startupTime;
    //messageSource helper
    private MessageSource messageSource;

    //事件传播器(本身也实现了事件监听)
    private ApplicationEventMulticaster eventMulticaster = new ApplicationEventMulticasterImpl();

    public AbstractApplicationContext(ApplicationContext parent) {
        this.parent = parent;
    }


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
        //1.初始化具体使用的容器
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        //2.设置bean处理器等
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyType(ResourceLoader.class);
        beanFactory.ignoreDependencyType(ApplicationContext.class);

        //3.在容器上下文的内部容器已经初始化，资源已经被加载，但尚未实例化时调用，由子类实现，可加载一些子类特殊的BeanPostProcessor
        postProcessBeanFactory(beanFactory);

        //4.调用直接附加到Context容器上下文的后置处理
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : getBeanFactoryPostProcessors()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }

        //bean 数量
        if (getBeanDefinitionCount() == 0) {
            logger.warn("No beans defined in ApplicationContext [" + getDisplayName() + "]");
        } else {
            logger.info(getBeanDefinitionCount() + "beans defined in ApplicationContext [" + getDisplayName() + "]");
        }

        //5.调用注册到容器中的BeanFactoryProcessor 注意与上面的区别（来源）
        invokeBeanFactoryProcessors();

        //6.注册BeanPostProcessor
        registerBeanPostProcessor();

        //7.注册消息本地化处理器
        initMessageSource();

        //8.交给子类自定义的refresh方法
        onRefresh();

        //9.初始化上下文监听器
        refreshListeners();

        //10.实例化非延迟加载的单例
        beanFactory.preInstantiateSingletons();

        //11.发布容器已经刷新的事件通知各方
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 初始化ApplicationListener
     */
    protected void refreshListeners() {
        //从容器中获取所有的键值对，包括原型模式的bean
        Map<String, ApplicationListener> listenersMap = getBeansOfType(ApplicationListener.class, true, false);
        for (ApplicationListener listener : listenersMap.values()) {
            this.eventMulticaster.addApplicationListener(listener);
            logger.debug("application listener [" + listener + "] added.");
        }
    }


    /**
     * 具体容器实现中还需要处理的事情
     */
    protected void onRefresh() {
    }


    /**
     * 初始化消息本地化工具，当前容器中没有时从父容器中获取
     */
    protected void initMessageSource() throws BeansException {
        //首先从容器中获取消息处理器
        this.messageSource = getBean(MESSAGE_SOURCE, MessageSource.class);
        try {
            if (Objects.nonNull(parent) && (this.messageSource instanceof HierarchicalMessageSource) && (Arrays.asList(getBeanDefinitionNames()).contains(MESSAGE_SOURCE))) {
                ((HierarchicalMessageSource) this.messageSource).setParentMessageSource(this.parent);
            }
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug("容器中尚未自定义消息处理器，将使用默认消息处理器" + StaticMessageSource.class.getName());
            this.messageSource = new StaticMessageSource();
        }
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


    //----------------------------------
    @Override
    public void close() throws ApplicationContextException {
        logger.info("close application context [" + getDisplayName() + "]");
        getBeanFactory().destroySingletons();
        publishEvent(new ContextClosedEvent(this));
    }


    @Override
    public void publishEvent(ApplicationEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("发布容器刷新事件...");
        }
        eventMulticaster.onApplicationEvent(event);
        //同时发布父容器相应事件
        if (Objects.nonNull(parent)) {
            parent.publishEvent(event);
        }
    }


    //-------------------------需要子类实现的方法--------------------------------------

    /**
     * @param beanFactory
     */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    /**
     * 子类具体的容器初始化
     */
    protected abstract void refreshBeanFactory();

    /**
     * 具体的容器
     *
     * @return
     */
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory();


    //---------------------------------------------------------------------------------------------------------
    @Override
    public ApplicationContext getParent() {
        return parent;
    }

    @Override
    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }


    protected List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, int dependencyCheck) {
        return getBeanFactory().autowire(beanClass, autowireMode, dependencyCheck);
    }

    @Override
    public void autowireBeanProperties(Object existBean, int autowireMode, int dependencyCheck) {
        getBeanFactory().autowireBeanProperties(existBean, autowireMode, dependencyCheck);
    }

    @Override
    public Object applyPostProcessorBeforeInitialization(Object existBean, String name) {
        return getBeanFactory().applyPostProcessorBeforeInitialization(existBean, name);
    }

    @Override
    public Object applyPostProcessorAfterInitialization(Object existBean, String name) {
        return getBeanFactory().applyPostProcessorAfterInitialization(existBean, name);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return getBeanFactory().getParentBeanFactory();
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {
        return getBeanFactory().getBeanDefinitionNames(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requireType, boolean includeProtoType, boolean includeFactoryBeans) {
        return getBeanFactory().getBeansOfType(requireType, includeProtoType, includeFactoryBeans);
    }

    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) {
        return getBeanFactory().getBean(beanName, requireType);
    }

    @Override
    public boolean contains(String beanName) {
        return getBeanFactory().contains(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return getBeanFactory().isSingleton(beanName);
    }

    @Override
    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return this.messageSource.getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(": ");
        sb.append("displayName=[").append(this.displayName).append("]; ");
        sb.append("startup date=[").append(new Date(this.startupTime)).append("]; ");
        if (this.parent == null) {
            sb.append("root of ApplicationContext hierarchy");
        } else {
            sb.append("parent=[").append(this.parent).append(']');
        }
        return sb.toString();
    }
}

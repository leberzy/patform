package org.patform.bean;

import org.patform.context.factory.BeanFactory;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class RootBeanDefinition extends AbstractBeanDefinition {
    //注入方式
    //不注入
    public static final int AUTOWIRE_NO = 0;
    //根据名称注入
    public static final int AUTOWIRE_BY_NAME = 1;
    //根据类型注入
    public static final int AUTOWIRE_BY_TYPE = 2;
    //根据构造方法注入
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    //自动推断
    public static final int AUTOWIRE_AUTODETECT = 4;

    //--------------------dependency check-------------------------
    //不进行依赖检查
    public static final int DEPENDENCY_CHECK_NO = 0;
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;//
    public static final int DEPENDENCY_CHECK_SIMPLE = 2;//
    public static final int DEPENDENCY_CHECK_ALL = 3;

    //数据
    private Class<?> beanClass;//类对象或类全路径名
    private ConstructorArgumentValues constructorArgumentValues;
    private int autowireMode = AUTOWIRE_NO;
    private int dependencyCheck = DEPENDENCY_CHECK_NO;
    private String[] dependsOn;
    private String initMethod;
    private String destroyMethod;

    public RootBeanDefinition(Class<?> beanClass, int autowireMode) {
        super(null);
        this.beanClass = beanClass;
        this.autowireMode = autowireMode;
    }

    public RootBeanDefinition(RootBeanDefinition other) {
        super(other.getPropertyValues());
        this.beanClass = other.getBeanClass();
        this.autowireMode = other.getAutowireMode();
        this.dependencyCheck = other.getDependencyCheck();
        this.setSingleton(other.isSingleton());
        this.setLazyInit(other.isLazyInit());
        this.constructorArgumentValues = other.getConstructorArgumentValues();
        this.dependsOn = other.getDependsOn();
        this.initMethod = other.getInitMethod();
        this.destroyMethod = other.getDestroyMethod();
    }

    public RootBeanDefinition(MutablePropertyValues propertyValues) {
        super(propertyValues);
    }

    @Override
    public ConstructorArgumentValues getConstructorValues() {
        return null;
    }

    /**
     * 推断使用何种注入方式
     *
     * @return autoWired mode
     */
    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            Class<?> beanClass = getBeanClass();
            Constructor<?>[] cons = beanClass.getConstructors();
            for (Constructor<?> con : cons) {
                if (con.getParameterCount() == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        } else {
            return this.autowireMode;
        }
    }


    @Override
    public void validate() {
        super.validate();
        if (Objects.isNull(beanClass)) {
            throw new IllegalStateException("beanClass not be null.");
        }

        if (BeanFactory.class.isAssignableFrom(beanClass) && !isSingleton()) {
            throw new IllegalStateException("BeanFactory bean should be singleton.");
        }

        if (beanClass.getConstructors().length == 0) {
            throw new IllegalStateException("the beanClass not have public constructor.");
        }
    }




    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanClassName() {
        return beanClass.getName();
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public int getAutowireMode() {
        return autowireMode;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getDependencyCheck() {
        return dependencyCheck;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    @Override
    public String toString() {
        return "Root bean with class [" + getBeanClassName() + "] defined in " + getResourceDescription();
    }
}

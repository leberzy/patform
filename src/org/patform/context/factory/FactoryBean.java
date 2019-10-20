package org.patform.context.factory;

public interface FactoryBean {

    /**
     * Return an instance (possibly shared or independent) of the object
     * managed by this factory. As with a BeanFactory, this allows
     * support for both the Singleton and Prototype design pattern.
     *
     * @return an instance of the bean (should never be null)
     * @throws Exception in case of creation errors
     */
    Object getObject();

    /**
     * Return the type of object that this FactoryBean creates, or null
     * if not known in advance. This allows to check for specific types of
     * beans without instantiating objects, e.g. on autowiring.
     * <p>For a singleton, this can simply return getObject().getClass(),
     * or even null, as autowiring will always check the actual objects
     * for singletons. For prototypes, returning a meaningful type here
     * is highly advisable, as autowiring will simply ignore them else.
     *
     * @return the type of object that this FactoryBean creates, or null
     */
    Class getObjectType();

    /**
     * Is the bean managed by this factory a singleton or a prototype?
     * That is, will getObject() always return the same object?
     * <p>The singleton status of the FactoryBean itself will
     * generally be provided by the owning BeanFactory.
     *
     * @return if this bean is a singleton
     */
    boolean isSingleton();

}
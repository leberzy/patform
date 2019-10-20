package org.patform.context.util;

/**
 * @author leber
 * date 2019/10/19
 */
public interface BeanPostProcessor {

    void postProcessorBeforeInitialization(Object existBean, String name);

    void postProcessorAfterInitialization(Object existBean, String name);


}

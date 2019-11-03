package org.patform.context.util;

/**
 * @author leber
 * date 2019/10/19
 */
public interface BeanPostProcessor {

    Object postProcessorBeforeInitialization(Object existBean, String name);

    Object postProcessorAfterInitialization(Object existBean, String name);


}

package org.patform.context.resource;

/**
 * @author leber
 * date 2019-11-03
 */
public interface HierarchicalMessageSource extends MessageSource {


    void setParentMessageSource(MessageSource messageSource);

    MessageSource getParentMessageSource();


}

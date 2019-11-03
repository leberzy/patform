package org.patform.context.util;

import org.patform.context.factory.ApplicationContext;

/**
 * 设置上下文
 * @author leber
 * date 2019-11-03
 */
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext context);

}

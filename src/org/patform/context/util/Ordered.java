package org.patform.context.util;

/**
 * 排序工具标记
 *
 * @author leber
 * date 2019-11-03
 */
public interface Ordered {

    /**
     * 获取序号 越大优先级越低
     *
     * @return
     */
    int getOrder();

}

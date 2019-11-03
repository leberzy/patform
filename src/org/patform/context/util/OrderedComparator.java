package org.patform.context.util;

import java.util.Comparator;

/**
 * 排序比较
 *
 * @author leber
 * date 2019-11-03
 */
public class OrderedComparator<Object> implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        int i1 = o1 instanceof Ordered ? ((Ordered) o1).getOrder() : Integer.MAX_VALUE;
        int i2 = o1 instanceof Ordered ? ((Ordered) o2).getOrder() : Integer.MAX_VALUE;
        return Integer.compare(i1, i2);
    }
}

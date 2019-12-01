package org.patform.test;

import org.junit.Test;
import org.patform.bean.MutablePropertyValues;
import org.patform.bean.PropertyValue;
import org.patform.bean.RootBeanDefinition;
import org.patform.context.factory.DefaultListableBeanFactory;
import org.patform.test.bean.Bag;
import org.patform.test.bean.Cat;
import org.patform.test.bean.Student;
import org.patform.test.bean.User;


public class DefaultListableBeanFactoryTest {

    @Test
    public void test() {

        DefaultListableBeanFactory factory = new DefaultListableBeanFactory(null);
        factory.registerBeanDefinition("user", new RootBeanDefinition(User.class, RootBeanDefinition.AUTOWIRE_BY_TYPE, RootBeanDefinition.DEPENDENCY_CHECK_NO));
        factory.registerBeanDefinition("cat", new RootBeanDefinition(Cat.class, RootBeanDefinition.AUTOWIRE_BY_TYPE, RootBeanDefinition.AUTOWIRE_NO));

        Object user = factory.getBean("user");
        System.out.println(user);
    }

    @Test
    public void test001() {

        DefaultListableBeanFactory factory = new DefaultListableBeanFactory(null);
        RootBeanDefinition bag = new RootBeanDefinition(Bag.class, RootBeanDefinition.AUTOWIRE_BY_TYPE);
        MutablePropertyValues mpvs = new MutablePropertyValues();
        mpvs.addPropertyValue(new PropertyValue("id",1));
        mpvs.addPropertyValue(new PropertyValue("name","中国牌"));
        mpvs.addPropertyValue(new PropertyValue("price",22.22));
        bag.setPropertyValues(mpvs);
        factory.registerBeanDefinition("bag", bag);

        Object bag1 = factory.getBean("bag");
        System.out.println(bag1);

        RootBeanDefinition student = new RootBeanDefinition(Student.class, RootBeanDefinition.AUTOWIRE_BY_TYPE, RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS);
        MutablePropertyValues v = new MutablePropertyValues();
        v.addPropertyValue(new PropertyValue("id",2));
        v.addPropertyValue(new PropertyValue("name","sir"));
        student.setPropertyValues(v);
        factory.registerBeanDefinition("student", student);
        Object s = factory.getBean("student");
        System.out.println(s);

    }

}

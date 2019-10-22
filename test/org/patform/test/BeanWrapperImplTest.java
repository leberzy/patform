package org.patform.test;

import org.junit.Test;
import org.patform.bean.wrapper.BeanWrapperImpl;
import org.patform.test.bean.Cat;
import org.patform.test.bean.User;

import java.lang.reflect.InvocationTargetException;

public class BeanWrapperImplTest {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        BeanWrapperImpl user = new BeanWrapperImpl(User.class);
        Cat cat = new Cat();
        user.setPropertyValue("cat",cat);

        user.setPropertyValue("cat.name","jack");
        System.out.println(user);
    }


}

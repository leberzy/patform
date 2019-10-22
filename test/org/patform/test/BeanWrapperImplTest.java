package org.patform.test;

import org.junit.Test;
import org.patform.bean.wrapper.BeanWrapperImpl;
import org.patform.test.bean.Cat;
import org.patform.test.bean.User;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class BeanWrapperImplTest {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        BeanWrapperImpl user = new BeanWrapperImpl(User.class);
        Cat cat = new Cat();
        user.setPropertyValue("cat",cat);
        user.setPropertyValue("list",new ArrayList<>());
        user.setPropertyValue("list[0]","马老师");
        user.setPropertyValue("list[1]","马老师2");
        user.setPropertyValue("cat.catName","jack");
        user.setPropertyValue("cat.list", new ArrayList<>());
        user.setPropertyValue("cat.list[0]",1);
        user.setPropertyValue("cat.id",123);
        user.setPropertyValue("id", 1);
        user.setPropertyValue("username","sir");


        user.setPropertyValue("map", new HashMap<>());
        user.setPropertyValue("map[jack]", cat);
        System.out.println(user.getInstance());
    }

    public static void amain(String[] args) {



        boolean matches = "list[1]".matches("^\\w+\\[\\w+\\]");
        System.out.println(matches);
        String $1 = "list[1]".replaceAll("^(\\w+)\\[\\w+\\]", "$1");
        System.out.println($1);
    }
}

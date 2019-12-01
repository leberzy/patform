package org.patform.util;

import org.junit.Assert;

import java.util.Objects;

/**
 * @author leber
 * @date 2019-12-01
 */
public class CusStringUtil {

    public static String lowerFirstChar(String name) {
        Objects.requireNonNull(name);
        char c = Character.toLowerCase(name.charAt(0));
        //Unnecessary 'Character.toString()' call
        return c + name.substring(1);

    }

}

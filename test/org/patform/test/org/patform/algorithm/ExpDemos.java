package org.patform.test.org.patform.algorithm;

import org.junit.Test;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中缀 后缀 前缀 波兰 逆波兰等表达式
 */
public class ExpDemos {

    @Test
    public void testToPostfix() {

        System.out.println(getNextNum("12+14"));


    }

    public void toPostfix(String exp) {

        Stack<Integer> stack = new Stack<>();

        Integer nextNum = getNextNum(exp);

        while (nextNum != null) {


            exp.substring(nextNum.toString().length());
            nextNum = getNextNum(exp);

        }


    }

    Integer getNextNum(String exp) {
        Pattern pattern = Pattern.compile("^(\\d+)");
        Matcher matcher = pattern.matcher(exp);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return null;
    }
}

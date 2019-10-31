package org.patform.context.resource;

/**
 * @author leber
 * date 2019/10/31
 */
public interface MessageSourceResolvable {

    String[] getCodes();

    Object[] getArguments();

    String getDefaultMessage();

}
package org.patform.context.resource;

/**
 * @author leber
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource();
}

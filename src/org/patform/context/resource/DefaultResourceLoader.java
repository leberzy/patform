package org.patform.context.resource;

/**
 * @author leber
 * @date 2019-12-01
 */
public class DefaultResourceLoader implements ResourceLoader {


    @Override
    public Resource getResource(String location) {

        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location,null);
        }else {

        }

        return null;
    }
}

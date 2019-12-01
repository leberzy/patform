package org.patform.context.resource;

import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * @author leber
 * @date 2019-12-01
 * 加载类路径下的资源
 */
public class ClassPathResource extends AbstractResource {

    private String path;
    private Class<?> clazz;

    public ClassPathResource(String path, Class<?> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    @Override
    public String getDescription() {
        return String.format("class path loader path=%s", path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        Assert.assertNotNull("load path not be null.", path);
        if (Objects.isNull(clazz)) {
            return ClassPathResource.class.getClassLoader().getResourceAsStream(path);
        } else {
            return clazz.getResourceAsStream(path);
        }
    }

    @Override
    public URL getURL() throws IOException {
        if (Objects.nonNull(clazz)) {
            return clazz.getResource(path);
        } else {
            return Thread.currentThread().getContextClassLoader().getResource(path);
        }
    }

    @Override
    public File getFile() throws IOException {
        if (path.startsWith("file://")) {
            return new File(URLDecoder.decode(getURL().getFile()));
        } else {
            throw new FileNotFoundException("文件路径不正确.");
        }

    }
}

package org.patform.context.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author leber
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    String getDescription();

}
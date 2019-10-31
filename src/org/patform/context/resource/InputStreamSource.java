package org.patform.context.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author leber
 */
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;

}

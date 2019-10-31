package org.patform.context.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractResource implements Resource {

	protected static final String URL_PROTOCOL_FILE = "file";

	/**
	 * This implementation checks whether a File can be opened,
	 * falling back to whether an InputStream can be opened.
	 * This will cover both directories and content resources.
	 */
	public boolean exists() {
		// try file existence
		try {
			return getFile().exists();
		}
		catch (IOException ex) {
			// fall back to stream existence
			try {
				InputStream is = getInputStream();
				is.close();
				return true;
			}
			catch (IOException ex2) {
				return false;
			}
		}
	}

	/**
	 * This implementations always returns false.
	 */
	public boolean isOpen() {
		return false;
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming
	 * that the resource cannot be resolved to a URL.
	 */
	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming
	 * that the resource cannot be resolved to an absolute file path.
	 */
	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	/**
	 * This implementation returns the description of this resource.
	 * @see #getDescription
	 */
	public String toString() {
		return getDescription();
	}

	/**
	 * This implementation compares description strings.
	 * @see #getDescription
	 */
	public boolean equals(Object obj) {
		return (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription()));
	}

	/**
	 * This implementation returns the description's hash code.
	 * @see #getDescription
	 */
	public int hashCode() {
		return getDescription().hashCode();
	}

}
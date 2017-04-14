/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 */
public final class ResourceLoader {

    public static final int TIMEOUT;

    static {
        int timeoutProperty = 5000;
        try {
            timeoutProperty = Integer.parseInt(System.getProperty("net.sourceforge.pmd.http.timeout", "5000"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        TIMEOUT = timeoutProperty;
    }

    // Only static methods, so we shouldn't allow an instance to be created
    /**
     * Constructor for ResourceLoader.
     */
    private ResourceLoader() {
    }

    /**
     * Method to find a file, first by finding it as a file (either by the
     * absolute or relative path), then as a URL, and then finally seeing if it
     * is on the classpath.
     * <p>
     * Caller is responsible for closing the {@link InputStream}.
     *
     * @param name
     *            String
     * @return InputStream
     * @throws RuleSetNotFoundException
     */
    public static InputStream loadResourceAsStream(String name) throws RuleSetNotFoundException {
        InputStream stream = loadResourceAsStream(name, ResourceLoader.class.getClassLoader());
        if (stream == null) {
            throw new RuleSetNotFoundException("Can't find resource " + name
                    + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
        }
        return stream;
    }

    /**
     * Uses the ClassLoader passed in to attempt to load the resource if it's
     * not a File or a URL
     * <p>
     * Caller is responsible for closing the {@link InputStream}.
     *
     * @param name
     *            String
     * @param loader
     *            ClassLoader
     * @return InputStream
     * @throws RuleSetNotFoundException
     */
    public static InputStream loadResourceAsStream(String name, ClassLoader loader) throws RuleSetNotFoundException {
        File file = new File(name);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // if the file didn't exist, we wouldn't be here
            }
        } else {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(name).openConnection();
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(TIMEOUT);
                return connection.getInputStream();
            } catch (Exception e) {
                try {
                    /*
                     * Don't use getResourceAsStream to void reusing connections between threads
                     * See https://github.com/pmd/pmd/issues/234
                     */
                    URL resource = loader.getResource(name);
                    if (resource == null) {
                        // Don't throw RuleSetNotFoundException, keep API compatibility
                        return null;
                    } else {
                        final URLConnection connection = resource.openConnection();
                        final InputStream inputStream = connection.getInputStream();
                        if (connection instanceof JarURLConnection) {
                            // Wrap the InputStream to also close the underlying JarFile if from a JarURLConnection.
                            // See https://github.com/pmd/pmd/issues/337
                            return new InputStream() {
                                @Override
                                public int read() throws IOException {
                                    return inputStream.read();
                                }

                                @Override
                                public void close() throws IOException {
                                    inputStream.close();
                                    if (connection instanceof JarURLConnection) {
                                        ((JarURLConnection) connection).getJarFile().close();
                                    }
                                }
                            };
                        } else {
                            return inputStream;
                        }
                    }
                } catch (IOException e1) {
                    // Ignored
                }
            }
        }
        throw new RuleSetNotFoundException("Can't find resource " + name
                + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
    }
}

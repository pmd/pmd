/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a source file to be analyzed.
 * Different implementations can get the source file
 * from different places: the filesystem, a zip or jar file, etc.
 */
public interface DataSource {
    /**
     * Get an InputStream on the source file.
     *
     * @return the InputStream reading the source file
     * @throws IOException if the file can't be opened
     */
    InputStream getInputStream() throws IOException;

    /**
     * Return a nice version of the filename.
     *
     * @param shortNames    true if short names are being used
     * @param inputFileName name of a "master" file this file is relative to
     * @return String
     */
    String getNiceFileName(boolean shortNames, String inputFileName);
}

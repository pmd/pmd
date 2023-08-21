/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import net.sourceforge.pmd.lang.document.TextFile;

/**
 * Represents a source file to be analyzed. Different implementations can get
 * the source file from different places: the filesystem, a zip or jar file,
 * etc.
 *
 * @deprecated Use {@link TextFile}
 */
@Deprecated
public interface DataSource extends Closeable {

    /**
     * Get an InputStream on the source file.
     *
     * @return the InputStream reading the source file
     * @throws IOException
     *             if the file can't be opened
     */
    InputStream getInputStream() throws IOException;

    /**
     * Return a nice version of the filename.
     *
     * @param shortNames
     *            true if short names are being used
     * @param inputFileName
     *            name of a "master" file this file is relative to
     * @return String
     */
    String getNiceFileName(boolean shortNames, String inputFileName);


    static DataSource forString(String sourceText, String fileName) {
        return new ReaderDataSource(new StringReader(sourceText), fileName);
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

/**
 * Represents a source file to be analyzed. Different implementations can get
 * the source file from different places: the filesystem, a zip or jar file,
 * etc.
 */
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

    /**
     * Reads the contents of the data source to a string. Skips the byte-order
     * mark if present. Parsers expect input without a BOM.
     *
     * @param dataSource     Data source
     * @param sourceEncoding Encoding to use to read from the data source
     */
    static String readToString(DataSource dataSource, Charset sourceEncoding) throws IOException {
        String fullSource;
        try (InputStream stream = dataSource.getInputStream();
             // Skips the byte-order mark
             BOMInputStream bomIs = new BOMInputStream(stream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE);
             Reader reader = new InputStreamReader(bomIs, sourceEncoding)) {

            fullSource = IOUtils.toString(reader); // this already buffers properly
        }
        return fullSource;
    }
}

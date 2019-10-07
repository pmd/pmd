/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.input.ReaderInputStream;

import net.sourceforge.pmd.util.datasource.internal.AbstractDataSource;

/**
 * DataSource implementation to read data from a Reader.
 */
public class ReaderDataSource extends AbstractDataSource {
    /**
     * Reader
     */
    private final Reader reader;

    /**
     * Real or pseudo filename or path name.
     *
     * <p>
     * Including a file suffix mapped to the correct language helps assign the
     * correct parser.
     * </p>
     */
    private String dataSourceName;

    /**
     * Create the DataSource from the Reader.
     */
    public ReaderDataSource(Reader reader, String dataSourceName) {
        this.reader = reader;
        this.dataSourceName = dataSourceName;
    }

    /**
     * Convert the Reader into an InputStream.
     * <p>
     * <strong>Note:</strong> This uses the default encoding.
     * </p>
     *
     * @return Derived InputStream
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new ReaderInputStream(reader);
    }

    /**
     * Return the dataSourceName via the {@link DataSource} Interface method.
     *
     * <p>
     * Both the parameters are ignored
     * </p>
     *
     * @param shortNames
     *            ignored
     * @param inputFileName
     *            ignored
     * @return
     */
    @Override
    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return getDataSourceName();
    }

    /**
     * @return the dataSourceName
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param dataSourceName
     *            the dataSourceName to set
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public String toString() {
        return new StringBuilder(ReaderDataSource.class.getSimpleName())
                .append('[')
                .append(dataSourceName)
                .append(']')
                .toString();
    }
}

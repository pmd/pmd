/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.datasource;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.ReaderInputStream;


/**
 * DataSource implementation to read data from a reader.
 */
public class ReaderDataSource implements DataSource {
	

  
  
    /**
     * Reader 
     */
    private Reader reader;

    /**
     * Real or pseudo filename or path name.
     * 
     * <p>Including a file suffix mapped to the correct language helps assign the correct parser.
     * </p>
     */
    private String dataSourceName;
    private String encoding;

    /**
     * @param Create the DataSource from the Reader.
     */
    public ReaderDataSource(Reader reader, String dataSourceName) {
        this.reader = reader;
        this.dataSourceName = dataSourceName;
    }

    /**
     *  Convert the Reader into an InputStream.
     * 
     * @return Derived InputStream
     * @throws IOException 
     */
    public InputStream getInputStream() throws IOException {
        return new ReaderInputStream(reader) ;
    }

    /**
     * Return the dataSourceName via the {@link DataSource} Interface method.
     * 
     * <p>Both the parameters are ignored
     * </p>
     * 
     * @param shortNames
     * @param inputFileName
     * @return 
     */
    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return dataSourceName ;
    }

}

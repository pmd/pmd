/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * DataSource implementation to read data from a file.
 */
public class FileDataSource implements DataSource {
    private File file;

    /**
     * Constructor.
     * @param file the file to read
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return PMD.glomName(shortNames, inputFileName, file);
    }
}

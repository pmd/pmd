/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * DataSource implementation to read data from an entry
 * in a zip or jar file.
 */
public class ZipDataSource implements DataSource {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    /**
     * @param zipFile  the ZipFile
     * @param zipEntry the ZipEntry containing the file to read
     */
    public ZipDataSource(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    public InputStream getInputStream() throws IOException {
        return zipFile.getInputStream(zipEntry);
    }

    public String getNiceFileName(boolean shortNames, String inputFileName) {
        // FIXME: this could probably be done better
        return zipFile.getName() + ":" + zipEntry.getName();
    }
}

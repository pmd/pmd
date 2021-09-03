/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.pmd.util.datasource.internal.AbstractDataSource;

/**
 * DataSource implementation to read data from an entry in a zip or jar file.
 */
public class ZipDataSource extends AbstractDataSource {
    private final ZipFile zipFile;
    private final ZipEntry zipEntry;

    /**
     * @param zipFile
     *            the ZipFile
     * @param zipEntry
     *            the ZipEntry containing the file to read
     */
    public ZipDataSource(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return zipFile.getInputStream(zipEntry);
    }

    @Override
    public String getNiceFileName(boolean shortNames, String inputFileName) {
        // FIXME: this could probably be done better
        return zipFile.getName() + ":" + zipEntry.getName();
    }

    @Override
    public String toString() {
        return new StringBuilder(ZipDataSource.class.getSimpleName())
                .append('[')
                .append(zipFile.getName())
                .append('!')
                .append(zipEntry.getName())
                .append(']')
                .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((zipEntry == null) ? 0 : zipEntry.getName().hashCode());
        result = prime * result + ((zipFile == null) ? 0 : zipFile.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("PMD.CloseResource")
        ZipDataSource other = (ZipDataSource) obj;
        if (zipEntry == null) {
            if (other.zipEntry != null) {
                return false;
            }
        } else if (!zipEntry.getName().equals(other.zipEntry.getName())) {
            return false;
        }
        if (zipFile == null) {
            if (other.zipFile != null) {
                return false;
            }
        } else if (!zipFile.getName().equals(other.zipFile.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        if (zipFile != null) {
            zipFile.close();
        }
    }
}

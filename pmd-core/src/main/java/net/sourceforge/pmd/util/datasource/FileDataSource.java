/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.internal.util.ShortFilenameUtil;
import net.sourceforge.pmd.util.datasource.internal.AbstractDataSource;

/**
 * DataSource implementation to read data from a file.
 */
public class FileDataSource extends AbstractDataSource {
    private final File file;

    /**
     * @param file
     *            the file to read
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }

    @Override
    public String getNiceFileName(boolean shortNames, String inputPaths) {
        return glomName(shortNames, inputPaths, file);
    }

    private String glomName(boolean shortNames, String inputPaths, File file) {
        if (shortNames) {
            if (inputPaths != null) {
                List<String> inputPathPrefixes = Arrays.asList(inputPaths.split(","));
                final String absoluteFilePath = file.getAbsolutePath();
                return ShortFilenameUtil.determineFileName(inputPathPrefixes, absoluteFilePath);
            } else {
                // if the 'master' file is not specified, just use the file name
                return file.getName();
            }
        }

        try {
            return file.getCanonicalFile().getAbsolutePath();
        } catch (Exception e) {
            return file.getAbsolutePath();
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(FileDataSource.class.getSimpleName())
                .append('[')
                .append(file.getPath())
                .append(']')
                .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
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
        FileDataSource other = (FileDataSource) obj;
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!file.equals(other.file)) {
            return false;
        }
        return true;
    }


}

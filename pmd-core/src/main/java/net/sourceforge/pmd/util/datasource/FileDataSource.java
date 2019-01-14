/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DataSource implementation to read data from a file.
 */
public class FileDataSource implements DataSource {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

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
    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return glomName(shortNames, inputFileName, file);
    }

    private String glomName(boolean shortNames, String inputFileName, File file) {
        if (shortNames) {
            if (inputFileName != null) {
                for (final String prefix : inputFileName.split(",")) {
                    final Path prefPath = Paths.get(prefix).toAbsolutePath();
                    final String prefPathString = prefPath.toString();
                    final String absoluteFilePath = file.getAbsolutePath();

                    if (absoluteFilePath.startsWith(prefPathString)) {
                        if (prefPath.toFile().isDirectory()) {
                            return trimAnyPathSep(absoluteFilePath.substring(prefPathString.length()));
                        } else {
                            if (inputFileName.indexOf(FILE_SEPARATOR.charAt(0)) == -1) {
                                return inputFileName;
                            }
                            return trimAnyPathSep(absoluteFilePath.substring(prefPathString.lastIndexOf(FILE_SEPARATOR)));
                        }
                    }
                }
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

    private String trimAnyPathSep(String name) {

        return name.startsWith(FILE_SEPARATOR) ? name.substring(1) : name;
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

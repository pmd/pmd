/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DataSource implementation to read data from a file.
 */
public class FileDataSource implements DataSource {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private File file;

    /**
     * @param file
     *            the file to read
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
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
}

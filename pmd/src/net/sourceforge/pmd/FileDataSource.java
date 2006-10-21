/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DataSource implementation to read data from a file.
 */
public class FileDataSource implements DataSource {
	
	private static final String fileSeparator = System.getProperty("file.separator");
	
    private File file;

    /**
     * @param file the file to read
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return glomName(shortNames, inputFileName, file);
    }

    private String glomName(boolean shortNames, String inputFileName, File file) {
        if (shortNames && inputFileName.indexOf(',') == -1) {
            if ((new File(inputFileName)).isDirectory()) {
                return trimAnyPathSep(file.getAbsolutePath().substring(inputFileName.length()));
            } else {
                if (inputFileName.indexOf(fileSeparator.charAt(0)) == -1) {
                    return inputFileName;
                }
                return trimAnyPathSep(inputFileName.substring(inputFileName.lastIndexOf(System.getProperty("file.separator"))));
            }
        } 

        return file.getAbsolutePath();
    }

    private String trimAnyPathSep(String name) {

    	return name.startsWith(fileSeparator) ?
            name.substring(1) :
            name;
    }
}

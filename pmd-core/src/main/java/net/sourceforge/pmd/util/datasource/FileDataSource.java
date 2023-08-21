/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import java.io.File;

import net.sourceforge.pmd.util.datasource.internal.PathDataSource;

/**
 * DataSource implementation to read data from a file.
 */
@Deprecated
public class FileDataSource extends PathDataSource {

    /**
     * @param file the file to read
     */
    public FileDataSource(File file) {
        super(file.toPath(), null);
    }
}

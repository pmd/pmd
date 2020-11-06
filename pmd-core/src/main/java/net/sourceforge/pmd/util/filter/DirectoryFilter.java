/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.filter;

import java.io.File;

/**
 * Directory filter.
 * @deprecated See {@link Filter}
 */
@Deprecated
public final class DirectoryFilter implements Filter<File> {
    public static final DirectoryFilter INSTANCE = new DirectoryFilter();

    private DirectoryFilter() {
    }

    @Override
    public boolean filter(File file) {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return "is Directory";
    }
}

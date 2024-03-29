/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.util.filter;

import java.io.File;
import java.util.Locale;

/**
 * @deprecated See {@link Filter}
 */
@Deprecated
public class FileExtensionFilter implements Filter<File> {
    protected final String[] extensions;
    protected final boolean ignoreCase;

    /**
     * Matches any files with the given extensions, ignoring case
     */
    public FileExtensionFilter(String... extensions) {
        this(true, extensions);
    }

    /**
     * Matches any files with the given extensions, optionally ignoring case.
     */
    public FileExtensionFilter(boolean ignoreCase, String... extensions) {
        this.extensions = extensions;
        this.ignoreCase = ignoreCase;
        if (ignoreCase) {
            for (int i = 0; i < this.extensions.length; i++) {
                this.extensions[i] = this.extensions[i].toUpperCase(Locale.ROOT);
            }
        }
    }

    @Override
    public boolean filter(File file) {
        boolean accept = extensions == null;
        if (!accept) {
            for (String extension : extensions) {
                String name = file.getName();
                if (ignoreCase ? name.toUpperCase(Locale.ROOT).endsWith(extension) : name.endsWith(extension)) {
                    accept = true;
                    break;
                }
            }
        }
        return accept;
    }
}

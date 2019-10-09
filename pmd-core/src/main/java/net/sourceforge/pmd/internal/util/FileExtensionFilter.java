/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.File;
import java.util.Locale;
import java.util.function.Predicate;

final class FileExtensionFilter implements Predicate<File> {

    private final String[] extensions;
    private final boolean ignoreCase;

    /**
     * Matches any files with the given extensions, optionally ignoring case.
     */
    FileExtensionFilter(boolean ignoreCase, String... extensions) {
        this.extensions = extensions;
        this.ignoreCase = ignoreCase;
        if (ignoreCase) {
            for (int i = 0; i < this.extensions.length; i++) {
                this.extensions[i] = this.extensions[i].toUpperCase(Locale.ROOT);
            }
        }
    }

    @Override
    public boolean test(File file) {
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

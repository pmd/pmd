/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.Locale;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

final class FileExtensionFilter implements Predicate<String> {

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
    public boolean test(String path) {
        boolean accept = extensions == null;
        if (!accept) {
            for (String extension : extensions) {
                boolean matches =
                    ignoreCase ? StringUtils.endsWithIgnoreCase(path, extension)
                               : path.endsWith(extension);
                if (matches) {
                    accept = true;
                    break;
                }
            }
        }
        return accept;
    }
}

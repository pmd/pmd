/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This class is here just to make maven-pmd-plugin compile with
// pmd 7.0.0 including this compat6 module.
// It would only be used, if a custom language (other than java, jsp or javascript)
// would be requested.

package net.sourceforge.pmd.cpd;

import java.util.Properties;

public final class LanguageFactory {
    private LanguageFactory() {
        // utility class
    }

    public static Language createLanguage(String name, Properties properties) {
        throw new UnsupportedOperationException();
    }
}

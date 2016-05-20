/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.util;

import java.util.ResourceBundle;

/**
 * helps with internationalization
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class NLS {
    private static final ResourceBundle BUNDLE;

    static {
        BUNDLE = ResourceBundle.getBundle("net.sourceforge.pmd.util.viewer.resources.viewer_strings");
    }

    /**
     * translates the given key to the message
     *
     * @param key key to be translated
     * @return translated string
     */
    public static String nls(String key) {
        return BUNDLE.getString(key);
    }
}


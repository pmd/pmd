/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0
// Changes: setProperties doesn't work, provide properties in constructor already

package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class JavaLanguage extends AbstractLanguage {
    public JavaLanguage() {
        this(System.getProperties());
    }

    public JavaLanguage(Properties properties) {
        super("Java", "java", new JavaTokenizer(properties), ".java");
    }

    @Override
    public final void setProperties(Properties properties) {
        // note: this might be actually incompatible
        throw new UnsupportedOperationException();
    }
}

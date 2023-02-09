/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class JavaLanguage extends AbstractLanguage {
    public JavaLanguage() {
        this(System.getProperties());
    }

    public JavaLanguage(Properties properties) {
        super(JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, new JavaTokenizer(), JavaLanguageModule.EXTENSIONS);
        setProperties(properties);
    }

    @Override
    public final void setProperties(Properties properties) {
        JavaTokenizer tokenizer = (JavaTokenizer) getTokenizer();
        tokenizer.setProperties(properties);
    }
}

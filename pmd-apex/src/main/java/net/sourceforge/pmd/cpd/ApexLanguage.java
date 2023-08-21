/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

public class ApexLanguage extends AbstractLanguage {

    public ApexLanguage() {
        this(new Properties());
    }

    public ApexLanguage(Properties properties) {
        super(ApexLanguageModule.NAME, ApexLanguageModule.TERSE_NAME, new ApexTokenizer(), ApexLanguageModule.EXTENSIONS);
        setProperties(properties);
    }

    @Override
    public final void setProperties(Properties properties) {
        ApexTokenizer tokenizer = (ApexTokenizer) getTokenizer();
        tokenizer.setProperties(properties);
    }
}

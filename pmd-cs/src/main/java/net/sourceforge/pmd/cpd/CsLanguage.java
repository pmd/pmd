/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

/**
 * Language implementation for C#
 */
public class CsLanguage extends AbstractLanguage {

    public CsLanguage() {
        this(System.getProperties());
    }

    public CsLanguage(Properties properties) {
        super("C#", "cs", new CsTokenizer(), ".cs");
        setProperties(properties);
    }

    @Override
    public final void setProperties(Properties properties) {
        CsTokenizer tokenizer = (CsTokenizer) getTokenizer();
        tokenizer.setProperties(properties);
    }
}

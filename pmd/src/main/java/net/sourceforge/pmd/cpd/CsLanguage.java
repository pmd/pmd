/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class CsLanguage extends AbstractLanguage {
	public CsLanguage() {
		super(new CsTokenizer(), ".cs");
	}
        public final void setProperties(Properties properties) {
        CsTokenizer tokenizer = (CsTokenizer)getTokenizer();
        tokenizer.setProperties(properties);
	}
}
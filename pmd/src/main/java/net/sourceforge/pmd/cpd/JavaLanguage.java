/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class JavaLanguage extends AbstractLanguage {
	public JavaLanguage() {
		this(System.getProperties());
	}

	public JavaLanguage(Properties properties) {
		super(new JavaTokenizer(), ".java");
		setProperties(properties);
	}

	public final void setProperties(Properties properties) {
        JavaTokenizer tokenizer = (JavaTokenizer)getTokenizer();
        tokenizer.setProperties(properties);
	}
}

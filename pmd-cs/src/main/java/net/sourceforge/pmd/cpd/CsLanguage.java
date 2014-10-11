/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

public class CsLanguage extends AbstractLanguage {
	public CsLanguage() {
		super(new CsTokenizer(), ".cs");
	}
}

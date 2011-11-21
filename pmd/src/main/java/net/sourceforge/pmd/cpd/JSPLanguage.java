/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

public class JSPLanguage extends AbstractLanguage {
	public JSPLanguage() {
		super(new JSPTokenizer(), ".jsp", ".jspx");
	}
}

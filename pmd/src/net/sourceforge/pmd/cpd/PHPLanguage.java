/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

public class PHPLanguage extends AbstractLanguage {
	public PHPLanguage() {
		super(new PHPTokenizer(), ".php", ".class");
	}
}

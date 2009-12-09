/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @author Zev Blut zb@ubit.com
 */
package net.sourceforge.pmd.cpd;

public class EcmascriptLanguage extends AbstractLanguage {
	public EcmascriptLanguage() {
		super(new EcmascriptTokenizer(), ".js");
	}
}

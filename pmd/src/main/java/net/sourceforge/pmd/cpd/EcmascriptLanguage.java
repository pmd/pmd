/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

/**
 * 
 * @author Zev Blut zb@ubit.com
 */
public class EcmascriptLanguage extends AbstractLanguage {
	public EcmascriptLanguage() {
		super(new EcmascriptTokenizer(), ".js");
	}
}

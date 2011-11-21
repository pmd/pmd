/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @author Zev Blut zb@ubit.com
 */
package net.sourceforge.pmd.cpd;

public class RubyLanguage extends AbstractLanguage {
	public RubyLanguage() {
		super(new RubyTokenizer(), ".rb", ".cgi", ".class");
	}
}

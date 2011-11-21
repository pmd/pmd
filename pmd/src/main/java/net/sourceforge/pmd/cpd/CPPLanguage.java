/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

public class CPPLanguage extends AbstractLanguage {
	public CPPLanguage() {
		super(new CPPTokenizer(), ".h", ".hpp", ".hxx",".c", ".cpp", ".cxx", ".cc", ".C");
	}
}

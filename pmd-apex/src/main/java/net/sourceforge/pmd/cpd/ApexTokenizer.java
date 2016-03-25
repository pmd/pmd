/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

public class ApexTokenizer extends AbstractTokenizer {
	public ApexTokenizer() {
		// setting markers for "string" in apex
		this.stringToken = new ArrayList<>();
		this.stringToken.add("\'");
		this.stringToken.add("\"");

		// setting markers for 'ignorable character' in apex
		this.ignorableCharacter = new ArrayList<>();
		this.ignorableCharacter.add(";");

		// setting markers for 'ignorable string' in apex
		this.ignorableStmt = new ArrayList<>();

		// strings do indeed span multiple lines in apex
		this.spanMultipleLinesString = false;
		// the lines do to end with backslashes
		this.spanMultipleLinesLineContinuationCharacter = '\\';
	}
}
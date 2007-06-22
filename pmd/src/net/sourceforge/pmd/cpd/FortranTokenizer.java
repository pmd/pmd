/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

/**
 * @author Romain PELISSE - romain.pelisse@atosorigin.com
 *
 */
public class FortranTokenizer extends AbstractTokenizer implements Tokenizer
{
	public FortranTokenizer()
	{
//		 setting markers for "string" in Fortran
		this.stringToken = new ArrayList<String>();
		this.stringToken.add("\'");
		// setting markers for 'ignorable character' in Fortran
		this.ignorableCharacter = new ArrayList<String>();
		this.ignorableCharacter.add("(");
		this.ignorableCharacter.add(")");
		this.ignorableCharacter.add(",");

		// setting markers for 'ignorable string' in Fortran
		this.ignorableStmt = new ArrayList<String>();
		this.ignorableStmt.add("do");
		this.ignorableStmt.add("while");
		this.ignorableStmt.add("end");
		// Fortran comment start with an !
		this.ONE_LINE_COMMENT_CHAR = '!';
	}
}

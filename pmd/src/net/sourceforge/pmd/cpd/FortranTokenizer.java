/**
 * Copyright Atos Origin - 2007
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
//		 setting markers for "string" in ruby
		this.stringToken = new ArrayList();
		this.stringToken.add("\'");
		// setting markers for 'ignorable character' in Ruby
		this.ignorableCharacter = new ArrayList();
		this.ignorableCharacter.add("(");
		this.ignorableCharacter.add(")");
		this.ignorableCharacter.add(",");

		// setting markers for 'ignorable string' in Ruby
		this.ignorableStmt = new ArrayList();
		this.ignorableStmt.add("do");
		this.ignorableStmt.add("while");
		this.ignorableStmt.add("end");
		// Fortran comment start with an !
		this.ONE_LINE_COMMENT_CHAR = '!';
	}
}

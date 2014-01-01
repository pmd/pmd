/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

/**
 *
 * @author Zev Blut zb@ubit.com
 */
public class RubyTokenizer extends AbstractTokenizer
{
    public RubyTokenizer()
    {
	// setting markers for "string" in ruby
	this.stringToken = new ArrayList<String>();
	this.stringToken.add("\'");
	this.stringToken.add("\"");
	// setting markers for 'ignorable character' in Ruby
	this.ignorableCharacter = new ArrayList<String>();
	this.ignorableCharacter.add("{");
	this.ignorableCharacter.add("}");
	this.ignorableCharacter.add("(");
	this.ignorableCharacter.add(")");
	this.ignorableCharacter.add(";");
	this.ignorableCharacter.add(",");

	// setting markers for 'ignorable string' in Ruby
	this.ignorableStmt = new ArrayList<String>();
	this.ignorableStmt.add("while");
	this.ignorableStmt.add("do");
	this.ignorableStmt.add("end");
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

/**
 * Tokenizer for Ruby.
 *
 * @author Zev Blut zb@ubit.com
 */
public class RubyTokenizer extends AbstractTokenizer {

    /**
     * Creates a new Ruby tokenizer.
     */
    public RubyTokenizer() {
        // setting markers for "string" in ruby
        this.stringToken = new ArrayList<>();
        this.stringToken.add("\'");
        this.stringToken.add("\"");
        // setting markers for 'ignorable character' in Ruby
        this.ignorableCharacter = new ArrayList<>();
        this.ignorableCharacter.add("{");
        this.ignorableCharacter.add("}");
        this.ignorableCharacter.add("(");
        this.ignorableCharacter.add(")");
        this.ignorableCharacter.add(";");
        this.ignorableCharacter.add(",");

        // setting markers for 'ignorable string' in Ruby
        this.ignorableStmt = new ArrayList<>();
        this.ignorableStmt.add("while");
        this.ignorableStmt.add("do");
        this.ignorableStmt.add("end");
    }
}

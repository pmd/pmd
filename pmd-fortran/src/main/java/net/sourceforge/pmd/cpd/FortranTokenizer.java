/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

/**
 * Tokenizer implementation for Fortran
 *
 * @author Romain PELISSE - romain.pelisse@atosorigin.com
 */
public class FortranTokenizer extends AbstractTokenizer implements Tokenizer {

    /**
     * Creates a new instance of {@link FortranTokenizer}.
     */
    public FortranTokenizer() {
        this.spanMultipleLinesString = false; // No such thing in Fortran !
        // setting markers for "string" in Fortran
        this.stringToken = new ArrayList<>();
        this.stringToken.add("\'");
        // setting markers for 'ignorable character' in Fortran
        this.ignorableCharacter = new ArrayList<>();
        this.ignorableCharacter.add("(");
        this.ignorableCharacter.add(")");
        this.ignorableCharacter.add(",");

        // setting markers for 'ignorable string' in Fortran
        this.ignorableStmt = new ArrayList<>();
        this.ignorableStmt.add("do");
        this.ignorableStmt.add("while");
        this.ignorableStmt.add("end");
        this.ignorableStmt.add("if");
        // Fortran comment start with an !
        this.oneLineCommentChar = '!';
    }
}

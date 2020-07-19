/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * A continue statement, that jumps to the next iteration of an enclosing loop.
 *
 * <pre class="grammar">
 *
 * ContinueStatement ::= "continue" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 */
public final class ASTContinueStatement extends AbstractStatement {

    ASTContinueStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An empty statement (useless).
 *
 * <pre class="grammar">
 *
 * EmptyStatement ::= ";"
 *
 * </pre>
 */
public final class ASTEmptyStatement extends AbstractStatement {

    ASTEmptyStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}

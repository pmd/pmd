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

    ASTContinueStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}

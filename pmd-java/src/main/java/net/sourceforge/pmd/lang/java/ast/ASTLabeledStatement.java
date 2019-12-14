/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A wrapper around a statement that assigns it a label.
 *
 * <pre class="grammar">
 *
 * LabeledStatement ::= &lt;IDENTIFIER&gt; ":" {@link ASTStatement Statement}
 *
 * </pre>
 */
public final class ASTLabeledStatement extends AbstractStatement {

    ASTLabeledStatement(int id) {
        super(id);
    }

    ASTLabeledStatement(JavaParser p, int id) {
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

    /**
     * Returns the name of the label.
     */
    public String getLabel() {
        return getImage();
    }

    /**
     * Returned the statement named by this label.
     */
    public ASTStatement getStatement() {
        return (ASTStatement) jjtGetChild(1);
    }

}

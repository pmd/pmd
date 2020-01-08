/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A non-fallthrough switch rule, introduced with switch expressions.
 * See {@link ASTSwitchLike}.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledExpression ::= {@link ASTSwitchLabel SwitchLabel} "->" {@link ASTSwitchArrowRHS SwitchArrowRHS}
 *
 * </pre>
 */
public final class ASTSwitchArrowBranch extends AbstractJavaNode implements LeftRecursiveNode, ASTSwitchBranch {

    ASTSwitchArrowBranch(int id) {
        super(id);
    }

    ASTSwitchArrowBranch(JavaParser p, int id) {
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

    /** Returns the right hand side of the arrow. */
    public ASTSwitchArrowRHS getRightHandSide() {
        return (ASTSwitchArrowRHS) getLastChild();
    }


}

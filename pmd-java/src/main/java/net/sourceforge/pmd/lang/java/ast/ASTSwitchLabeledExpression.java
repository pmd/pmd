/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * One of the {@linkplain ASTSwitchLabeledRule SwitchLabeledRule}s.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledExpression ::= {@link ASTSwitchLabel SwitchLabel} "->" {@link ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTSwitchLabeledExpression extends AbstractJavaNode implements ASTSwitchLabeledRule {

    ASTSwitchLabeledExpression(int id) {
        super(id);
    }

    ASTSwitchLabeledExpression(JavaParser p, int id) {
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

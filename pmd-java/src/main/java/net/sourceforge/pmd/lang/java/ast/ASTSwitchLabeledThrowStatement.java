/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * One of the {@linkplain ASTSwitchLabeledRule SwitchLabeledRule}s.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledThrowStatement ::= {@link ASTSwitchLabel SwitchLabel} "->" {@link ASTThrowStatement ThrowStatement}
 *
 * </pre>
 */
public final class ASTSwitchLabeledThrowStatement extends AbstractJavaNode implements ASTSwitchLabeledRule {

    ASTSwitchLabeledThrowStatement(int id) {
        super(id);
    }

    ASTSwitchLabeledThrowStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

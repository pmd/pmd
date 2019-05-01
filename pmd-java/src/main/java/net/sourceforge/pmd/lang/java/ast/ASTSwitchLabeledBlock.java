/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * One of the {@linkplain ASTSwitchLabeledRule SwitchLabeledRule}s.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledBlock ::= {@link ASTSwitchLabel SwitchLabel} "->" {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTSwitchLabeledBlock extends AbstractJavaNode implements ASTSwitchLabeledRule {

    ASTSwitchLabeledBlock(int id) {
        super(id);
    }

    ASTSwitchLabeledBlock(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

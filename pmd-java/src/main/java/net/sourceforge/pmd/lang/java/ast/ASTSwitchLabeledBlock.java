/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

/**
 * One of the {@linkplain ASTSwitchLabeledRule SwitchLabeledRule}s.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledBlock ::= {@link ASTSwitchLabel SwitchLabel} "->" {@link ASTBlock Block}
 *
 * </pre>
 */
public class ASTSwitchLabeledBlock extends AbstractJavaNode implements ASTSwitchLabeledRule {

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

    @Override
    public void jjtClose() {
        super.jjtClose();
        if (jjtGetNumChildren() > 0) {
            AbstractNode firstChild = (AbstractNode) jjtGetChild(0);
            jjtSetFirstToken(firstChild.jjtGetFirstToken());
        }
    }
}

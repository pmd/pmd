/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AbstractNode;

public class ASTSwitchLabeledBlock extends AbstractJavaNode implements ASTSwitchLabeledRule {

    @Deprecated
    @InternalApi
    ASTSwitchLabeledBlock(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    ASTSwitchLabeledBlock(JavaParser p, int id) {
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

    @Override
    public void jjtClose() {
        super.jjtClose();
        if (jjtGetNumChildren() > 0) {
            AbstractNode firstChild = (AbstractNode) jjtGetChild(0);
            jjtSetFirstToken(firstChild.jjtGetFirstToken());
        }
    }
}

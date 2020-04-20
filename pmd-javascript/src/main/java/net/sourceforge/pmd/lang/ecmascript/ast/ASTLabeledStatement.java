/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.LabeledStatement;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTLabeledStatement extends AbstractEcmascriptNode<LabeledStatement> {
    @Deprecated
    @InternalApi
    public ASTLabeledStatement(LabeledStatement labeledStatement) {
        super(labeledStatement);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public int getNumLabels() {
        return node.getLabels().size();
    }

    public ASTLabel getLabel(int index) {
        return (ASTLabel) getChild(index);
    }

    public EcmascriptNode<?> getStatement() {
        return (EcmascriptNode<?>) getChild(getNumChildren() - 1);
    }
}

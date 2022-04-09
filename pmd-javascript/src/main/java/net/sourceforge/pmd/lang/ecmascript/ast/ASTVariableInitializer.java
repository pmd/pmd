/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.VariableInitializer;

public final class ASTVariableInitializer extends AbstractEcmascriptNode<VariableInitializer> implements DestructuringNode {
    ASTVariableInitializer(VariableInitializer variableInitializer) {
        super(variableInitializer);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTarget() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getInitializer() {
        if (getNumChildren() > 0) {
            return (EcmascriptNode<?>) getChild(1);
        } else {
            return null;
        }
    }

    @Override
    public boolean isDestructuring() {
        return node.isDestructuring();
    }
}

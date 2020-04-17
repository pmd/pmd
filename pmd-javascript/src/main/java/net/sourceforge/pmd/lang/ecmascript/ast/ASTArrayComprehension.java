/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayComprehension;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTArrayComprehension extends AbstractEcmascriptNode<ArrayComprehension> {
    @Deprecated
    @InternalApi
    public ASTArrayComprehension(ArrayComprehension arrayComprehension) {
        super(arrayComprehension);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getResult() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public int getNumArrayComprehensionLoops() {
        return node.getLoops().size();
    }

    public ASTArrayComprehensionLoop getArrayComprehensionLoop(int index) {
        return (ASTArrayComprehensionLoop) getChild(index + 1);
    }

    public boolean hasFilter() {
        return node.getFilter() != null;
    }

    public EcmascriptNode<?> getFilter() {
        return (EcmascriptNode<?>) getChild(getNumChildren() - 1);
    }
}

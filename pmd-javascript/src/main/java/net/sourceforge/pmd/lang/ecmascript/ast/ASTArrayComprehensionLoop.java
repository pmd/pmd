/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayComprehensionLoop;

public class ASTArrayComprehensionLoop extends AbstractEcmascriptNode<ArrayComprehensionLoop> {

    public ASTArrayComprehensionLoop(ArrayComprehensionLoop arrayComprehensionLoop) {
        super(arrayComprehensionLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getIterator() {
        return (EcmascriptNode<?>) jjtGetChild(0);
    }

    public EcmascriptNode<?> getIteratedObject() {
        return (EcmascriptNode<?>) jjtGetChild(1);
    }
}

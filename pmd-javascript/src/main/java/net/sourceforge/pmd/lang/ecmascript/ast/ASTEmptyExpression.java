/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.EmptyExpression;

public class ASTEmptyExpression extends AbstractEcmascriptNode<EmptyExpression> {
    public ASTEmptyExpression(EmptyExpression emptyExpression) {
        super(emptyExpression);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ArrayLoadExpression;

public class ASTArrayLoadExpression extends AbstractApexNode<ArrayLoadExpression> {

    public ASTArrayLoadExpression(ArrayLoadExpression arrayLoadExpression) {
        super(arrayLoadExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

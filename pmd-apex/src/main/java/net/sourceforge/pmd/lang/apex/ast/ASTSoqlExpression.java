/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SoqlExpression;

public class ASTSoqlExpression extends AbstractApexNode<SoqlExpression> {

    public ASTSoqlExpression(SoqlExpression soqlExpression) {
        super(soqlExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

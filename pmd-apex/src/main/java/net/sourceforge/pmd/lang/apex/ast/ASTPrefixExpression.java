/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.PrefixExpression;

public class ASTPrefixExpression extends AbstractApexNode<PrefixExpression> {

    public ASTPrefixExpression(PrefixExpression prefixExpression) {
        super(prefixExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

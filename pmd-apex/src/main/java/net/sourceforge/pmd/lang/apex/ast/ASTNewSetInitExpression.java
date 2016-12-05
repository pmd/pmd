/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewSetInitExpression;

public class ASTNewSetInitExpression extends AbstractApexNode<NewSetInitExpression> {

    public ASTNewSetInitExpression(NewSetInitExpression newSetInitExpression) {
        super(newSetInitExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

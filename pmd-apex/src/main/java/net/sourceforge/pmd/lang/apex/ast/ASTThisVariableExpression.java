/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.ThisExpression;

public class ASTThisVariableExpression extends AbstractApexNode.Single<ThisExpression> {

    @Deprecated
    @InternalApi
    public ASTThisVariableExpression(ThisExpression thisExpression) {
        super(thisExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

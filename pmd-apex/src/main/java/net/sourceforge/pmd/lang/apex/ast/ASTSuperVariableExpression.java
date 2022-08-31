/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.SuperExpression;

public class ASTSuperVariableExpression extends AbstractApexNode.Single<SuperExpression> {

    @Deprecated
    @InternalApi
    public ASTSuperVariableExpression(SuperExpression superExpression) {
        super(superExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

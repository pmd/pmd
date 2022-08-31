/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.CallExpression;

public class ASTSuperMethodCallExpression extends AbstractApexNode.Single<CallExpression> {

    @Deprecated
    @InternalApi
    public ASTSuperMethodCallExpression(CallExpression callExpression) {
        super(callExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

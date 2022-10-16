/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.CallExpression;

public class ASTThisMethodCallExpression extends AbstractApexNode.Single<CallExpression> {

    ASTThisMethodCallExpression(CallExpression callExpression) {
        super(callExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

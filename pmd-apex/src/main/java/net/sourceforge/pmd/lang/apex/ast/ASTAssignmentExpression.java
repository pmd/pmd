/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.AssignExpression;

public class ASTAssignmentExpression extends AbstractApexNode.Single<AssignExpression> {

    @Deprecated
    @InternalApi
    public ASTAssignmentExpression(AssignExpression assignmentExpression) {
        super(assignmentExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public AssignmentOperator getOp() {
        return AssignmentOperator.valueOf(node.getPreOperation());
    }
}

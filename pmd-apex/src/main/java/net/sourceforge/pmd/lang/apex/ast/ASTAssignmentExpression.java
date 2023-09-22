/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.AssignExpression;

public final class ASTAssignmentExpression extends AbstractApexNode.Single<AssignExpression> {

    ASTAssignmentExpression(AssignExpression assignmentExpression) {
        super(assignmentExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public AssignmentOperator getOp() {
        return AssignmentOperator.valueOf(node.getPreOperation());
    }
}

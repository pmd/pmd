/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.AssignmentOp;
import apex.jorje.semantic.ast.expression.AssignmentExpression;

public final class ASTAssignmentExpression extends AbstractApexNode<AssignmentExpression> {

    ASTAssignmentExpression(AssignmentExpression assignmentExpression) {
        super(assignmentExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public AssignmentOp getOperator() {
        return node.getOp();
    }
}

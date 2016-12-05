/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.AssignmentExpression;

public class ASTAssignmentExpression extends AbstractApexNode<AssignmentExpression> {

    public ASTAssignmentExpression(AssignmentExpression assignmentExpression) {
        super(assignmentExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

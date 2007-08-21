package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.typeresolution.TypeHelper;

public class UseStringBufferForStringAppends extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node, String.class)) {
            return data;
        }
        Node parent = node.jjtGetParent().jjtGetParent();
        if (!parent.getClass().equals(ASTLocalVariableDeclaration.class)) {
            return data;
        }
        for (NameOccurrence no: node.getUsages()) {
            SimpleNode name = no.getLocation();
            ASTStatementExpression statement = name.getFirstParentOfType(ASTStatementExpression.class);
            if (statement == null) {
                continue;
            }
            if (statement.jjtGetNumChildren() > 0 && statement.jjtGetChild(0).getClass().equals(ASTPrimaryExpression.class)) {
                ASTName astName = ((SimpleNode) statement.jjtGetChild(0)).getFirstChildOfType(ASTName.class);
                if(astName != null){
                    if (astName.equals(name)) {
                        ASTAssignmentOperator assignmentOperator = statement.getFirstChildOfType(ASTAssignmentOperator.class);
                        if (assignmentOperator != null && assignmentOperator.isCompound()) {
                            addViolation(data, assignmentOperator);
                        }
                    } else if(astName.getImage().equals(name.getImage())){
                        ASTAssignmentOperator assignmentOperator = statement.getFirstChildOfType(ASTAssignmentOperator.class);
                        if (assignmentOperator != null && !assignmentOperator.isCompound()) {
                            addViolation(data, astName);
                        }
                    }
                }
            }
        }
        return data;
    }
}

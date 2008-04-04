package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.typeresolution.TypeHelper;

public class UseStringBufferForStringAppends extends AbstractJavaRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node, String.class)) {
            return data;
        }
        Node parent = node.jjtGetParent().jjtGetParent();
        if (!parent.getClass().equals(ASTLocalVariableDeclaration.class)) {
            return data;
        }
        for (NameOccurrence no: node.getUsages()) {
            Node name = no.getLocation();
            ASTStatementExpression statement = name.getFirstParentOfType(ASTStatementExpression.class);
            if (statement == null) {
                continue;
            }
            if (statement.jjtGetNumChildren() > 0 && statement.jjtGetChild(0).getClass().equals(ASTPrimaryExpression.class)) {
                ASTName astName = statement.jjtGetChild(0).getFirstChildOfType(ASTName.class);
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

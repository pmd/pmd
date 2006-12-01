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

import java.util.Iterator;

public class UseStringBufferForStringAppends extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.getTypeNameNode().jjtGetNumChildren() == 0 || !"String".equals(((SimpleNode) node.getTypeNameNode().jjtGetChild(0)).getImage())) {
            return data;
        }
        Node parent = node.jjtGetParent().jjtGetParent();
        if (!parent.getClass().equals(ASTLocalVariableDeclaration.class)) {
            return data;
        }
        for (Iterator iter = node.getUsages().iterator(); iter.hasNext();) {
            NameOccurrence no = (NameOccurrence) iter.next();
            SimpleNode name = (SimpleNode) no.getLocation();
            ASTStatementExpression statement = (ASTStatementExpression) name.getFirstParentOfType(ASTStatementExpression.class);
            if (statement == null) {
                continue;
            }
            if (statement.jjtGetNumChildren() > 0 && statement.jjtGetChild(0).getClass().equals(ASTPrimaryExpression.class)) {
                // FIXME - hm, is there a bug in those methods?
                // check that we're looking at the "left hand" node. NB:
                // no.isRightHand / no.isLeftHand doesn't look like it works
                ASTName astName = (ASTName) ((SimpleNode) statement.jjtGetChild(0)).getFirstChildOfType(ASTName.class);
                if (astName != null && astName.equals(name)) {
                    ASTAssignmentOperator assignmentOperator = (ASTAssignmentOperator) statement.getFirstChildOfType(ASTAssignmentOperator.class);
                    if (assignmentOperator != null && assignmentOperator.isCompound()) {
                        addViolation(data, assignmentOperator);
                    }
                }
            }
        }

        return data;
    }
}

package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCastExpression;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTBlock;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a mailto:trond.andersen@nordea.com>Trond Andersen</a>
 */
public class ExceptionTypeChecking extends AbstractRule {

    public Object visit(ASTCatchStatement node, Object data) {
        String exceptionParameter = getExceptionParameter(node);
        // Retrieves all instance of expressions
        ASTBlock block = (ASTBlock)(node.jjtGetChild(1));
        List myList = block.findChildrenOfType(ASTInstanceOfExpression.class);
        for (Iterator i = myList.iterator(); i.hasNext();) {
            evaluateInstanceOfExpression((ASTInstanceOfExpression) i.next(), exceptionParameter, (RuleContext)data);
        }
        return super.visit(node, data);
    }

    private void evaluateInstanceOfExpression(ASTInstanceOfExpression instanceOfExpression,
                                              String exceptionName, RuleContext ctx) {
        if (!hasTypeEvaluation(instanceOfExpression)) {
            return;
        }
        if (exceptionName.equals(getInstanceOfObjectReference(instanceOfExpression))) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, instanceOfExpression));
        }
    }

    private boolean hasTypeEvaluation(ASTInstanceOfExpression instanceOfExpression) {
        List typeList = instanceOfExpression.findChildrenOfType(ASTType.class);
        if (!typeList.isEmpty()) {
            ASTType theType = (ASTType) typeList.get(0);
            if (!(theType.jjtGetParent() instanceof ASTCastExpression)) {
                return true;
            }
        }
        return false;
    }

    private String getInstanceOfObjectReference(ASTInstanceOfExpression expression) {
        List primaryList = expression.findChildrenOfType(ASTPrimaryExpression.class);
        String objectReferenceName = null;
        if (primaryList.size() == 1) {
            List someList = ((ASTPrimaryExpression) primaryList.get(0)).findChildrenOfType(ASTName.class);
            if (someList.size() == 1) {
                objectReferenceName = ((ASTName) someList.get(0)).getImage();
            }
        }
        return objectReferenceName;
    }

    private String getExceptionParameter(ASTCatchStatement catchStmt) {
        ASTVariableDeclaratorId id = (ASTVariableDeclaratorId)((SimpleNode)catchStmt.jjtGetChild(0)).findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
        return id.getImage();
    }

}

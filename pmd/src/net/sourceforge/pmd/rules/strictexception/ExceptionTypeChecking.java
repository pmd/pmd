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

import java.util.Iterator;
import java.util.List;

/**
 * 
 * <p>
 * @author <a mailto:trond.andersen@nordea.com>Trond Andersen</a>
 * @version 1.0
 * @since 1.1?
 */
public class ExceptionTypeChecking extends AbstractRule {

    public Object visit(ASTTryStatement catchStatment, Object object) {
        if (catchStatment.hasCatch()) {
            for (Iterator iter = catchStatment.getCatchBlocks().iterator(); iter.hasNext();) {
                evaluateCatchClause((ASTCatch)iter.next(), (RuleContext)object);
            }
        }

        return super.visit(catchStatment, object);
    }

    private void evaluateCatchClause(ASTCatch catchStmt, RuleContext ctx) {
        String exceptionParameter = getExceptionParameter(catchStmt);
        // Retrieves all instance of expressions
        List myList = catchStmt.getBlock().findChildrenOfType(ASTInstanceOfExpression.class);

        for (Iterator i = myList.iterator(); i.hasNext();) {
            evaluateInstanceOfExpression((ASTInstanceOfExpression)i.next(), exceptionParameter, ctx);
        }
    }

    private void evaluateInstanceOfExpression(ASTInstanceOfExpression instanceOfExpression,
                                              String exceptionName, RuleContext ctx) {
        if (!hasTypeEvaluation(instanceOfExpression)) {
            return;
        }
        if (exceptionName.equals( getInstanceOfObjectReference(instanceOfExpression)) ) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, instanceOfExpression.getBeginLine()));
        }
    }

    private boolean hasTypeEvaluation(ASTInstanceOfExpression instanceOfExpression) {
        List typeList = instanceOfExpression.findChildrenOfType(ASTType.class);
        if (typeList != null && typeList.size() >= 1) {
            ASTType theType = (ASTType)typeList.get(0);
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
            List someList = ((ASTPrimaryExpression)primaryList.get(0)).findChildrenOfType(ASTName.class);
            if (someList.size() == 1) {
                objectReferenceName = ((ASTName)someList.get(0)).getImage();
            }
        }
        return objectReferenceName;
    }

    private String getExceptionParameter(ASTCatch catchStmt) {
        List declarationList = catchStmt.getFormalParameter().findChildrenOfType(ASTVariableDeclaratorId.class);
        return ((ASTVariableDeclaratorId)declarationList.get(0)).getImage();
    }

}

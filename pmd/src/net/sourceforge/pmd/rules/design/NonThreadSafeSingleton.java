/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NonThreadSafeSingleton extends AbstractRule {

    private Map fieldDecls = new HashMap();

    private boolean checkNonStaticMethods = true;

    private boolean checkNonStaticFields = true;

    public NonThreadSafeSingleton() {
        checkNonStaticMethods = super.getBooleanProperty("checkNonStaticMethods");
        checkNonStaticFields = super.getBooleanProperty("checkNonStaticFields");
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        fieldDecls.clear();
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (checkNonStaticFields || node.isStatic()) {
            fieldDecls.put(node.getVariableName(), node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {

        if ((checkNonStaticMethods && !node.isStatic()) || node.isSynchronized()) {
            return super.visit(node, data);
        }

        List ifStatements = node.findChildrenOfType(ASTIfStatement.class);
        for (Iterator iter = ifStatements.iterator(); iter.hasNext();) {
            ASTIfStatement ifStatement = (ASTIfStatement) iter.next();
            if (ifStatement.getFirstParentOfType(ASTSynchronizedStatement.class) == null) {
                ASTNullLiteral NullLiteral = (ASTNullLiteral) ifStatement.getFirstChildOfType(ASTNullLiteral.class);

                if (NullLiteral == null) {
                    continue;
                }
                ASTName Name = (ASTName) ifStatement.getFirstChildOfType(ASTName.class);
                if (Name == null || !fieldDecls.containsKey(Name.getImage())) {
                    continue;
                }
                List assigmnents = ifStatement.findChildrenOfType(ASTAssignmentOperator.class);
                boolean violation = false;
                for (int ix = 0; ix < assigmnents.size(); ix++) {
                    ASTAssignmentOperator oper = (ASTAssignmentOperator) assigmnents.get(ix);
                    if (!oper.jjtGetParent().getClass().equals(ASTStatementExpression.class)) {
                        continue;
                    }
                    ASTStatementExpression expr = (ASTStatementExpression) oper.jjtGetParent();
                    if (expr.jjtGetChild(0).getClass().equals(ASTPrimaryExpression.class) && ((ASTPrimaryExpression) expr.jjtGetChild(0)).jjtGetChild(0).getClass().equals(ASTPrimaryPrefix.class)) {
                        ASTPrimaryPrefix pp = (ASTPrimaryPrefix) ((ASTPrimaryExpression) expr.jjtGetChild(0)).jjtGetChild(0);
                        ASTName name = (ASTName) pp.jjtGetChild(0);
                        if (fieldDecls.containsKey(name.getImage())) {
                            violation = true;
                        }
                    }
                }
                if (violation) {
                    addViolation(data, ifStatement);
                }
            }
        }
        return super.visit(node, data);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class UnnecessaryLocalBeforeBranchRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<Boolean> STATEMENT_ORDER_MATTERS = booleanProperty("statementOrderMatters")
            .defaultValue(true)
            .desc("If set to false this rule no longer requires the variable declaration and branch statement to be on consecutive lines. Any variable that is used solely in a return or throw statement will be reported.")
            .build();

    public UnnecessaryLocalBeforeBranchRule() {
        super(ASTReturnStatement.class, ASTThrowStatement.class);
        definePropertyDescriptor(STATEMENT_ORDER_MATTERS);
    }

    @Override
    public Object visit(ASTReturnStatement returnStmt, Object data) {
        checkBranchStatement(returnStmt, returnStmt.getExpr(), data);
        return null;
    }

    @Override
    public Object visit(ASTThrowStatement throwStmt, Object data) {
        checkBranchStatement(throwStmt, throwStmt.getExpr(), data);
        return null;
    }

    private void checkBranchStatement(Object branchStmt, ASTExpression expr, Object data) {
        if (!(expr instanceof ASTVariableAccess)) {
            return;
        }

        JVariableSymbol sym = ((ASTVariableAccess) expr).getReferencedSym();
        if (sym == null) {
            return;
        }

        ASTVariableId varDecl = sym.tryGetNode();
        if (varDecl == null || !varDecl.isLocalVariable() || varDecl.getDeclaredAnnotations().nonEmpty()) {
            return;
        }

        if (varDecl.getLocalUsages().size() != 1) {
            return;
        }
        if (!getProperty(STATEMENT_ORDER_MATTERS)
                || varDecl.ancestors(ASTLocalVariableDeclaration.class).firstOrThrow().getNextSibling() == branchStmt) {
            asCtx(data).addViolation(varDecl, varDecl.getName());
        }
    }
}

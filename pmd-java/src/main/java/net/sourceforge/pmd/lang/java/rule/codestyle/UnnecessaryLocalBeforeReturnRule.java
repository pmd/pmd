/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class UnnecessaryLocalBeforeReturnRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> STATEMENT_ORDER_MATTERS = booleanProperty("statementOrderMatters").defaultValue(true).desc("If set to false this rule no longer requires the variable declaration and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be reported.").build();

    public UnnecessaryLocalBeforeReturnRule() {
        super(ASTReturnStatement.class);
        definePropertyDescriptor(STATEMENT_ORDER_MATTERS);
    }

    @Override
    public Object visit(ASTReturnStatement returnStmt, Object data) {
        if (!(returnStmt.getExpr() instanceof ASTVariableAccess)) {
            return null;
        }
        ASTVariableAccess varExpr = (ASTVariableAccess) returnStmt.getExpr();
        JVariableSymbol sym = varExpr.getReferencedSym();
        if (sym == null) {
            return null;
        }

        ASTVariableDeclaratorId varDecl = sym.tryGetNode();
        if (varDecl == null || !varDecl.isLocalVariable() || varDecl.getDeclaredAnnotations().nonEmpty()) {
            return null;
        }

        if (varDecl.getLocalUsages().size() != 1) {
            return null;
        }
        // then this is the only usage

        if (!getProperty(STATEMENT_ORDER_MATTERS)
            || varDecl.ancestors(ASTLocalVariableDeclaration.class).firstOrThrow().getNextSibling() == returnStmt) {
            addViolation(data, varDecl, varDecl.getName());
        }
        return null;
    }
}

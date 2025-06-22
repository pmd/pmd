/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static java.util.Objects.requireNonNull;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class UnnecessaryLocalBeforeThrowRule extends AbstractJavaRulechainRule {

    public UnnecessaryLocalBeforeThrowRule() {
        super(ASTThrowStatement.class);
    }

    @Override
    public Object visit(ASTThrowStatement throwStmt, Object data) {
        ASTExpression expr = throwStmt.getExpr();
        if (expr instanceof ASTVariableAccess) {
            ASTVariableId varDecl = requireNonNull(((ASTVariableAccess) expr).getReferencedSym()).tryGetNode();
            if (requireNonNull(varDecl).isLocalVariable()
                    && !varDecl.getDeclaredAnnotations().nonEmpty()
                    && varDecl.getLocalUsages().size() == 1
                    && varDecl.ancestors(ASTLocalVariableDeclaration.class).firstOrThrow().getNextSibling() == throwStmt) {
                asCtx(data).addViolation(throwStmt, varDecl.getName());
            }
        }
        return null;
    }
}

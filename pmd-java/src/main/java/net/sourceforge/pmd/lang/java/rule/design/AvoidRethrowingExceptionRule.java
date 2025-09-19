/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Avoid rethrowing exceptions unless there's a subsequent catch clause
 * that handles a superclass of the exception being rethrown.
 */
public class AvoidRethrowingExceptionRule extends AbstractJavaRulechainRule {

    public AvoidRethrowingExceptionRule() {
        super(ASTTryStatement.class);
    }

    @Override
    public Object visit(ASTTryStatement tryStmt, Object data) {
        List<ASTCatchClause> catchClauses = tryStmt.getCatchClauses().toList();
        
        for (int i = 0; i < catchClauses.size(); i++) {
            ASTCatchClause currentCatch = catchClauses.get(i);
            
            if (!JavaAstUtils.isJustRethrowException(currentCatch)) {
                continue;
            }

            List<ASTCatchClause> subsequentCatches = catchClauses.subList(i + 1, catchClauses.size());
            if (!hasSubsequentSuperclassCatch(currentCatch, subsequentCatches)) {
                asCtx(data).addViolation(currentCatch);
            }
        }

        return null;
    }

    /**
     * Checks if any subsequent catch clause handles a superclass of the exception being rethrown.
     */
    private boolean hasSubsequentSuperclassCatch(ASTCatchClause currentCatch, List<ASTCatchClause> subsequentCatches) {
        return currentCatch.getParameter().getAllExceptionTypes()
                .any(currentType -> subsequentCatches.stream()
                        .anyMatch(subsequentCatch -> subsequentCatch.getParameter().getAllExceptionTypes()
                                .any(subsequentType -> currentType.getTypeMirror().isSubtypeOf(subsequentType.getTypeMirror()))));
    }
}

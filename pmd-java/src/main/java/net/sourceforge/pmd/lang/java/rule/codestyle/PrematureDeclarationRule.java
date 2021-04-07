/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

/**
 * Checks for variables in methods that are defined before they are really
 * needed. A reference is deemed to be premature if it is created ahead of a
 * block of code that doesn't use it that also has the ability to return or
 * throw an exception.
 *
 * @author Brian Remedios
 */
public class PrematureDeclarationRule extends AbstractJavaRulechainRule {

    public PrematureDeclarationRule() {
        super(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        if (node.getParent() instanceof ASTForInit
            || node.getParent() instanceof ASTResource) {
            // those don't count
            return null;
        }

        for (ASTVariableDeclaratorId id : node) {
            ASTExpression initializer = id.getInitializer();
            if (JavaRuleUtil.isNeverUsed(id) // avoid the duplicate with unused variables
                || JavaRuleUtil.hasSideEffect(initializer)) {
                continue;
            }

            // if there's no initializer then we don't care about side-effects
            boolean hasInitializer = initializer != null;
            for (ASTStatement stmt : statementsAfter(node)) {
                if (hasReferencesIn(stmt, id)
                    || hasInitializer && JavaRuleUtil.hasSideEffect(stmt)) {
                    break;
                }

                if (hasExit(stmt)) {
                    addViolation(data, node, id.getName());
                    break;
                }
            }
        }

        return null;
    }

    /**
     * Returns whether the block contains a return call or throws an exception.
     * Exclude blocks that have these things as part of an inner class.
     */
    private static boolean hasExit(ASTStatement block) {
        return block.descendants()
                    .map(asInstanceOf(ASTThrowStatement.class, ASTReturnStatement.class))
                    .nonEmpty();
    }


    /**
     * Returns whether the variable is mentioned within the statement or not.
     */
    private static boolean hasReferencesIn(ASTStatement stmt, ASTVariableDeclaratorId var) {
        return stmt.descendants(ASTVariableAccess.class)
                   .crossFindBoundaries()
                   .filterMatching(ASTNamedReferenceExpr::getReferencedSym, var.getSymbol())
                   .nonEmpty();
    }

    /** Returns all the statements following the given local var declaration. */
    private static NodeStream<ASTStatement> statementsAfter(ASTLocalVariableDeclaration node) {
        return node.asStream().followingSiblings().filterIs(ASTStatement.class);
    }
}

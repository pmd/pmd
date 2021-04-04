/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

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

        // is it part of a for-loop declaration?
        if (node.getParent() instanceof ASTForInit) {
            // yes, those don't count
            return null;
        }

        for (ASTVariableDeclaratorId id : node) {
            for (ASTStatement block : statementsAfter(node)) {
                if (hasReferencesIn(block, id)) {
                    break;
                }

                if (hasExit(block)) {
                    addViolation(data, node);
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
    private boolean hasExit(ASTStatement block) {
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

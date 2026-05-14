/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;

import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

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

        for (ASTVariableId id : node) {
            ASTExpression initializer = id.getInitializer();

            if (JavaAstUtils.isNeverUsed(id) // avoid the duplicate with unused variables
                || JavaRuleUtil.cannotBeMoved(initializer)
                || JavaRuleUtil.hasSideEffect(initializer, emptySet())) {
                continue;
            }

            Set<JVariableSymbol> refsInInitializer = getReferencedVars(initializer);
            // If there's no initializer, or the initializer doesn't depend on anything (eg, a literal),
            // then we don't care about side-effects
            boolean hasStatefulInitializer = !refsInInitializer.isEmpty() || JavaRuleUtil.hasSideEffect(initializer, emptySet());
            for (ASTStatement stmt : statementsAfter(node)) {
                if (JavaRuleUtil.hasReferencesIn(stmt, id)
                    || hasStatefulInitializer && JavaRuleUtil.hasSideEffect(stmt, refsInInitializer)) {
                    break;
                }

                if (hasExit(stmt)) {
                    asCtx(data).addViolation(node, id.getName());
                    break;
                }
            }
        }

        return null;
    }

    /**
     * Returns the set of local variables referenced inside the expression.
     */
    private static Set<JVariableSymbol> getReferencedVars(ASTExpression term) {
        return term == null ? emptySet()
                            : term.descendantsOrSelf()
                                  .filterIs(ASTNamedReferenceExpr.class)
                                  .filter(it -> it.getReferencedSym() != null)
                                  .collect(Collectors.mapping(ASTNamedReferenceExpr::getReferencedSym, Collectors.toSet()));
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

    /** Returns all the statements following the given local var declaration. */
    private static NodeStream<ASTStatement> statementsAfter(ASTLocalVariableDeclaration node) {
        return node.asStream().followingSiblings().filterIs(ASTStatement.class);
    }
}

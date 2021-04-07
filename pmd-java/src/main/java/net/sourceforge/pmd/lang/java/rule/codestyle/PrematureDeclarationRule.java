/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;
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
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil.InvocationMatcher;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Checks for variables in methods that are defined before they are really
 * needed. A reference is deemed to be premature if it is created ahead of a
 * block of code that doesn't use it that also has the ability to return or
 * throw an exception.
 *
 * @author Brian Remedios
 */
public class PrematureDeclarationRule extends AbstractJavaRulechainRule {

    private static final List<InvocationMatcher> TIME_METHODS =
        listOf(
            InvocationMatcher.parse("java.lang.System#nanoTime()"),
            InvocationMatcher.parse("java.lang.System#currentTimeMillis()")
        );

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
                || cannotBeMoved(initializer)
                || JavaRuleUtil.hasSideEffect(initializer, emptySet())) {
                continue;
            }

            // if there's no initializer then we don't care about side-effects
            Set<JLocalVariableSymbol> refsInInitializer = getReferencedLocals(initializer);
            boolean hasStatefulInitializer = !refsInInitializer.isEmpty() || JavaRuleUtil.hasSideEffect(initializer, emptySet());
            for (ASTStatement stmt : statementsAfter(node)) {
                if (hasReferencesIn(stmt, id)
                    || hasStatefulInitializer && JavaRuleUtil.hasSideEffect(stmt, refsInInitializer)) {
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
     * Returns the set of local variables referenced inside the expression.
     */
    private static Set<JLocalVariableSymbol> getReferencedLocals(ASTExpression term) {
        return term == null ? emptySet()
                            : term.descendantsOrSelf()
                                  .filterIs(ASTVariableAccess.class)
                                  .filter(it -> it.getReferencedSym() instanceof JLocalVariableSymbol)
                                  .collect(Collectors.mapping(it -> (JLocalVariableSymbol) it.getReferencedSym(), Collectors.toSet()));
    }

    /**
     * Time methods cannot be moved ever, even when there are no side-effects.
     * The side effect they depend on is the program being executed. Are they
     * the only methods like that?
     */
    private boolean cannotBeMoved(ASTExpression initializer) {
        return CollectionUtil.any(TIME_METHODS, m -> m.matchesCall(initializer));
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

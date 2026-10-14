/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTEmptyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class AvoidCatchingGenericExceptionRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> TYPES_THAT_SHOULD_NOT_BE_CAUGHT =
        stringListProperty("typesThatShouldNotBeCaught")
            .desc("List of canonical type names that should trigger a violation if used in a catch clause.")
            .defaultValues("java.lang.NullPointerException", "java.lang.Exception", "java.lang.RuntimeException",
                           "java.lang.Throwable", "java.lang.Error")
            .build();

    private static final PropertyDescriptor<Boolean> EXCLUDE_RETHROWN =
        booleanProperty("excludeCasesWhereExceptionIsRethrown")
            .desc("Exclude catch clauses that rethrow the unchanged caught exception on all explicit paths.")
            .defaultValue(false)
            .build();

    // Possible outcomes of a statement. A set of outcomes is represented by their bitwise OR.
    private static final int NORMAL = 1;
    private static final int RETHROW = 2;
    private static final int OTHER = 4;

    public AvoidCatchingGenericExceptionRule() {
        super(ASTCatchClause.class);
        definePropertyDescriptor(TYPES_THAT_SHOULD_NOT_BE_CAUGHT);
        definePropertyDescriptor(EXCLUDE_RETHROWN);
    }

    @Override
    public Object visit(ASTCatchClause node, Object data) {
        if (getProperty(EXCLUDE_RETHROWN) && rethrowsException(node)) {
            return null;
        }
        for (ASTClassType type : node.getParameter().getAllExceptionTypes()) {
            if (getProperty(TYPES_THAT_SHOULD_NOT_BE_CAUGHT).stream().anyMatch(name -> TypeTestUtil.isExactlyA(name, type))) {
                asCtx(data).addViolation(type, type.getSimpleName());
            }
        }
        return null;
    }

    private boolean rethrowsException(ASTCatchClause clause) {
        ASTVariableId parameter = clause.getParameter().getVarId();
        return parameter.getLocalUsages().stream().noneMatch(it -> it.getAccessType() == AccessType.WRITE)
            // Switch expressions may themselves throw or contain other control flow.
            && clause.getBody().descendants(ASTSwitchExpression.class).isEmpty()
            && outcomes(clause.getBody(), parameter.getSymbol()) == RETHROW;
    }

    /**
     * Computes the possible ways in which a statement can complete. NORMAL means that execution
     * can continue with the following statement, RETHROW means that the caught exception is thrown,
     * and OTHER means that execution leaves this statement in another way.
     */
    private static int outcomes(ASTStatement statement, JVariableSymbol exception) {
        if (statement instanceof ASTBlock) {
            return outcomes((ASTBlock) statement, exception);
        } else if (statement instanceof ASTThrowStatement) {
            return JavaAstUtils.isReferenceToVar(((ASTThrowStatement) statement).getExpr(), exception)
                ? RETHROW : OTHER;
        } else if (statement instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) statement;
            int result = outcomes(ifStmt.getThenBranch(), exception);
            return ifStmt.hasElse() ? result | outcomes(ifStmt.getElseBranch(), exception) : result | NORMAL;
        } else if (statement instanceof ASTTryStatement) {
            return outcomes((ASTTryStatement) statement, exception);
        } else if (statement instanceof ASTSynchronizedStatement) {
            return outcomes(((ASTSynchronizedStatement) statement).getBody(), exception);
        } else if (statement instanceof ASTLabeledStatement) {
            return outcomes(((ASTLabeledStatement) statement).getStatement(), exception);
        } else if (statement instanceof ASTExpressionStatement || statement instanceof ASTLocalVariableDeclaration
                || statement instanceof ASTEmptyStatement || statement instanceof ASTLocalClassStatement) {
            // Don't descend into lambdas or local classes: they have independent control flow.
            return NORMAL;
        }
        // Returns, breaks, and continues do not rethrow. Loops and switches are conservatively
        // left reported too, rather than assuming that their body necessarily executes.
        return OTHER;
    }

    private static int outcomes(ASTBlock block, JVariableSymbol exception) {
        int result = 0;
        boolean canContinue = true;
        for (ASTStatement statement : block) {
            if (!canContinue) {
                break;
            }
            int statementOutcomes = outcomes(statement, exception);
            result |= statementOutcomes & (RETHROW | OTHER);
            canContinue = (statementOutcomes & NORMAL) != 0;
        }
        return canContinue ? result | NORMAL : result;
    }

    private static int outcomes(ASTTryStatement tryStmt, JVariableSymbol exception) {
        int result = outcomes(tryStmt.getBody(), exception);
        for (ASTCatchClause catchClause : tryStmt.getCatchClauses()) {
            result |= outcomes(catchClause.getBody(), exception);
        }

        if (tryStmt.getFinallyClause() != null) {
            int finallyOutcomes = outcomes(tryStmt.getFinallyClause().getBody(), exception);
            int abruptFinally = finallyOutcomes & (RETHROW | OTHER);
            result = abruptFinally | ((finallyOutcomes & NORMAL) != 0 ? result : 0);
        }
        return result;
    }
}

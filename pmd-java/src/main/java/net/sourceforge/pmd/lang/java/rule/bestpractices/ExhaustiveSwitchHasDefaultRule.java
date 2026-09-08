/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.10.0 (as XPath) / 7.27.0 (as Java)
 */
public class ExhaustiveSwitchHasDefaultRule extends AbstractJavaRulechainRule {

    static final int MAX_NAMED_CASES = 3;

    public ExhaustiveSwitchHasDefaultRule() {
        super(ASTSwitchExpression.class, ASTSwitchStatement.class);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    private void visitSwitchLike(ASTSwitchLike node, RuleContext ctx) {
        if (node.isExhaustive() && node.hasDefaultCase()) {
            if (!defaultBranchIsNecessary(node)) {
                List<String> missing = missingCases(node);
                if (missing.isEmpty()
                        && node instanceof ASTSwitchExpression
                        && node.getDefaultCase() instanceof ASTSwitchArrowBranch) {
                    ctx.addViolationWithMessage(node,
                            "Remove default case to make sure compiler keeps checking that all cases are present.");
                } else {
                    ctx.addViolation(node, formatMissingCases(missing));
                }
            } else if (!branchJustThrows(node.getDefaultCase())) {
                ctx.addViolationWithMessage(node, "The switch block is exhaustive. The default case should only throw, nothing else.");
            }
        }
    }

    /**
     * returns true iff the only thing this branch does is throw an exception
     */
    // visible for testing
    /* private */ static boolean branchJustThrows(ASTSwitchBranch branch) {
        if (branch instanceof ASTSwitchFallthroughBranch) {
            ASTSwitchFallthroughBranch fallthroughBranch = (ASTSwitchFallthroughBranch) branch;
            return fallthroughBranch.getStatements().count() == 1
                    && fallthroughBranch.getStatements().first() instanceof ASTThrowStatement;
        }
        ASTSwitchArrowBranch arrowBranch = (ASTSwitchArrowBranch) branch;
        if (arrowBranch.getRightHandSide() instanceof ASTThrowStatement) {
            return true;
        }
        if (arrowBranch.getRightHandSide() instanceof ASTBlock) {
            ASTBlock block = (ASTBlock) arrowBranch.getRightHandSide();
            return block.size() == 1 && block.get(0) instanceof ASTThrowStatement;
        }
        return false;
    }

    /**
     * returns true iff the default branch of this switch is necessary to make the code compile.
     * This happens, if you initialize a final variable in your other branches.
     */
    // visible for testing
    /* private */ static boolean defaultBranchIsNecessary(ASTSwitchLike switchLike) {
        return switchLike.descendants(ASTAssignableExpr.ASTNamedReferenceExpr.class)
                .filter(expr -> expr.getReferencedSym() != null)
                .filter(expr -> expr.getReferencedSym().isFinal())
                .any(JavaAstUtils::isVarAccessStrictlyWrite);
    }

    // visible for testing
    /* private */ static List<String> missingCases(ASTSwitchLike node) {
        JTypeDeclSymbol symbol = node.getTestedExpression().getTypeMirror().getSymbol();
        if (!(symbol instanceof JClassSymbol)) {
            return Collections.emptyList();
        }
        JClassSymbol classSymbol = (JClassSymbol) symbol;
        Set<String> covered = coveredCaseNames(node);
        List<String> missing = new ArrayList<>();
        if (classSymbol.isEnum()) {
            for (JFieldSymbol constant : classSymbol.getEnumConstants()) {
                if (!covered.contains(constant.getSimpleName())) {
                    missing.add(constant.getSimpleName());
                }
            }
        } else if (classSymbol.isSealed()) {
            for (JClassSymbol subtype : classSymbol.getPermittedSubtypes()) {
                if (!covered.contains(subtype.getSimpleName())) {
                    missing.add(subtype.getSimpleName());
                }
            }
        }
        Collections.sort(missing);
        return missing;
    }

    private static Set<String> coveredCaseNames(ASTSwitchLike node) {
        Set<String> names = new HashSet<>();
        for (ASTSwitchBranch branch : node.getBranches()) {
            for (ASTExpression expr : branch.getLabel().getExprList()) {
                if (expr instanceof ASTNullLiteral) {
                    continue;
                }
                if (expr instanceof ASTVariableAccess) {
                    names.add(((ASTVariableAccess) expr).getName());
                } else if (expr instanceof ASTFieldAccess) {
                    names.add(((ASTFieldAccess) expr).getName());
                }
            }
            for (ASTTypePattern pattern : branch.getLabel().children(ASTTypePattern.class)) {
                JTypeDeclSymbol typeSym = pattern.getTypeNode().getTypeMirror().getSymbol();
                if (typeSym instanceof JClassSymbol) {
                    names.add(typeSym.getSimpleName());
                }
            }
        }
        return names;
    }

    // visible for testing
    /* private */ static String formatMissingCases(List<String> names) {
        if (names.isEmpty()) {
            return "";
        }
        boolean truncated = names.size() > MAX_NAMED_CASES;
        List<String> shown = truncated ? names.subList(0, MAX_NAMED_CASES) : names;
        String joined = String.join(", ", shown);
        if (truncated) {
            joined += ", ...";
        }
        return " (" + joined + ")";
    }
}

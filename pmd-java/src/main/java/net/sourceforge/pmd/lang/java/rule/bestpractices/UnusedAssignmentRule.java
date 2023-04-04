/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnusedAssignmentRule extends AbstractJavaRulechainRule {

    /*
        Detects unused assignments. This performs a reaching definition
        analysis. This makes the assumption that there is no dead code.

        Since we have the reaching definitions at each variable usage, we
        could also use that to detect other kinds of bug, eg conditions
        that are always true, or dereferences that will always NPE. In
        the general case though, this is complicated and better left to
        a DFA library, eg google Z3.

        This analysis may be used as-is to detect switch labels that
        fall-through, which could be useful to improve accuracy of other
        rules.

        TODO
           * labels on arbitrary statements (currently only loops)
           * explicit ctor call (hard to impossible without type res,
             or at least proper graph algorithms like toposort)
                -> this is pretty invisible as it causes false negatives, not FPs
           * test ternary expr
           * more precise exception handling: since we have access to
             the overload for method & ctors, we can know where its thrown
             exceptions may end up in enclosing catches.
           * extract the reaching definition analysis, to exploit control
           flow information in rules + symbol table. The following are needed
           to implement scoping of pattern variables, and are already computed
           by this analysis:
             * whether a switch may fall through
             * whether a statement always completes abruptly
             * whether a statement never completes abruptly because of break


        DONE
           * conditionals
           * loops
           * switch
           * loop labels
           * try/catch/finally
           * lambdas
           * constructors + initializers
           * anon class
           * test this.field in ctors
           * foreach var should be reassigned from one iter to another
           * test local class/anonymous class
           * shortcut conditionals have their own control-flow
           * parenthesized expressions
           * conditional exprs in loops
           * ignore variables that start with 'ignore'
           * ignore params of native methods
           * ignore params of abstract methods

     */

    private static final PropertyDescriptor<Boolean> CHECK_PREFIX_INCREMENT =
        PropertyFactory.booleanProperty("checkUnusedPrefixIncrement")
                       .desc("Report expressions like ++i that may be replaced with (i + 1)")
                       .defaultValue(false)
                       .build();

    private static final PropertyDescriptor<Boolean> REPORT_UNUSED_VARS =
        PropertyFactory.booleanProperty("reportUnusedVariables")
                       .desc("Report variables that are only initialized, and never read at all. "
                                 + "The rule UnusedVariable already cares for that, but you can enable it if needed")
                       .defaultValue(false)
                       .build();

    public UnusedAssignmentRule() {
        super(ASTCompilationUnit.class);
        definePropertyDescriptor(CHECK_PREFIX_INCREMENT);
        definePropertyDescriptor(REPORT_UNUSED_VARS);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        DataflowResult result = DataflowPass.getDataflowResult(node);
        reportFinished(result, (RuleContext) data);
        return data;
    }

    private void reportFinished(DataflowResult result, RuleContext ruleCtx) {

        for (AssignmentEntry entry : result.getUnusedAssignments()) {
            if (entry.isUnaryReassign() && isIgnorablePrefixIncrement(entry.getLocation())) {
                continue;
            }

            Set<AssignmentEntry> killers = result.getKillers(entry);
            final String reason;
            if (killers.isEmpty()) {
                // var went out of scope before being used (no assignment kills it, yet it's unused)

                if (entry.isField()) {
                    // assignments to fields don't really go out of scope
                    continue;
                } else if (suppressUnusedVariableRuleOverlap(entry)) {
                    // see REPORT_UNUSED_VARS property
                    continue;
                }
                // This is a "DU" anomaly, the others are "DD"
                reason = null;
            } else if (killers.size() == 1) {
                AssignmentEntry k = killers.iterator().next();
                if (k.getLocation().equals(entry.getLocation())) {
                    // assignment reassigns itself, only possible in a loop
                    if (suppressUnusedVariableRuleOverlap(entry)) {
                        continue;
                    } else if (entry.isForeachVar()) {
                        reason = null;
                    } else {
                        reason = "reassigned every iteration";
                    }
                } else {
                    reason = "overwritten on line " + k.getLine();
                }
            } else {
                reason = joinLines("overwritten on lines ", killers);
            }
            if (reason == null && JavaRuleUtil.isExplicitUnusedVarName(entry.getVarId().getName())) {
                // Then the variable is never used (cf UnusedVariable)
                // We ignore those that start with "ignored", as that is standard
                // practice for exceptions, and may be useful for resources/foreach vars
                continue;
            }
            addViolationWithMessage(ruleCtx, entry.getLocation(), makeMessage(entry, reason, entry.isField()));
        }
    }

    private boolean suppressUnusedVariableRuleOverlap(AssignmentEntry entry) {
        return !getProperty(REPORT_UNUSED_VARS) && (entry.isInitializer() || entry.isBlankDeclaration());
    }

    private static String getKind(ASTVariableDeclaratorId id) {
        if (id.isField()) {
            return "field";
        } else if (id.isResourceDeclaration()) {
            return "resource";
        } else if (id.isExceptionBlockParameter()) {
            return "exception parameter";
        } else if (id.getNthParent(3) instanceof ASTForeachStatement) {
            return "loop variable";
        } else if (id.isFormalParameter()) {
            return "parameter";
        }
        return "variable";
    }

    private boolean isIgnorablePrefixIncrement(JavaNode assignment) {
        if (assignment instanceof ASTUnaryExpression) {
            // the variable value is used if it was found somewhere else
            // than in statement position
            UnaryOp op = ((ASTUnaryExpression) assignment).getOperator();
            return !getProperty(CHECK_PREFIX_INCREMENT) && !op.isPure() && op.isPrefix()
                && !(assignment.getParent() instanceof ASTExpressionStatement);
        }
        return false;
    }

    private static String makeMessage(AssignmentEntry assignment, @Nullable String reason, boolean isField) {
        // if reason is null, then the variable is unused (at most assigned to)

        StringBuilder result = new StringBuilder(64);
        if (assignment.isInitializer()) {
            result.append(isField ? "the field initializer for"
                                  : "the initializer for variable");
        } else if (assignment.isBlankDeclaration()) {
            if (reason != null) {
                result.append("the initial value of ");
            }
            result.append(getKind(assignment.getVarId()));
        } else { // regular assignment
            if (assignment.isUnaryReassign()) {
                result.append("the updated value of ");
            } else {
                result.append("the value assigned to ");
            }
            result.append(isField ? "field" : "variable");
        }
        result.append(" ''").append(assignment.getVarId().getName()).append("''");
        result.append(" is never used");
        if (reason != null) {
            result.append(" (").append(reason).append(")");
        }
        result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        return result.toString();
    }

    private static String joinLines(String prefix, Set<AssignmentEntry> killers) {
        StringBuilder sb = new StringBuilder(prefix);
        List<AssignmentEntry> sorted = new ArrayList<>(killers);
        sorted.sort(Comparator.naturalOrder());

        sb.append(sorted.get(0).getLine());
        for (int i = 1; i < sorted.size() - 1; i++) {
            sb.append(", ").append(sorted.get(i).getLine());
        }
        sb.append(" and ").append(sorted.get(sorted.size() - 1).getLine());

        return sb.toString();
    }

}

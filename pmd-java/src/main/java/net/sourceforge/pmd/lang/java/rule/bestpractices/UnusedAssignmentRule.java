/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;


import static net.sourceforge.pmd.lang.java.rule.codestyle.ConfusingTernaryRule.unwrapParentheses;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledRule;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnusedAssignmentRule extends AbstractJavaRule {

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
        definePropertyDescriptor(CHECK_PREFIX_INCREMENT);
        definePropertyDescriptor(REPORT_UNUSED_VARS);
        addRuleChainVisit(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        for (JavaNode child : node.children()) {
            if (child instanceof ASTTypeDeclaration) {

                ASTAnyTypeDeclaration typeDecl = (ASTAnyTypeDeclaration) child.getChild(child.getNumChildren() - 1);
                GlobalAlgoState result = new GlobalAlgoState();
                typeDecl.jjtAccept(ReachingDefsVisitor.ONLY_LOCALS, new SpanInfo(result));

                reportFinished(result, (RuleContext) data);
            }
        }

        return data;
    }

    private void reportFinished(GlobalAlgoState result, RuleContext ruleCtx) {
        if (result.usedAssignments.size() < result.allAssignments.size()) {
            Set<AssignmentEntry> unused = result.allAssignments;
            // note that this mutates allAssignments, so the global
            // state is unusable after this
            unused.removeAll(result.usedAssignments);

            for (AssignmentEntry entry : unused) {
                if (isIgnorablePrefixIncrement(entry.rhs)) {
                    continue;
                }

                Set<AssignmentEntry> killers = result.killRecord.get(entry);
                final String reason;
                if (killers == null || killers.isEmpty()) {
                    // var went out of scope before being used (no assignment kills it, yet it's unused)

                    if (entry.var.isField()) {
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
                    if (k.rhs.equals(entry.rhs)) {
                        // assignment reassigns itself, only possible in a loop
                        if (suppressUnusedVariableRuleOverlap(entry)) {
                            continue;
                        } else if (entry.rhs instanceof ASTVariableDeclaratorId) {
                            reason = null; // unused foreach variable
                        } else {
                            reason = "reassigned every iteration";
                        }
                    } else {
                        reason = "overwritten on line " + k.rhs.getBeginLine();
                    }
                } else {
                    reason = joinLines("overwritten on lines ", killers);
                }
                if (reason == null && JavaRuleUtil.isExplicitUnusedVarName(entry.var.getName())) {
                    // Then the variable is never used (cf UnusedVariable)
                    // We ignore those that start with "ignored", as that is standard
                    // practice for exceptions, and may be useful for resources/foreach vars
                    continue;
                }
                addViolationWithMessage(ruleCtx, entry.rhs, makeMessage(entry, reason, entry.var.isField()));
            }
        }
    }

    private boolean suppressUnusedVariableRuleOverlap(AssignmentEntry entry) {
        return !getProperty(REPORT_UNUSED_VARS) && (entry.rhs instanceof ASTVariableInitializer
            || entry.rhs instanceof ASTVariableDeclaratorId);
    }

    private static String getKind(ASTVariableDeclaratorId id) {
        if (id.isField()) {
            return "field";
        } else if (id.isResourceDeclaration()) {
            return "resource";
        } else if (id.isExceptionBlockParameter()) {
            return "exception parameter";
        } else if (id.getNthParent(3) instanceof ASTForStatement) {
            return "loop variable";
        } else if (id.isFormalParameter()) {
            return "parameter";
        }
        return "variable";
    }

    private boolean isIgnorablePrefixIncrement(JavaNode assignment) {
        if (assignment instanceof ASTPreIncrementExpression
            || assignment instanceof ASTPreDecrementExpression) {
            // the variable value is used if it was found somewhere else
            // than in statement position
            return !getProperty(CHECK_PREFIX_INCREMENT) && !(assignment.getParent() instanceof ASTStatementExpression);
        }
        return false;
    }

    private static String makeMessage(AssignmentEntry assignment, /* Nullable */ String reason, boolean isField) {
        // if reason is null, then the variable is unused (at most assigned to)

        String varName = assignment.var.getName();
        StringBuilder result = new StringBuilder(64);
        if (assignment.rhs instanceof ASTVariableInitializer) {
            result.append(isField ? "the field initializer for"
                                  : "the initializer for variable");
        } else if (assignment.rhs instanceof ASTVariableDeclaratorId) {
            if (reason != null) {
                result.append("the initial value of ");
            }
            result.append(getKind(assignment.var));
        } else {
            if (assignment.rhs instanceof ASTPreIncrementExpression
                || assignment.rhs instanceof ASTPreDecrementExpression
                || assignment.rhs instanceof ASTPostfixExpression) {
                result.append("the updated value of ");
            } else {
                result.append("the value assigned to ");
            }
            result.append(isField ? "field" : "variable");
        }
        result.append(" ''").append(varName).append("''");
        result.append(" is never used");
        if (reason != null) {
            result.append(" (").append(reason).append(")");
        }
        result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        return result.toString();
    }

    private static String joinLines(String prefix, Set<AssignmentEntry> killers) {
        StringBuilder sb = new StringBuilder(prefix);
        ArrayList<AssignmentEntry> sorted = new ArrayList<>(killers);
        Collections.sort(sorted, new Comparator<AssignmentEntry>() {
            @Override
            public int compare(AssignmentEntry o1, AssignmentEntry o2) {
                int lineRes = Integer.compare(o1.rhs.getBeginLine(), o2.rhs.getBeginLine());
                return lineRes != 0 ? lineRes
                                    : Integer.compare(o1.rhs.getBeginColumn(), o2.rhs.getBeginColumn());
            }
        });

        sb.append(sorted.get(0).rhs.getBeginLine());
        for (int i = 1; i < sorted.size() - 1; i++) {
            sb.append(", ").append(sorted.get(i).rhs.getBeginLine());
        }
        sb.append(" and ").append(sorted.get(sorted.size() - 1).rhs.getBeginLine());

        return sb.toString();
    }

    private static class ReachingDefsVisitor extends JavaParserVisitorAdapter {


        static final ReachingDefsVisitor ONLY_LOCALS = new ReachingDefsVisitor(null);

        // The class scope for the "this" reference, used to find fields
        // of this class
        // null if we're not processing instance/static initializers,
        // so in methods we don't care about fields
        // If not null, fields are effectively treated as locals
        private final ClassScope enclosingClassScope;

        private ReachingDefsVisitor(ClassScope scope) {
            this.enclosingClassScope = scope;
        }


        // following deals with control flow structures

        @Override
        public Object visit(JavaNode node, Object data) {

            for (JavaNode child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                data = child.jjtAccept(this, data);
            }

            return data;
        }

        @Override
        public Object visit(ASTBlock node, final Object data) {
            // variables local to a loop iteration must be killed before the
            // next iteration

            SpanInfo state = (SpanInfo) data;
            Set<ASTVariableDeclaratorId> localsToKill = new HashSet<>();

            for (JavaNode child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                state = acceptOpt(child, state);
                if (child instanceof ASTBlockStatement
                    && child.getChild(0) instanceof ASTLocalVariableDeclaration) {
                    ASTLocalVariableDeclaration local = (ASTLocalVariableDeclaration) child.getChild(0);
                    for (ASTVariableDeclaratorId id : local) {
                        localsToKill.add(id);
                    }
                }
            }

            for (ASTVariableDeclaratorId var : localsToKill) {
                state.deleteVar(var);
            }

            return state;
        }

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            return processSwitch(node, (SpanInfo) data, node.getTestedExpression());
        }

        @Override
        public Object visit(ASTSwitchExpression node, Object data) {
            return processSwitch(node, (SpanInfo) data, node.getChild(0));
        }

        private SpanInfo processSwitch(JavaNode switchLike, SpanInfo data, JavaNode testedExpr) {
            GlobalAlgoState global = data.global;
            SpanInfo before = acceptOpt(testedExpr, data);

            global.breakTargets.push(before.fork());

            SpanInfo current = before;
            for (int i = 1; i < switchLike.getNumChildren(); i++) {
                JavaNode child = switchLike.getChild(i);
                if (child instanceof ASTSwitchLabel) {
                    current = before.fork().absorb(current);
                } else if (child instanceof ASTSwitchLabeledRule) {
                    current = acceptOpt(child.getChild(1), before.fork());
                    current = global.breakTargets.doBreak(current, null); // process this as if it was followed by a break
                } else {
                    // statement in a regular fallthrough switch block
                    current = acceptOpt(child, current);
                }
            }

            before = global.breakTargets.pop();

            // join with the last state, which is the exit point of the
            // switch, if it's not closed by a break;
            return before.absorb(current);
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            SpanInfo before = (SpanInfo) data;
            return makeConditional(before, node.getCondition(), node.getThenBranch(), node.getElseBranch());
        }

        @Override
        public Object visit(ASTConditionalExpression node, Object data) {
            SpanInfo before = (SpanInfo) data;
            return makeConditional(before, node.getCondition(), node.getChild(1), node.getChild(2));
        }

        // This will be much easier with the 7.0 grammar.....
        SpanInfo makeConditional(SpanInfo before, JavaNode condition, JavaNode thenBranch, JavaNode elseBranch) {
            SpanInfo thenState = before.fork();
            SpanInfo elseState = elseBranch != null ? before.fork() : before;

            linkConditional(before, condition, thenState, elseState, true);

            thenState = acceptOpt(thenBranch, thenState);
            elseState = acceptOpt(elseBranch, elseState);

            return elseState.absorb(thenState);
        }

        /*
         * This recursive procedure translates shortcut conditionals
         * that occur in condition position in the following way:
         *
         * if (a || b) <then>                  if (a)      <then>
         * else <else>               ~>        else
         *                                       if (b)    <then>
         *                                       else      <else>
         *
         *
         * if (a && b) <then>                  if (a)
         * else <else>               ~>          if (b)    <then>
         *                                       else      <else>
         *                                     else        <else>
         *
         * The new conditions are recursively processed to translate
         * bigger conditions, like `a || b && c`
         *
         * This is how it works, but the <then> and <else> branch are
         * visited only once, because it's not done in this method, but
         * in makeConditional.
         *
         * @return the state in which all expressions have been evaluated
         *      Eg for `a || b`, this is the `else` state (all evaluated to false)
         *      Eg for `a && b`, this is the `then` state (all evaluated to true)
         *
         */
        private SpanInfo linkConditional(SpanInfo before, JavaNode condition, SpanInfo thenState, SpanInfo elseState, boolean isTopLevel) {
            if (condition == null) {
                return before;
            }
            condition = unwrapParentheses(condition);

            if (condition instanceof ASTConditionalOrExpression) {
                return visitShortcutOrExpr(condition, before, thenState, elseState);
            } else if (condition instanceof ASTConditionalAndExpression) {
                // To mimic a shortcut AND expr, swap the thenState and the elseState
                // See explanations in method
                return visitShortcutOrExpr(condition, before, elseState, thenState);
            } else if (condition instanceof ASTExpression && condition.getNumChildren() == 1) {
                return linkConditional(before, condition.getChild(0), thenState, elseState, isTopLevel);
            } else {
                SpanInfo state = acceptOpt(condition, before);
                if (isTopLevel) {
                    thenState.absorb(state);
                    elseState.absorb(state);
                }
                return state;
            }
        }

        SpanInfo visitShortcutOrExpr(JavaNode orExpr,
                                     SpanInfo before,
                                     SpanInfo thenState,
                                     SpanInfo elseState) {

            //  if (<a> || <b> || ... || <n>) <then>
            //  else <else>
            //
            // in <then>, we are sure that at least <a> was evaluated,
            // but really any prefix of <a> ... <n> is possible so they're all merged

            // in <else>, we are sure that all of <a> ... <n> were evaluated (to false)

            // If you replace || with &&, then the above holds if you swap <then> and <else>
            // So this method handles the OR expr, the caller can swap the arguments to make an AND

            // ---
            // This method side effects on thenState and elseState to
            // set the variables.

            Iterator<? extends JavaNode> iterator = orExpr.children().iterator();

            SpanInfo cur = before;
            do {
                JavaNode cond = iterator.next();
                cur = linkConditional(cur, cond, thenState, elseState, false);
                thenState.absorb(cur);
            } while (iterator.hasNext());

            elseState.absorb(cur);

            return cur;
        }


        @Override
        public Object visit(ASTTryStatement node, Object data) {
            final SpanInfo before = (SpanInfo) data;
            ASTFinallyStatement finallyClause = node.getFinallyClause();

            /*
                <before>
                try (<resources>) {
                    <body>
                } catch (IOException e) {
                    <catch>
                } finally {
                    <finally>
                }
                <end>

                There is a path      <before> -> <resources> -> <body> -> <finally> -> <end>
                and for each catch,  <before> -> <catch> -> <finally> -> <end>

                Except that abrupt completion before the <finally> jumps
                to the <finally> and completes abruptly for the same
                reason (if the <finally> completes normally), which
                means it doesn't go to <end>
             */

            if (finallyClause != null) {
                before.myFinally = before.forkEmpty();
            }

            final List<ASTCatchStatement> catchClauses = node.getCatchClauses();
            final List<SpanInfo> catchSpans = catchClauses.isEmpty() ? Collections.<SpanInfo>emptyList()
                                                                     : new ArrayList<SpanInfo>();

            // pre-fill catch spans
            for (int i = 0; i < catchClauses.size(); i++) {
                catchSpans.add(before.forkEmpty());
            }

            ASTResourceSpecification resources = node.getFirstChildOfType(ASTResourceSpecification.class);

            SpanInfo bodyState = before.fork();
            bodyState = bodyState.withCatchBlocks(catchSpans);
            bodyState = acceptOpt(resources, bodyState);
            bodyState = acceptOpt(node.getBody(), bodyState);
            bodyState = bodyState.withCatchBlocks(Collections.<SpanInfo>emptyList());

            SpanInfo exceptionalState = null;
            for (int i = 0; i < catchClauses.size(); i++) {
                ASTCatchStatement catchClause = catchClauses.get(i);

                SpanInfo current = acceptOpt(catchClause, catchSpans.get(i));
                exceptionalState = current.absorb(exceptionalState);
            }

            SpanInfo finalState;
            finalState = bodyState.absorb(exceptionalState);
            if (finallyClause != null) {
                // this represents the finally clause when it was entered
                // because of abrupt completion
                // since we don't know when it terminated we must join it with before
                SpanInfo abruptFinally = before.myFinally.absorb(before);
                acceptOpt(finallyClause, abruptFinally);
                before.myFinally = null;
                abruptFinally.abruptCompletionByThrow(false); // propagate to enclosing catch/finallies

                // this is the normal finally
                finalState = acceptOpt(finallyClause, finalState);
            }

            // In the 7.0 grammar, the resources should be explicitly
            // used here. For now they don't trigger anything as their
            // node is not a VariableDeclaratorId. There's a test to
            // check that.

            return finalState;
        }

        @Override
        public Object visit(ASTCatchStatement node, Object data) {
            SpanInfo result = (SpanInfo) visit((JavaNode) node, data);
            result.deleteVar(node.getExceptionId());
            return result;
        }

        @Override
        public Object visit(ASTLambdaExpression node, Object data) {
            // Lambda expression have control flow that is separate from the method
            // So we fork the context, but don't join it

            // Reaching definitions of the enclosing context still reach in the lambda
            // Since those definitions are [effectively] final, they actually can't be
            // killed, but they can be used in the lambda

            SpanInfo before = (SpanInfo) data;

            JavaNode lambdaBody = node.getChild(node.getNumChildren() - 1);
            // if it's an expression, then no assignments may occur in it,
            // but it can still use some variables of the context
            acceptOpt(lambdaBody, before.forkCapturingNonLocal());
            return before;
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            return handleLoop(node, (SpanInfo) data, null, node.getCondition(), null, node.getBody(), true, null);
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            return handleLoop(node, (SpanInfo) data, null, node.getCondition(), null, node.getBody(), false, null);
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ASTStatement body = node.getBody();
            if (node.isForeach()) {
                // the iterable expression
                JavaNode init = node.getChild(1);
                ASTVariableDeclaratorId foreachVar = ((ASTLocalVariableDeclaration) node.getChild(0)).iterator().next();
                return handleLoop(node, (SpanInfo) data, init, null, null, body, true, foreachVar);
            } else {
                ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
                ASTExpression cond = node.getCondition();
                ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
                return handleLoop(node, (SpanInfo) data, init, cond, update, body, true, null);
            }
        }


        private SpanInfo handleLoop(JavaNode loop,
                                    SpanInfo before,
                                    JavaNode init,
                                    JavaNode cond,
                                    JavaNode update,
                                    JavaNode body,
                                    boolean checkFirstIter,
                                    ASTVariableDeclaratorId foreachVar) {
            final GlobalAlgoState globalState = before.global;

            SpanInfo breakTarget = before.forkEmpty();
            SpanInfo continueTarget = before.forkEmpty();
            pushTargets(loop, breakTarget, continueTarget);

            // perform a few "iterations", to make sure that assignments in
            // the body can affect themselves in the next iteration, and
            // that they affect the condition, etc

            before = acceptOpt(init, before);
            if (checkFirstIter && cond != null) { // false for do-while
                SpanInfo ifcondTrue = before.forkEmpty();
                linkConditional(before, cond, ifcondTrue, breakTarget, true);
                before = ifcondTrue;
            }

            if (foreachVar != null) {
                // in foreach loops, the loop variable is assigned before the first iteration
                before.assign(foreachVar, foreachVar);
            }


            // make the defs of the body reach the other parts of the loop,
            // including itself
            SpanInfo iter = acceptOpt(body, before.fork());

            if (foreachVar != null && iter.hasVar(foreachVar)) {
                // in foreach loops, the loop variable is reassigned on each update
                iter.assign(foreachVar, foreachVar);
            } else {
                iter = acceptOpt(update, iter);
            }

            linkConditional(iter, cond, iter, breakTarget, true);
            iter = acceptOpt(body, iter);


            breakTarget = globalState.breakTargets.peek();
            continueTarget = globalState.continueTargets.peek();
            if (!continueTarget.symtable.isEmpty()) {
                // make assignments before a continue reach the other parts of the loop

                linkConditional(continueTarget, cond, continueTarget, breakTarget, true);

                continueTarget = acceptOpt(body, continueTarget);
                continueTarget = acceptOpt(update, continueTarget);
            }

            SpanInfo result = popTargets(loop, breakTarget, continueTarget);
            result = result.absorb(iter);
            if (checkFirstIter) {
                // if the first iteration is checked,
                // then it could be false on the first try, meaning
                // the definitions before the loop reach after too
                result = result.absorb(before);
            }

            if (foreachVar != null) {
                result.deleteVar(foreachVar);
            }

            return result;
        }

        private void pushTargets(JavaNode loop, SpanInfo breakTarget, SpanInfo continueTarget) {
            GlobalAlgoState globalState = breakTarget.global;
            globalState.breakTargets.unnamedTargets.push(breakTarget);
            globalState.continueTargets.unnamedTargets.push(continueTarget);

            Node parent = loop.getNthParent(2);
            while (parent instanceof ASTLabeledStatement) {
                String label = parent.getImage();
                globalState.breakTargets.namedTargets.put(label, breakTarget);
                globalState.continueTargets.namedTargets.put(label, continueTarget);
                parent = parent.getNthParent(2);
            }
        }

        private SpanInfo popTargets(JavaNode loop, SpanInfo breakTarget, SpanInfo continueTarget) {
            GlobalAlgoState globalState = breakTarget.global;
            globalState.breakTargets.unnamedTargets.pop();
            globalState.continueTargets.unnamedTargets.pop();

            SpanInfo total = breakTarget.absorb(continueTarget);

            Node parent = loop.getNthParent(2);
            while (parent instanceof ASTLabeledStatement) {
                String label = parent.getImage();
                total = total.absorb(globalState.breakTargets.namedTargets.remove(label));
                total = total.absorb(globalState.continueTargets.namedTargets.remove(label));
                parent = parent.getNthParent(2);
            }
            return total;
        }

        private SpanInfo acceptOpt(JavaNode node, SpanInfo before) {
            return node == null ? before : (SpanInfo) node.jjtAccept(this, before);
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            SpanInfo state = (SpanInfo) data;
            return state.global.continueTargets.doBreak(state, node.getImage());
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            SpanInfo state = (SpanInfo) data;
            return state.global.breakTargets.doBreak(state, node.getImage());
        }

        @Override
        public Object visit(ASTYieldStatement node, Object data) {
            super.visit(node, data); // visit expression

            SpanInfo state = (SpanInfo) data;
            // treat as break, ie abrupt completion + link reaching defs to outer context
            return state.global.breakTargets.doBreak(state, null);
        }


        // both of those exit the scope of the method/ctor, so their assignments go dead

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            super.visit(node, data);
            return ((SpanInfo) data).abruptCompletionByThrow(false);
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            super.visit(node, data);
            return ((SpanInfo) data).abruptCompletion(null);
        }

        // following deals with assignment

        @Override
        public Object visit(ASTFormalParameter node, Object data) {
            if (!node.isExplicitReceiverParameter()) {
                ASTVariableDeclaratorId id = node.getVariableDeclaratorId();
                ((SpanInfo) data).assign(id, id);
            }
            return data;
        }

        @Override
        public Object visit(ASTVariableDeclarator node, Object data) {
            ASTVariableDeclaratorId var = node.getVariableId();
            ASTVariableInitializer rhs = node.getInitializer();
            if (rhs != null) {
                rhs.jjtAccept(this, data);
                ((SpanInfo) data).assign(var, rhs);
            } else {
                ((SpanInfo) data).assign(var, node.getVariableId());
            }
            return data;
        }


        @Override
        public Object visit(ASTExpression node, Object data) {
            return checkAssignment(node, data);
        }

        @Override
        public Object visit(ASTStatementExpression node, Object data) {
            return checkAssignment(node, data);
        }

        public Object checkAssignment(JavaNode node, Object data) {
            SpanInfo result = (SpanInfo) data;
            if (node.getNumChildren() == 3) {
                // assignment
                assert node.getChild(1) instanceof ASTAssignmentOperator;

                // visit the rhs as it is evaluated before
                JavaNode rhs = node.getChild(2);
                result = acceptOpt(rhs, result);

                ASTVariableDeclaratorId lhsVar = getVarFromExpression(node.getChild(0), true, result);
                if (lhsVar != null) {
                    // in that case lhs is a normal variable (array access not supported)

                    if (node.getChild(1).getImage().length() >= 2) {
                        // compound assignment, to use BEFORE assigning
                        result.use(lhsVar);
                    }

                    result.assign(lhsVar, rhs);
                } else {
                    result = acceptOpt(node.getChild(0), result);
                }
                return result;
            } else {
                return visit(node, data);
            }
        }

        @Override
        public Object visit(ASTPreDecrementExpression node, Object data) {
            return checkIncOrDecrement(node, (SpanInfo) data);
        }

        @Override
        public Object visit(ASTPreIncrementExpression node, Object data) {
            return checkIncOrDecrement(node, (SpanInfo) data);
        }

        @Override
        public Object visit(ASTPostfixExpression node, Object data) {
            return checkIncOrDecrement(node, (SpanInfo) data);
        }

        private SpanInfo checkIncOrDecrement(JavaNode unary, SpanInfo data) {
            ASTVariableDeclaratorId var = getVarFromExpression(unary.getChild(0), true, data);
            if (var != null) {
                data.use(var);
                data.assign(var, unary);
            }
            return data;
        }

        // variable usage

        @Override
        public Object visit(ASTPrimaryExpression node, Object data) {
            SpanInfo state = (SpanInfo) visit((JavaNode) node, data); // visit subexpressions

            ASTVariableDeclaratorId var = getVarFromExpression(node, false, state);
            if (var != null) {
                state.use(var);
            }

            maybeThrowUncheckedExceptions(node, state);

            return state;
        }

        private void maybeThrowUncheckedExceptions(ASTPrimaryExpression e, SpanInfo state) {
            // Note that this doesn't really respect the order of evaluation of subexpressions
            // This can be easily fixed in the 7.0 tree, but this is rare enough to not deserve
            // the effort on master.

            // For the record this has problems with call chains with side effects, like
            //  a.foo(a = 2).bar(a = 3);

            // In 7.0, with the precise type/overload resolution, we
            // could only target methods that throw checked exceptions
            // (unless some catch block catches an unchecked exceptions)
            for (JavaNode child : e.children()) {
                if (child instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) child).isArguments()
                    || child instanceof ASTPrimarySuffix && child.getNumChildren() > 0 && child.getChild(0) instanceof ASTAllocationExpression
                    || child instanceof ASTPrimaryPrefix && child.getNumChildren() > 0 && child.getChild(0) instanceof ASTAllocationExpression) {
                    state.abruptCompletionByThrow(true); // this is a noop if we're outside a try block that has catch/finally
                }
            }
        }

        /**
         * Get the variable accessed from a primary.
         */
        private ASTVariableDeclaratorId getVarFromExpression(JavaNode primary, boolean inLhs, SpanInfo state) {

            if (primary instanceof ASTPrimaryExpression) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primary.getChild(0);


                //   this.x = 2;
                if (prefix.usesThisModifier() && this.enclosingClassScope != null) {
                    int numChildren = primary.getNumChildren();
                    if (numChildren < 2 || numChildren > 2 && inLhs) {
                        if (numChildren == 3 || numChildren == 1) {
                            // method call on this, or just bare `this` reference
                            state.recordThisLeak(true, enclosingClassScope);
                        }
                        return null;
                    }

                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) primary.getChild(1);
                    if (suffix.getImage() == null) {
                        return null;
                    } else if (primary.getNumChildren() > 2 && ((ASTPrimarySuffix) primary.getChild(2)).isArguments()) {
                        //     this.foo()
                        // first suffix is the name, second is the arguments
                        state.recordThisLeak(true, enclosingClassScope);
                        return null;
                    }

                    return findVar(primary.getScope(), true, suffix.getImage());
                } else {
                    if (prefix.getNumChildren() > 0 && prefix.getChild(0) instanceof ASTName) {
                        String prefixImage = prefix.getChild(0).getImage();
                        String varname = identOf(inLhs, prefixImage);
                        if (primary.getNumChildren() > 1) {
                            if (primary.getNumChildren() > 2 && inLhs) {
                                // this is for chains like `foo.m().field = 3`
                                return null;
                            }
                            ASTPrimarySuffix suffix = (ASTPrimarySuffix) primary.getChild(1);
                            if (suffix.isArguments()) {
                                // then the prefix has the method name
                                varname = methodLhsName(prefixImage);
                            } else if (suffix.isArrayDereference() && inLhs) {
                                return null;
                            }
                        }
                        return findVar(prefix.getScope(), false, varname);
                    }
                }
            }

            return null;
        }

        private static String identOf(boolean inLhs, String str) {
            int i = str.indexOf('.');
            if (i < 0) {
                return str;
            } else if (inLhs) {
                // a qualified name in LHS, so
                // the assignment doesn't assign the variable but one of its fields
                return null;
            }
            return str.substring(0, i);
        }

        private static String methodLhsName(String name) {
            int i = name.indexOf('.');
            return i < 0 ? null // no lhs, the name is just the method name
                         : name.substring(0, i);
        }

        private ASTVariableDeclaratorId findVar(Scope scope, boolean isField, String name) {
            if (name == null) {
                return null;
            }

            if (isField) {
                return getFromSingleScope(enclosingClassScope, name);
            }

            while (scope != null) {
                ASTVariableDeclaratorId result = getFromSingleScope(scope, name);
                if (result != null) {
                    if (scope instanceof ClassScope && scope != enclosingClassScope) { // NOPMD CompareObjectsWithEqual this is what we want
                        // don't handle fields
                        return null;
                    }
                    return result;
                }

                scope = scope.getParent();
            }

            return null;
        }

        private ASTVariableDeclaratorId getFromSingleScope(Scope scope, String name) {
            if (scope != null) {
                for (VariableNameDeclaration decl : scope.getDeclarations(VariableNameDeclaration.class).keySet()) {
                    if (decl.getImage().equals(name)) {
                        return (ASTVariableDeclaratorId) decl.getNode();
                    }
                }
            }
            return null;
        }


        // ctor/initializer handling

        // this is the common denominator between anonymous class & astAnyTypeDeclaration on master

        @Override
        public Object visit(ASTClassOrInterfaceBody node, Object data) {
            visitTypeBody(node, (SpanInfo) data);
            return data; // type doesn't contribute anything to the enclosing control flow
        }

        @Override
        public Object visit(ASTEnumBody node, Object data) {
            visitTypeBody(node, (SpanInfo) data);
            return data; // type doesn't contribute anything to the enclosing control flow
        }


        private void visitTypeBody(JavaNode typeBody, SpanInfo data) {
            List<ASTAnyTypeBodyDeclaration> declarations = typeBody.findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
            processInitializers(declarations, data, (ClassScope) typeBody.getScope());

            for (ASTAnyTypeBodyDeclaration decl : declarations) {
                JavaNode d = decl.getDeclarationNode();
                if (d instanceof ASTMethodDeclaration) {
                    ASTMethodDeclaration method = (ASTMethodDeclaration) d;
                    if (!method.isAbstract() && !method.isNative()) {
                        ONLY_LOCALS.acceptOpt(d, data.forkCapturingNonLocal());
                    }
                } else if (d instanceof ASTAnyTypeDeclaration) {
                    JavaNode body = d.getChild(d.getNumChildren() - 1);
                    visitTypeBody(body, data.forkEmptyNonLocal());
                }
            }
        }

        private static void processInitializers(List<ASTAnyTypeBodyDeclaration> declarations,
                                                SpanInfo beforeLocal,
                                                ClassScope scope) {

            ReachingDefsVisitor visitor = new ReachingDefsVisitor(scope);

            // All field initializers + instance initializers
            SpanInfo ctorHeader = beforeLocal.forkCapturingNonLocal();
            // All static field initializers + static initializers
            SpanInfo staticInit = beforeLocal.forkEmptyNonLocal();

            List<ASTConstructorDeclaration> ctors = new ArrayList<>();

            for (ASTAnyTypeBodyDeclaration declaration : declarations) {
                JavaNode node = declaration.getDeclarationNode();

                final boolean isStatic;
                if (node instanceof ASTFieldDeclaration) {
                    isStatic = ((ASTFieldDeclaration) node).isStatic();
                } else if (node instanceof ASTInitializer) {
                    isStatic = ((ASTInitializer) node).isStatic();
                } else if (node instanceof ASTConstructorDeclaration) {
                    ctors.add((ASTConstructorDeclaration) node);
                    continue;
                } else {
                    continue;
                }

                if (isStatic) {
                    staticInit = visitor.acceptOpt(node, staticInit);
                } else {
                    ctorHeader = visitor.acceptOpt(node, ctorHeader);
                }
            }


            SpanInfo ctorEndState = ctors.isEmpty() ? ctorHeader : null;
            for (ASTConstructorDeclaration ctor : ctors) {
                SpanInfo state = visitor.acceptOpt(ctor, ctorHeader.forkCapturingNonLocal());
                ctorEndState = ctorEndState == null ? state : ctorEndState.absorb(state);
            }

            // assignments that reach the end of any constructor must
            // be considered used
            useAllSelfFields(staticInit, ctorEndState, visitor.enclosingClassScope);
        }

        static void useAllSelfFields(/*nullable*/SpanInfo staticState, SpanInfo instanceState, ClassScope classScope) {
            for (VariableNameDeclaration field : classScope.getVariableDeclarations().keySet()) {
                ASTVariableDeclaratorId var = field.getDeclaratorId();
                if (field.getAccessNodeParent().isStatic()) {
                    if (staticState != null) {
                        staticState.use(var);
                    }
                } else {
                    instanceState.use(var);
                }
            }
        }
    }

    /**
     * The shared state for all {@link SpanInfo} instances in the same
     * toplevel class.
     */
    private static class GlobalAlgoState {

        final Set<AssignmentEntry> allAssignments;
        final Set<AssignmentEntry> usedAssignments;

        // track which assignments kill which
        // assignment -> killers(assignment)
        final Map<AssignmentEntry, Set<AssignmentEntry>> killRecord;

        final TargetStack breakTargets = new TargetStack();
        // continue jumps to the condition check, while break jumps to after the loop
        final TargetStack continueTargets = new TargetStack();

        private GlobalAlgoState(Set<AssignmentEntry> allAssignments,
                                Set<AssignmentEntry> usedAssignments,
                                Map<AssignmentEntry, Set<AssignmentEntry>> killRecord) {
            this.allAssignments = allAssignments;
            this.usedAssignments = usedAssignments;
            this.killRecord = killRecord;

        }

        private GlobalAlgoState() {
            this(new HashSet<AssignmentEntry>(),
                 new HashSet<AssignmentEntry>(),
                 new HashMap<AssignmentEntry, Set<AssignmentEntry>>());
        }
    }

    // Information about a variable in a code span.
    static class VarLocalInfo {

        Set<AssignmentEntry> reachingDefs;

        VarLocalInfo(Set<AssignmentEntry> reachingDefs) {
            this.reachingDefs = reachingDefs;
        }

        VarLocalInfo absorb(VarLocalInfo other) {
            if (this.equals(other)) {
                return this;
            }
            Set<AssignmentEntry> merged = new HashSet<>(reachingDefs.size() + other.reachingDefs.size());
            merged.addAll(reachingDefs);
            merged.addAll(other.reachingDefs);
            return new VarLocalInfo(merged);
        }

        @Override
        public String toString() {
            return "VarLocalInfo{reachingDefs=" + reachingDefs + '}';
        }

        public VarLocalInfo copy() {
            return new VarLocalInfo(this.reachingDefs);
        }
    }

    /**
     * Information about a span of code.
     */
    private static class SpanInfo {

        // spans are arranged in a tree, to look for enclosing finallies
        // when abrupt completion occurs. Blocks that have non-local
        // control-flow (lambda bodies, anonymous classes, etc) aren't
        // linked to the outer parents.
        final SpanInfo parent;

        // If != null, then abrupt completion in this span of code (and any descendant)
        // needs to go through the finally span (the finally must absorb it)
        SpanInfo myFinally = null;


        /**
         * Inside a try block, we assume that any method/ctor call may
         * throw, which means, any assignment reaching such a method call
         * may reach the catch blocks if there are any.
         */
        List<SpanInfo> myCatches;

        final GlobalAlgoState global;

        final Map<ASTVariableDeclaratorId, VarLocalInfo> symtable;

        private SpanInfo(GlobalAlgoState global) {
            this(null, global, new HashMap<ASTVariableDeclaratorId, VarLocalInfo>());
        }

        private SpanInfo(SpanInfo parent,
                         GlobalAlgoState global,
                         Map<ASTVariableDeclaratorId, VarLocalInfo> symtable) {
            this.parent = parent;
            this.global = global;
            this.symtable = symtable;
            this.myCatches = Collections.emptyList();
        }

        boolean hasVar(ASTVariableDeclaratorId var) {
            return symtable.containsKey(var);
        }

        void assign(ASTVariableDeclaratorId var, JavaNode rhs) {
            AssignmentEntry entry = new AssignmentEntry(var, rhs);
            VarLocalInfo previous = symtable.put(var, new VarLocalInfo(Collections.singleton(entry)));
            if (previous != null) {
                // those assignments were overwritten ("killed")
                for (AssignmentEntry killed : previous.reachingDefs) {
                    if (killed.rhs instanceof ASTVariableDeclaratorId
                        && killed.rhs.getParent() instanceof ASTVariableDeclarator
                        && killed.rhs != rhs) {
                        continue;
                    }
                    // java8: computeIfAbsent
                    Set<AssignmentEntry> killers = global.killRecord.get(killed);
                    if (killers == null) {
                        killers = new HashSet<>(1);
                        global.killRecord.put(killed, killers);
                    }
                    killers.add(entry);
                }
            }
            global.allAssignments.add(entry);
        }

        void use(ASTVariableDeclaratorId var) {
            VarLocalInfo info = symtable.get(var);
            // may be null for implicit assignments, like method parameter
            if (info != null) {
                global.usedAssignments.addAll(info.reachingDefs);
            }
        }

        void deleteVar(ASTVariableDeclaratorId var) {
            symtable.remove(var);
        }

        /**
         * Record a leak of the `this` reference in a ctor (including field initializers).
         *
         * <p>This means, all defs reaching this point, for all fields
         * of `this`, may be used in the expression. We assume that the
         * ctor finishes its execution atomically, that is, following
         * definitions are not observable at an arbitrary point (that
         * would be too conservative).
         *
         * <p>Constructs that are considered to leak the `this` reference
         * (only processed if they occur in a ctor):
         * - using `this` as a method/ctor argument
         * - using `this` as the receiver of a method/ctor invocation
         *
         * <p>Because `this` may be aliased (eg in a field, a local var,
         * inside an anon class or capturing lambda, etc), any method
         * call, on any receiver, may actually observe field definitions
         * of `this`. So the analysis may show some false positives, which
         * hopefully should be rare enough.
         */
        public void recordThisLeak(boolean thisIsLeaking, ClassScope enclosingClassScope) {
            if (thisIsLeaking && enclosingClassScope != null) {
                // all reaching defs to fields until now may be observed
                ReachingDefsVisitor.useAllSelfFields(null, this, enclosingClassScope);
            }
        }

        // Forks duplicate this context, to preserve the reaching defs
        // of the current context while analysing a sub-block
        // Forks must be merged later if control flow merges again, see ::absorb

        SpanInfo fork() {
            return doFork(this, copyTable());
        }

        SpanInfo forkEmpty() {
            return doFork(this, new HashMap<ASTVariableDeclaratorId, VarLocalInfo>());
        }


        SpanInfo forkEmptyNonLocal() {
            return doFork(null, new HashMap<ASTVariableDeclaratorId, VarLocalInfo>());
        }

        SpanInfo forkCapturingNonLocal() {
            return doFork(null, copyTable());
        }

        private Map<ASTVariableDeclaratorId, VarLocalInfo> copyTable() {
            HashMap<ASTVariableDeclaratorId, VarLocalInfo> copy = new HashMap<>(this.symtable.size());
            for (ASTVariableDeclaratorId var : this.symtable.keySet()) {
                copy.put(var, this.symtable.get(var).copy());
            }
            return copy;
        }

        private SpanInfo doFork(/*nullable*/ SpanInfo parent, Map<ASTVariableDeclaratorId, VarLocalInfo> reaching) {
            return new SpanInfo(parent, this.global, reaching);
        }

        /** Abrupt completion for return, continue, break. */
        SpanInfo abruptCompletion(SpanInfo target) {
            // if target == null then this will unwind all the parents
            SpanInfo parent = this;
            while (parent != target && parent != null) { // NOPMD CompareObjectsWithEqual this is what we want
                if (parent.myFinally != null) {
                    parent.myFinally.absorb(this);
                    // stop on the first finally, its own end state will
                    // be merged into the nearest enclosing finally
                    return this;
                }
                parent = parent.parent;
            }

            this.symtable.clear();
            return this;
        }


        /**
         * Record an abrupt completion occurring because of a thrown
         * exception.
         *
         * @param byMethodCall If true, a method/ctor call threw the exception
         *                     (we conservatively consider they do inside try blocks).
         *                     Otherwise, a throw statement threw.
         */
        SpanInfo abruptCompletionByThrow(boolean byMethodCall) {
            // Find the first block that has a finally
            // Be absorbed into every catch block on the way.

            // In 7.0, with the precise type/overload resolution, we
            // can target the specific catch block that would catch the
            // exception.

            SpanInfo parent = this;
            while (parent != null) {

                if (!parent.myCatches.isEmpty()) {
                    for (SpanInfo c : parent.myCatches) {
                        c.absorb(this);
                    }
                }

                if (parent.myFinally != null) {
                    // stop on the first finally, its own end state will
                    // be merged into the nearest enclosing finally
                    parent.myFinally.absorb(this);
                    return this;
                }
                parent = parent.parent;
            }

            if (!byMethodCall) {
                this.symtable.clear(); // following is dead code
            }
            return this;
        }

        SpanInfo withCatchBlocks(List<SpanInfo> catchStmts) {
            assert myCatches.isEmpty() || catchStmts.isEmpty() : "Cannot set catch blocks twice";
            myCatches = Collections.unmodifiableList(catchStmts); // we own the list now, to avoid copying
            return this;
        }

        SpanInfo absorb(SpanInfo other) {
            // Merge reaching defs of the other scope into this
            // This is used to join paths after the control flow has forked

            // a spanInfo may be absorbed several times so this method should not
            // destroy the parameter
            if (this.equals(other) || other == null || other.symtable.isEmpty()) {
                return this;
            }

            // we don't have to double the capacity since they're normally of the same size
            // (vars are deleted when exiting a block)
            Set<ASTVariableDeclaratorId> keysUnion = new HashSet<>(this.symtable.keySet());
            keysUnion.addAll(other.symtable.keySet());

            for (ASTVariableDeclaratorId var : keysUnion) {
                VarLocalInfo thisInfo = this.symtable.get(var);
                VarLocalInfo otherInfo = other.symtable.get(var);
                if (thisInfo == otherInfo) { // NOPMD CompareObjectsWithEqual this is what we want
                    continue;
                }
                if (otherInfo != null && thisInfo != null) {
                    this.symtable.put(var, thisInfo.absorb(otherInfo));
                } else if (otherInfo != null) {
                    this.symtable.put(var, otherInfo.copy());
                }
            }
            return this;
        }

        @Override
        public String toString() {
            return symtable.toString();
        }
    }

    static class TargetStack {

        final Deque<SpanInfo> unnamedTargets = new ArrayDeque<>();
        final Map<String, SpanInfo> namedTargets = new HashMap<>();


        void push(SpanInfo state) {
            unnamedTargets.push(state);
        }

        SpanInfo pop() {
            return unnamedTargets.pop();
        }

        SpanInfo peek() {
            return unnamedTargets.getFirst();
        }

        SpanInfo doBreak(SpanInfo data, /* nullable */ String label) {
            // basically, reaching defs at the point of the break
            // also reach after the break (wherever it lands)
            SpanInfo target;
            if (label == null) {
                target = unnamedTargets.getFirst();
            } else {
                target = namedTargets.get(label);
            }

            if (target != null) { // otherwise CT error
                target.absorb(data);
            }
            return data.abruptCompletion(target);
        }
    }

    static class AssignmentEntry {

        final ASTVariableDeclaratorId var;

        // this is not necessarily an expression, it may be also the
        // variable declarator of a foreach loop
        final JavaNode rhs;

        AssignmentEntry(ASTVariableDeclaratorId var, JavaNode rhs) {
            this.var = var;
            this.rhs = rhs;
        }

        @Override
        public String toString() {
            return var.getName() + " := " + rhs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AssignmentEntry that = (AssignmentEntry) o;
            return Objects.equals(rhs, that.rhs);
        }

        @Override
        public int hashCode() {
            return rhs.hashCode();
        }
    }
}

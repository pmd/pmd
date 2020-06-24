/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;


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
import net.sourceforge.pmd.lang.java.rule.codestyle.ConfusingTernaryRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnusedAssignmentRule extends AbstractJavaRule {

    /*
        Detects unused assignments. This performs a reaching definition
        analysis.

        This DFA can be modified trivially to check for all
        unused variables (just maintain a global set of variables that
        must be used, adding them as you go, and on each AlgoState::use,
        remove the var from this set). This would work even without variable
        usage pre-resolution (which in 7.0 is not implemented yet and
        maybe won't be).

        Since we have the reaching definitions at each variable usage, we
        could also use that to detect other kinds of bug, eg conditions
        that are always true, or dereferences that will always NPE. In
        the general case though, this is complicated and better left to
        a DFA library, eg google Z3.

        TODO
           * labels on arbitrary statements (currently only loops)
           * explicit ctor call (hard to impossible without type res,
             or at least proper graph algorithms like toposort)
                -> this is pretty invisible as it causes false negatives, not FPs
           * test ternary expr
           * conditional exprs in loops

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

     */

    private static final PropertyDescriptor<Boolean> CHECK_PREFIX_INCREMENT =
        PropertyFactory.booleanProperty("checkUnusedPrefixIncrement")
                       .desc("Report expressions like ++i that may be replaced with (i + 1)")
                       .defaultValue(false)
                       .build();

    private static final PropertyDescriptor<Boolean> REPORT_UNUSED_VARS =
        PropertyFactory.booleanProperty("reportUnusedVariables")
                       .desc("Report variables that are only initialized, and never read at all. "
                                 + "The rule UnusedVariable already cares for that, so you can disable it if needed")
                       .defaultValue(false)
                       .build();

    public UnusedAssignmentRule() {
        definePropertyDescriptor(CHECK_PREFIX_INCREMENT);
        definePropertyDescriptor(REPORT_UNUSED_VARS);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        for (JavaNode child : node.children()) {
            if (child instanceof ASTTypeDeclaration) {

                ASTAnyTypeDeclaration typeDecl = (ASTAnyTypeDeclaration) child.getChild(child.getNumChildren() - 1);
                GlobalAlgoState result = new GlobalAlgoState();
                typeDecl.jjtAccept(ReachingDefsVisitor.ONLY_LOCALS, new AlgoState(result));

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
                boolean isField = entry.var.getNode().getScope() instanceof ClassScope;

                Set<AssignmentEntry> killers = result.killRecord.get(entry);
                final String reason;
                if (killers == null || killers.isEmpty()) {
                    if (isField) {
                        // assignments to fields don't really go out of scope
                        continue;
                    } else if (suppressUnusedVariableRuleOverlap(entry)) {
                        // see REPORT_UNUSED_VARS property
                        continue;
                    }
                    // This is a "DU" anomaly, the others are "DD"
                    reason = "goes out of scope";
                } else if (killers.size() == 1) {
                    AssignmentEntry k = killers.iterator().next();
                    reason = "overwritten on line " + k.rhs.getBeginLine();
                } else {
                    reason = joinLines("overwritten on lines ", killers);
                }
                addViolationWithMessage(ruleCtx, entry.rhs, makeMessage(entry, reason, isField));
            }
        }
    }

    private boolean suppressUnusedVariableRuleOverlap(AssignmentEntry entry) {
        return !getProperty(REPORT_UNUSED_VARS) && entry.rhs instanceof ASTVariableInitializer;
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

    private static String makeMessage(AssignmentEntry assignment, String reason, boolean isField) {
        String varName = assignment.var.getName();
        StringBuilder format = new StringBuilder("The ");
        if (assignment.rhs instanceof ASTVariableInitializer) {
            format.append(isField ? "field initializer for"
                                  : "initializer for variable");
        } else {
            if (assignment.rhs instanceof ASTPreIncrementExpression
                || assignment.rhs instanceof ASTPreDecrementExpression
                || assignment.rhs instanceof ASTPostfixExpression) {
                format.append("updated value of ");
            } else {
                format.append("value assigned to ");
            }
            format.append(isField ? "field" : "variable");
        }
        format.append(" ''").append(varName).append("''");
        format.append(" is never used (").append(reason).append(")");
        return format.toString();
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

            AlgoState state = (AlgoState) data;
            Set<VariableNameDeclaration> localsToKill = new HashSet<>();

            for (JavaNode child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                state = acceptOpt(child, state);
                if (child instanceof ASTBlockStatement
                    && child.getChild(0) instanceof ASTLocalVariableDeclaration) {
                    ASTLocalVariableDeclaration local = (ASTLocalVariableDeclaration) child.getChild(0);
                    for (ASTVariableDeclaratorId id : local) {
                        localsToKill.add(id.getNameDeclaration());
                    }
                }
            }

            for (VariableNameDeclaration var : localsToKill) {
                state.deleteVar(var);
            }

            return state;
        }

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            return processSwitch(node, (AlgoState) data, node.getTestedExpression());
        }

        @Override
        public Object visit(ASTSwitchExpression node, Object data) {
            return processSwitch(node, (AlgoState) data, node.getChild(0));
        }

        private AlgoState processSwitch(JavaNode switchLike, AlgoState data, JavaNode testedExpr) {
            GlobalAlgoState global = data.global;
            AlgoState before = acceptOpt(testedExpr, data);

            global.breakTargets.push(before.fork());

            AlgoState current = before;
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
            AlgoState before = (AlgoState) data;
            return makeConditional(before, node.getCondition(), node.getThenBranch(), node.getElseBranch());
        }

        @Override
        public Object visit(ASTConditionalExpression node, Object data) {
            AlgoState before = (AlgoState) data;
            return makeConditional(before, node.getCondition(), node.getChild(1), node.getChild(2));
        }

        AlgoState makeConditional(AlgoState before, JavaNode condition, JavaNode thenBranch, JavaNode elseBranch) {
            AlgoState thenState = before.fork();
            AlgoState elseState = elseBranch != null ? before.fork() : before;

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
         * The new innermost `if` is recursively processed to translate
         * bigger conditions, like `a || b && c`
         *
         * This is how it works, but the <then> and <else> branch are
         * visited only once, because it's not done in this method, but
         * in makeConditional.
         */
        private AlgoState linkConditional(AlgoState before, JavaNode condition, AlgoState thenState, AlgoState elseState, boolean isTopLevel) {
            condition = ConfusingTernaryRule.unwrapParentheses(condition);

            if (condition instanceof ASTConditionalOrExpression) {
                return visitShortcutOrExpr(condition, before, thenState, elseState);
            } else if (condition instanceof ASTConditionalAndExpression) {
                // To mimic a shortcut AND expr, swap the thenState and the elseState
                // See explanations in method
                return visitShortcutOrExpr(condition, before, elseState, thenState);
            } else if (condition instanceof ASTExpression && condition.getNumChildren() == 1) {
                return linkConditional(before, condition.getChild(0), thenState, elseState, isTopLevel);
            } else {
                AlgoState state = acceptOpt(condition, before);
                if (isTopLevel) {
                    thenState.absorb(state);
                    elseState.absorb(state);
                }
                return state;
            }
        }

        AlgoState visitShortcutOrExpr(JavaNode orExpr,
                                      AlgoState before,
                                      AlgoState thenState,
                                      AlgoState elseState) {

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

            AlgoState cur = before;
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
            final AlgoState before = (AlgoState) data;
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

            ASTResourceSpecification resources = node.getFirstChildOfType(ASTResourceSpecification.class);

            AlgoState bodyState = acceptOpt(resources, before.fork());
            bodyState = acceptOpt(node.getBody(), bodyState);

            AlgoState exceptionalState = null;
            for (ASTCatchStatement catchClause : node.getCatchClauses()) {
                AlgoState current = acceptOpt(catchClause, before.fork().absorb(bodyState));
                exceptionalState = current.absorb(exceptionalState);
            }

            AlgoState finalState;
            finalState = bodyState.absorb(exceptionalState);
            if (finallyClause != null) {
                // this represents the finally clause when it was entered
                // because of abrupt completion
                // since we don't know when it terminated we must join it with before
                AlgoState abruptFinally = before.myFinally.absorb(before);
                acceptOpt(finallyClause, abruptFinally);
                before.myFinally = null;

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
        public Object visit(ASTLambdaExpression node, Object data) {
            // Lambda expression have control flow that is separate from the method
            // So we fork the context, but don't join it

            // Reaching definitions of the enclosing context still reach in the lambda
            // Since those definitions are [effectively] final, they actually can't be
            // killed, but they can be used in the lambda

            AlgoState before = (AlgoState) data;

            JavaNode lambdaBody = node.getChild(node.getNumChildren() - 1);
            // if it's an expression, then no assignments may occur in it,
            // but it can still use some variables of the context
            acceptOpt(lambdaBody, before.forkCapturingNonLocal());
            return before;
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            return handleLoop(node, (AlgoState) data, null, node.getCondition(), null, node.getBody(), true, null);
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            return handleLoop(node, (AlgoState) data, null, node.getCondition(), null, node.getBody(), false, null);
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ASTStatement body = node.getBody();
            if (node.isForeach()) {
                // the iterable expression
                JavaNode init = node.getChild(1);
                ASTVariableDeclaratorId foreachVar = (ASTVariableDeclaratorId) node.getChild(0).getChild(1).getChild(0);
                return handleLoop(node, (AlgoState) data, init, null, null, body, true, foreachVar.getNameDeclaration());
            } else {
                ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
                ASTExpression cond = node.getCondition();
                ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
                return handleLoop(node, (AlgoState) data, init, cond, update, body, true, null);
            }
        }


        private AlgoState handleLoop(JavaNode loop,
                                     AlgoState before,
                                     JavaNode init,
                                     JavaNode cond,
                                     JavaNode update,
                                     JavaNode body,
                                     boolean checkFirstIter,
                                     VariableNameDeclaration foreachVar) {
            // TODO linkConditional
            final GlobalAlgoState globalState = before.global;

            // perform a few "iterations", to make sure that assignments in
            // the body can affect themselves in the next iteration, and
            // that they affect the condition, etc

            before = acceptOpt(init, before);
            if (checkFirstIter) { // false for do-while
                before = acceptOpt(cond, before);
            }

            AlgoState breakTarget = before.forkEmpty();
            AlgoState continueTarget = before.forkEmpty();

            pushTargets(loop, breakTarget, continueTarget);

            // make the defs of the body reach the other parts of the loop,
            // including itself
            AlgoState iter = acceptOpt(body, before.fork());

            if (foreachVar != null && iter.hasVar(foreachVar)) {
                // in foreach loops, the loop variable is reassigned on each update
                iter.assign(foreachVar, (JavaNode) foreachVar.getNode());
            } else {
                iter = acceptOpt(update, iter);
            }

            iter = acceptOpt(cond, iter);
            iter = acceptOpt(body, iter);


            breakTarget = globalState.breakTargets.peek();
            continueTarget = globalState.continueTargets.peek();
            if (!continueTarget.reachingDefs.isEmpty()) {
                // make assignments before a continue reach the other parts of the loop
                continueTarget = acceptOpt(cond, continueTarget);
                continueTarget = acceptOpt(body, continueTarget);
                continueTarget = acceptOpt(update, continueTarget);
            }

            AlgoState result = popTargets(loop, breakTarget, continueTarget);
            result = result.absorb(iter);
            if (checkFirstIter) {
                // if the first iteration is checked,
                // then it could be false on the first try, meaning
                // the definitions before the loop reach after too
                result = result.absorb(before);
            }

            return result;
        }

        private void pushTargets(JavaNode loop, AlgoState breakTarget, AlgoState continueTarget) {
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

        private AlgoState popTargets(JavaNode loop, AlgoState breakTarget, AlgoState continueTarget) {
            GlobalAlgoState globalState = breakTarget.global;
            globalState.breakTargets.unnamedTargets.pop();
            globalState.continueTargets.unnamedTargets.pop();

            AlgoState total = breakTarget.absorb(continueTarget);

            Node parent = loop.getNthParent(2);
            while (parent instanceof ASTLabeledStatement) {
                String label = parent.getImage();
                total = total.absorb(globalState.breakTargets.namedTargets.remove(label));
                total = total.absorb(globalState.continueTargets.namedTargets.remove(label));
                parent = parent.getNthParent(2);
            }
            return total;
        }

        private AlgoState acceptOpt(JavaNode node, AlgoState before) {
            return node == null ? before : (AlgoState) node.jjtAccept(this, before);
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            AlgoState state = (AlgoState) data;
            return state.global.continueTargets.doBreak(state, node.getImage());
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            AlgoState state = (AlgoState) data;
            return state.global.breakTargets.doBreak(state, node.getImage());
        }

        @Override
        public Object visit(ASTYieldStatement node, Object data) {
            super.visit(node, data); // visit expression

            AlgoState state = (AlgoState) data;
            // treat as break, ie abrupt completion + link reaching defs to outer context
            return state.global.breakTargets.doBreak(state, null);
        }


        // both of those exit the scope of the method/ctor, so their assignments go dead

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            super.visit(node, data);
            return ((AlgoState) data).abruptCompletion(null);
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            super.visit(node, data);
            return ((AlgoState) data).abruptCompletion(null);
        }

        // following deals with assignment


        @Override
        public Object visit(ASTVariableDeclarator node, Object data) {
            VariableNameDeclaration var = node.getVariableId().getNameDeclaration();
            ASTVariableInitializer rhs = node.getInitializer();
            if (rhs != null) {
                rhs.jjtAccept(this, data);
                ((AlgoState) data).assign(var, rhs);
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
            AlgoState result = (AlgoState) data;
            if (node.getNumChildren() == 3) {
                // assignment
                assert node.getChild(1) instanceof ASTAssignmentOperator;

                // visit the rhs as it is evaluated before
                JavaNode rhs = node.getChild(2);
                result = acceptOpt(rhs, result);

                VariableNameDeclaration lhsVar = getVarFromExpression(node.getChild(0), true);
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
            return checkIncOrDecrement(node, (AlgoState) data);
        }

        @Override
        public Object visit(ASTPreIncrementExpression node, Object data) {
            return checkIncOrDecrement(node, (AlgoState) data);
        }

        @Override
        public Object visit(ASTPostfixExpression node, Object data) {
            return checkIncOrDecrement(node, (AlgoState) data);
        }

        private AlgoState checkIncOrDecrement(JavaNode unary, AlgoState data) {
            VariableNameDeclaration var = getVarFromExpression(unary.getChild(0), true);
            if (var != null) {
                data.use(var);
                data.assign(var, unary);
            }
            return data;
        }

        // variable usage

        @Override
        public Object visit(ASTPrimaryExpression node, Object data) {
            AlgoState state = (AlgoState) visit((JavaNode) node, data); // visit subexpressions

            VariableNameDeclaration var = getVarFromExpression(node, false);
            if (var != null) {
                state.use(var);
            }
            return state;
        }

        /**
         * Get the variable accessed from a primary.
         */
        private VariableNameDeclaration getVarFromExpression(JavaNode primary, boolean inLhs) {

            if (primary instanceof ASTPrimaryExpression) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primary.getChild(0);


                //   this.x = 2;
                if (prefix.usesThisModifier() && this.enclosingClassScope != null) {
                    if (primary.getNumChildren() < 2 || primary.getNumChildren() > 2 && inLhs) {
                        return null;
                    }

                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) primary.getChild(1);
                    if (suffix.getImage() == null) {
                        // catches arrays and such
                        return null;
                    }

                    return findVar(primary.getScope(), true, suffix.getImage());
                } else {
                    if (prefix.getNumChildren() > 0 && (prefix.getChild(0) instanceof ASTName)) {
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

        private VariableNameDeclaration findVar(Scope scope, boolean isField, String name) {
            if (name == null) {
                return null;
            }

            if (isField) {
                return getFromSingleScope(enclosingClassScope, name);
            }

            while (scope != null) {
                VariableNameDeclaration result = getFromSingleScope(scope, name);
                if (result != null) {
                    if (scope instanceof ClassScope && scope != enclosingClassScope) {
                        // don't handle fields
                        return null;
                    }
                    return result;
                }

                scope = scope.getParent();
            }

            return null;
        }

        private VariableNameDeclaration getFromSingleScope(Scope scope, String name) {
            if (scope != null) {
                for (VariableNameDeclaration decl : scope.getDeclarations(VariableNameDeclaration.class).keySet()) {
                    if (decl.getImage().equals(name)) {
                        return decl;
                    }
                }
            }
            return null;
        }


        // ctor/initializer handling

        // this is the common denominator between anonymous class & astAnyTypeDeclaration on master

        @Override
        public Object visit(ASTClassOrInterfaceBody node, Object data) {
            visitTypeBody(node, (AlgoState) data);
            return data; // type doesn't contribute anything to the enclosing control flow
        }

        @Override
        public Object visit(ASTEnumBody node, Object data) {
            visitTypeBody(node, (AlgoState) data);
            return data; // type doesn't contribute anything to the enclosing control flow
        }


        private void visitTypeBody(JavaNode typeBody, AlgoState data) {
            List<ASTAnyTypeBodyDeclaration> declarations = typeBody.findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
            processInitializers(declarations, data, (ClassScope) typeBody.getScope());

            for (ASTAnyTypeBodyDeclaration decl : declarations) {
                JavaNode d = decl.getDeclarationNode();
                if (d instanceof ASTMethodDeclaration) {
                    ONLY_LOCALS.acceptOpt(d, data.forkCapturingNonLocal());
                } else if (d instanceof ASTAnyTypeDeclaration) {
                    JavaNode body = d.getChild(d.getNumChildren() - 1);
                    visitTypeBody(body, data.forkEmptyNonLocal());
                }
            }
        }


        private static void processInitializers(List<ASTAnyTypeBodyDeclaration> declarations,
                                                AlgoState beforeLocal,
                                                ClassScope scope) {

            ReachingDefsVisitor visitor = new ReachingDefsVisitor(scope);

            // All field initializers + instance initializers
            AlgoState ctorHeader = beforeLocal.forkCapturingNonLocal();
            // All static field initializers + static initializers
            AlgoState staticInit = beforeLocal.forkEmptyNonLocal();

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


            AlgoState ctorEndState = ctors.isEmpty() ? ctorHeader : null;
            for (ASTConstructorDeclaration ctor : ctors) {
                AlgoState state = visitor.acceptOpt(ctor, ctorHeader.forkCapturingNonLocal());
                ctorEndState = ctorEndState == null ? state : ctorEndState.absorb(state);
            }

            // assignments that reach the end of any constructor must
            // be considered used
            for (VariableNameDeclaration field : visitor.enclosingClassScope.getVariableDeclarations().keySet()) {
                if (field.getAccessNodeParent().isStatic()) {
                    staticInit.use(field);
                }
                ctorEndState.use(field);
            }
        }
    }

    /**
     * The shared state for all {@link AlgoState} instances in the same
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

    private static class AlgoState {

        // nodes are arranged in a tree, to look for enclosing finallies
        // when abrupt completion occurs. Blocks that have non-local
        // control-flow (lambda bodies, anonymous classes, etc) aren't
        // linked to the outer parents.
        final AlgoState parent;

        final GlobalAlgoState global;

        // Map of var -> reaching(var)
        // Implicit assignments, like parameter declarations, are not contained
        // in this
        final Map<VariableNameDeclaration, Set<AssignmentEntry>> reachingDefs;

        // If != null, then this node has a finally that all abrupt-completing
        // statements must go through.
        AlgoState myFinally = null;

        private AlgoState(GlobalAlgoState global) {
            this(null, global, new HashMap<VariableNameDeclaration, Set<AssignmentEntry>>());
        }

        private AlgoState(AlgoState parent,
                          GlobalAlgoState global,
                          Map<VariableNameDeclaration, Set<AssignmentEntry>> reachingDefs) {
            this.parent = parent;
            this.global = global;
            this.reachingDefs = reachingDefs;
        }

        boolean hasVar(VariableNameDeclaration var) {
            return reachingDefs.containsKey(var);
        }

        void assign(VariableNameDeclaration var, JavaNode rhs) {
            AssignmentEntry entry = new AssignmentEntry(var, rhs);
            Set<AssignmentEntry> killed = reachingDefs.put(var, Collections.singleton(entry));
            if (killed != null) {
                // those assignments were overwritten ("killed")
                for (AssignmentEntry k : killed) {
                    // java8: computeIfAbsent
                    Set<AssignmentEntry> killers = global.killRecord.get(k);
                    if (killers == null) {
                        killers = new HashSet<>(1);
                        global.killRecord.put(k, killers);
                    }
                    killers.add(entry);
                }
            }
            global.allAssignments.add(entry);
        }

        void use(VariableNameDeclaration var) {
            Set<AssignmentEntry> reaching = reachingDefs.get(var);
            // may be null for implicit assignments, like method parameter
            if (reaching != null) {
                global.usedAssignments.addAll(reaching);
            }
        }

        void deleteVar(VariableNameDeclaration var) {
            reachingDefs.remove(var);
        }

        // Forks duplicate this context, to preserve the reaching defs
        // of the current context while analysing a sub-block
        // Forks must be merged later if control flow merges again, see ::absorb

        AlgoState fork() {
            return doFork(this, new HashMap<>(this.reachingDefs));
        }

        AlgoState forkEmpty() {
            return doFork(this, new HashMap<VariableNameDeclaration, Set<AssignmentEntry>>());
        }


        AlgoState forkEmptyNonLocal() {
            return doFork(null, new HashMap<VariableNameDeclaration, Set<AssignmentEntry>>());
        }

        AlgoState forkCapturingNonLocal() {
            return doFork(null, new HashMap<>(this.reachingDefs));
        }

        private AlgoState doFork(AlgoState parent, Map<VariableNameDeclaration, Set<AssignmentEntry>> reaching) {
            return new AlgoState(parent, this.global, reaching);
        }

        AlgoState abruptCompletion(AlgoState target) {
            // if target == null then this will unwind all the parents
            AlgoState parent = this;
            while (parent != target && parent != null) {
                if (parent.myFinally != null) {
                    parent.myFinally.absorb(this);
                }
                parent = parent.parent;
            }

            this.reachingDefs.clear();
            return this;
        }


        AlgoState absorb(AlgoState sub) {
            // Merge reaching defs of the other scope into this
            // This is used to join paths after the control flow has forked
            if (sub == this || sub == null || sub.reachingDefs.isEmpty()) {
                return this;
            }

            for (VariableNameDeclaration var : this.reachingDefs.keySet()) {
                Set<AssignmentEntry> myAssignments = this.reachingDefs.get(var);
                Set<AssignmentEntry> subScopeAssignments = sub.reachingDefs.get(var);
                if (subScopeAssignments == null) {
                    continue;
                }
                joinSets(var, myAssignments, subScopeAssignments);
            }

            for (VariableNameDeclaration var : sub.reachingDefs.keySet()) {
                Set<AssignmentEntry> subScopeAssignments = sub.reachingDefs.get(var);
                Set<AssignmentEntry> myAssignments = this.reachingDefs.get(var);
                if (myAssignments == null) {
                    this.reachingDefs.put(var, subScopeAssignments);
                    continue;
                }
                joinSets(var, myAssignments, subScopeAssignments);
            }

            return this;
        }

        private void joinSets(VariableNameDeclaration var, Set<AssignmentEntry> set1, Set<AssignmentEntry> set2) {
            Set<AssignmentEntry> newReaching = new HashSet<>(set1.size() + set2.size());
            newReaching.addAll(set2);
            newReaching.addAll(set1);
            this.reachingDefs.put(var, newReaching);
        }

        @Override
        public String toString() {
            return reachingDefs.toString();
        }
    }

    static class TargetStack {

        final Deque<AlgoState> unnamedTargets = new ArrayDeque<>();
        final Map<String, AlgoState> namedTargets = new HashMap<>();


        void push(AlgoState state) {
            unnamedTargets.push(state);
        }

        AlgoState pop() {
            return unnamedTargets.pop();
        }

        AlgoState peek() {
            return unnamedTargets.getFirst();
        }

        AlgoState doBreak(AlgoState data,/* nullable */ String label) {
            // basically, reaching defs at the point of the break
            // also reach after the break (wherever it lands)
            AlgoState target;
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

        final VariableNameDeclaration var;

        // this is not necessarily an expression, it may be also the
        // variable declarator of a foreach loop
        final JavaNode rhs;

        AssignmentEntry(VariableNameDeclaration var, JavaNode rhs) {
            this.var = var;
            this.rhs = rhs;
        }

        @Override
        public String toString() {
            return var.getImage() + " := " + rhs;
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

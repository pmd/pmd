/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTCatchParameter;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.rule.design.SingularFieldRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * A reaching definition analysis. This may be used to check whether
 * eg a value escapes, or is overwritten on all code paths.
 */
public final class DataflowPass {

    private static final SimpleDataKey<DataflowResult> DATAFLOW_RESULT_K = DataMap.simpleDataKey("java.dataflow.global");
    private static final SimpleDataKey<ReachingDefinitionSet> REACHING_DEFS = DataMap.simpleDataKey("java.dataflow.reaching.backwards");
    private static final SimpleDataKey<AssignmentEntry> VAR_DEFINITION = DataMap.simpleDataKey("java.dataflow.field.def");
    private static final SimpleDataKey<OptionalBool> SWITCH_BRANCH_FALLS_THROUGH = DataMap.simpleDataKey("java.dataflow.switch.fallthrough");

    public static DataflowResult getDataflowResult(ASTCompilationUnit acu) {
        return acu.getUserMap().computeIfAbsent(DATAFLOW_RESULT_K, () -> process(acu));
    }

    public static void ensureProcessed(ASTCompilationUnit acu) {
        getDataflowResult(acu);
    }

    public static @Nullable ReachingDefinitionSet getReachingDefinitions(ASTNamedReferenceExpr expr) {
        return expr.getUserMap().get(REACHING_DEFS);
    }

    /**
     * If the var id is that of a field, returns the assignment entry that
     * corresponds to its definition (either blank or its initializer). From
     * there, using the kill record, we can draw the graph of all assignments.
     * Returns null if not a field, or the compilation unit has not been processed.
     */
    public static @Nullable AssignmentEntry getFieldDefinition(ASTVariableDeclaratorId varId) {
        if (!varId.isField()) {
            return null;
        }
        return varId.getUserMap().get(VAR_DEFINITION);
    }

    public static @NonNull OptionalBool switchBranchFallsThrough(ASTSwitchBranch b) {
        if (b instanceof ASTSwitchFallthroughBranch) {
            return Objects.requireNonNull(b.getUserMap().get(SWITCH_BRANCH_FALLS_THROUGH));
        }
        return OptionalBool.NO;
    }

    private static DataflowResult process(ASTCompilationUnit node) {
        DataflowResult dataflowResult = new DataflowResult();
        for (ASTAnyTypeDeclaration typeDecl : node.getTypeDeclarations()) {
            GlobalAlgoState subResult = new GlobalAlgoState();
            typeDecl.acceptVisitor(ReachingDefsVisitor.ONLY_LOCALS, new SpanInfo(subResult));
            if (subResult.usedAssignments.size() < subResult.allAssignments.size()) {
                Set<AssignmentEntry> unused = subResult.allAssignments;
                unused.removeAll(subResult.usedAssignments);
                unused.removeIf(AssignmentEntry::isUnbound);
                unused.removeIf(AssignmentEntry::isFieldDefaultValue);
                dataflowResult.unusedAssignments.addAll(unused);
            }

            CollectionUtil.mergeMaps(
                dataflowResult.killRecord, subResult.killRecord,
                (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });
        }

        return dataflowResult;
    }

    public static final class ReachingDefinitionSet {

        private final Set<AssignmentEntry> reaching;
        private final boolean isNotFullyKnown;
        private final boolean containsInitialFieldValue;

        public ReachingDefinitionSet(Set<AssignmentEntry> reaching) {
            this.reaching = reaching;
            this.containsInitialFieldValue = reaching.removeIf(AssignmentEntry::isFieldAssignmentAtStartOfMethod);
            // not || as we want the side effect
            this.isNotFullyKnown = containsInitialFieldValue | reaching.removeIf(AssignmentEntry::isUnbound);
        }

        /**
         * Returns the set of assignments that may reach the place.
         */
        public Set<AssignmentEntry> getReaching() {
            return Collections.unmodifiableSet(reaching);
        }

        /**
         * Returns true if there were some {@linkplain AssignmentEntry#isUnbound() unbound}
         * assignments in this set. They are not part of {@link #getReaching()}.
         */
        public boolean isNotFullyKnown() {
            return isNotFullyKnown;
        }

        /**
         * Contains a {@link AssignmentEntry#isFieldAssignmentAtStartOfMethod()}.
         * They are not part of {@link #getReaching()}.
         */
        public boolean containsInitialFieldValue() {
            return containsInitialFieldValue;
        }
    }

    public static final class DataflowResult {

        final Set<AssignmentEntry> unusedAssignments;
        final Map<AssignmentEntry, Set<AssignmentEntry>> killRecord;


        public DataflowResult() {
            this.unusedAssignments = new HashSet<>();
            this.killRecord = new HashMap<>();
        }


        public Set<AssignmentEntry> getUnusedAssignments() {
            return Collections.unmodifiableSet(unusedAssignments);
        }

        public @NonNull Set<AssignmentEntry> getKillers(AssignmentEntry assignment) {
            return killRecord.getOrDefault(assignment, Collections.emptySet());
        }
    }

    private static class ReachingDefsVisitor extends JavaVisitorBase<SpanInfo, SpanInfo> {


        static final ReachingDefsVisitor ONLY_LOCALS = new ReachingDefsVisitor(null, false);

        // The class scope for the "this" reference, used to find fields
        // of this class
        // null if we're not processing instance/static initializers,
        // so in methods we don't care about fields
        // If not null, fields are effectively treated as locals
        private final JClassSymbol enclosingClassScope;
        private final boolean inStaticCtx;

        private ReachingDefsVisitor(JClassSymbol scope, boolean inStaticCtx) {
            this.enclosingClassScope = scope;
            this.inStaticCtx = inStaticCtx;
        }

        /**
         * If true, we're also tracking fields of the {@code this} instance,
         * because we're in a ctor or initializer, or instance method.
         */
        private boolean trackThisInstance() {
            return !inStaticCtx;
        }

        private boolean trackStaticFields() {
            // only tracked in initializers
            return enclosingClassScope != null && inStaticCtx;
        }

        // following deals with control flow structures

        @Override
        protected SpanInfo visitChildren(Node node, SpanInfo data) {
            for (Node child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                data = child.acceptVisitor(this, data);
            }
            return data;
        }

        @Override
        public SpanInfo visit(ASTBlock node, final SpanInfo data) {
            // variables local to a loop iteration must be killed before the
            // next iteration

            SpanInfo state = data;
            Set<ASTVariableDeclaratorId> localsToKill = new HashSet<>();

            for (JavaNode child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                state = acceptOpt(child, state);
                if (child instanceof ASTLocalVariableDeclaration) {
                    for (ASTVariableDeclaratorId id : (ASTLocalVariableDeclaration) child) {
                        localsToKill.add(id);
                    }
                }
            }

            for (ASTVariableDeclaratorId var : localsToKill) {
                state.deleteVar(var.getSymbol());
            }

            return state;
        }

        @Override
        public SpanInfo visit(ASTSwitchStatement node, SpanInfo data) {
            return processSwitch(node, data);
        }

        @Override
        public SpanInfo visit(ASTSwitchExpression node, SpanInfo data) {
            return processSwitch(node, data);
        }

        private SpanInfo processSwitch(ASTSwitchLike switchLike, SpanInfo data) {
            GlobalAlgoState global = data.global;
            SpanInfo before = acceptOpt(switchLike.getTestedExpression(), data);

            global.breakTargets.push(before.fork());

            SpanInfo current = before;
            for (ASTSwitchBranch branch : switchLike.getBranches()) {
                if (branch instanceof ASTSwitchArrowBranch) {
                    current = acceptOpt(((ASTSwitchArrowBranch) branch).getRightHandSide(), before.fork());
                    current = global.breakTargets.doBreak(current, null); // process this as if it was followed by a break
                } else {
                    // fallthrough branch
                    current = acceptOpt(branch, before.fork().absorb(current));
                    branch.getUserMap().set(SWITCH_BRANCH_FALLS_THROUGH, current.hasCompletedAbruptly.complement());
                }
            }

            before = global.breakTargets.pop();

            // join with the last state, which is the exit point of the
            // switch, if it's not closed by a break;
            return before.absorb(current);
        }

        @Override
        public SpanInfo visit(ASTIfStatement node, SpanInfo data) {
            return makeConditional(data, node.getCondition(), node.getThenBranch(), node.getElseBranch());
        }

        @Override
        public SpanInfo visit(ASTConditionalExpression node, SpanInfo data) {
            return makeConditional(data, node.getCondition(), node.getThenBranch(), node.getElseBranch());
        }

        SpanInfo makeConditional(SpanInfo before, ASTExpression condition, JavaNode thenBranch, JavaNode elseBranch) {
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
        private SpanInfo linkConditional(SpanInfo before, ASTExpression condition, SpanInfo thenState, SpanInfo elseState, boolean isTopLevel) {
            if (condition == null) {
                return before;
            }

            if (condition instanceof ASTInfixExpression) {
                BinaryOp op = ((ASTInfixExpression) condition).getOperator();
                if (op == BinaryOp.CONDITIONAL_OR) {
                    return visitShortcutOrExpr((ASTInfixExpression) condition, before, thenState, elseState);
                } else if (op == BinaryOp.CONDITIONAL_AND) {
                    // To mimic a shortcut AND expr, swap the thenState and the elseState
                    // See explanations in method
                    return visitShortcutOrExpr((ASTInfixExpression) condition, before, elseState, thenState);
                }
            }

            SpanInfo state = acceptOpt(condition, before);
            if (isTopLevel) {
                thenState.absorb(state);
                elseState.absorb(state);
            }
            return state;
        }

        SpanInfo visitShortcutOrExpr(ASTInfixExpression orExpr,
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

            SpanInfo cur = before;
            cur = linkConditional(cur, orExpr.getLeftOperand(), thenState, elseState, false);
            thenState.absorb(cur);
            cur = linkConditional(cur, orExpr.getRightOperand(), thenState, elseState, false);
            thenState.absorb(cur);

            elseState.absorb(cur);

            return cur;
        }


        @Override
        public SpanInfo visit(ASTTryStatement node, final SpanInfo before) {

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
            ASTFinallyClause finallyClause = node.getFinallyClause();

            if (finallyClause != null) {
                before.myFinally = before.forkEmpty();
            }

            final List<ASTCatchClause> catchClauses = node.getCatchClauses().toList();
            final List<SpanInfo> catchSpans = catchClauses.isEmpty() ? Collections.emptyList()
                                                                     : new ArrayList<>();

            // pre-fill catch spans
            for (int i = 0; i < catchClauses.size(); i++) {
                catchSpans.add(before.forkEmpty());
            }

            @Nullable ASTResourceList resources = node.getResources();

            SpanInfo bodyState = before.fork();
            bodyState = bodyState.withCatchBlocks(catchSpans);
            bodyState = acceptOpt(resources, bodyState);
            bodyState = acceptOpt(node.getBody(), bodyState);
            bodyState = bodyState.withCatchBlocks(Collections.emptyList());

            SpanInfo exceptionalState = null;
            int i = 0;
            for (ASTCatchClause catchClause : node.getCatchClauses()) {
                SpanInfo current = acceptOpt(catchClause, catchSpans.get(i));
                exceptionalState = current.absorb(exceptionalState);
                i++;
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
        public SpanInfo visit(ASTCatchClause node, SpanInfo data) {
            SpanInfo result = visitJavaNode(node, data);
            result.deleteVar(node.getParameter().getVarId().getSymbol());
            return result;
        }

        @Override
        public SpanInfo visit(ASTLambdaExpression node, SpanInfo data) {
            // Lambda expression have control flow that is separate from the method
            // So we fork the context, but don't join it

            // Reaching definitions of the enclosing context still reach in the lambda
            // Since those definitions are [effectively] final, they actually can't be
            // killed, but they can be used in the lambda

            SpanInfo before = data;

            JavaNode lambdaBody = node.getChild(node.getNumChildren() - 1);
            // if it's an expression, then no assignments may occur in it,
            // but it can still use some variables of the context
            acceptOpt(lambdaBody, before.forkCapturingNonLocal());
            return before;
        }

        @Override
        public SpanInfo visit(ASTWhileStatement node, SpanInfo data) {
            return handleLoop(node, data, null, node.getCondition(), null, node.getBody(), true, null);
        }

        @Override
        public SpanInfo visit(ASTDoStatement node, SpanInfo data) {
            return handleLoop(node, data, null, node.getCondition(), null, node.getBody(), false, null);
        }

        @Override
        public SpanInfo visit(ASTForeachStatement node, SpanInfo data) {
            ASTStatement body = node.getBody();
            // the iterable expression
            JavaNode init = node.getChild(1);
            ASTVariableDeclaratorId foreachVar = ((ASTLocalVariableDeclaration) node.getChild(0)).iterator().next();
            return handleLoop(node, data, init, null, null, body, true, foreachVar);
        }

        @Override
        public SpanInfo visit(ASTForStatement node, SpanInfo data) {
            ASTStatement body = node.getBody();
            ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
            ASTExpression cond = node.getCondition();
            ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
            return handleLoop(node, data, init, cond, update, body, true, null);
        }


        private SpanInfo handleLoop(ASTLoopStatement loop,
                                    SpanInfo before,
                                    JavaNode init,
                                    ASTExpression cond,
                                    JavaNode update,
                                    ASTStatement body,
                                    boolean checkFirstIter,
                                    ASTVariableDeclaratorId foreachVar) {
            final GlobalAlgoState globalState = before.global;

            //todo while(true) and do {}while(true); are special-cased
            // by the compiler and there is no fork

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
                before.assign(foreachVar.getSymbol(), foreachVar);
            }


            // make the defs of the body reach the other parts of the loop,
            // including itself
            SpanInfo iter = acceptOpt(body, before.fork());

            if (foreachVar != null && iter.hasVar(foreachVar)) {
                // in foreach loops, the loop variable is reassigned on each update
                iter.assign(foreachVar.getSymbol(), foreachVar);
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
                result.deleteVar(foreachVar.getSymbol());
            }

            return result;
        }

        private void pushTargets(ASTLoopStatement loop, SpanInfo breakTarget, SpanInfo continueTarget) {
            GlobalAlgoState globalState = breakTarget.global;
            globalState.breakTargets.unnamedTargets.push(breakTarget);
            globalState.continueTargets.unnamedTargets.push(continueTarget);

            Node parent = loop.getParent();
            while (parent instanceof ASTLabeledStatement) {
                String label = ((ASTLabeledStatement) parent).getLabel();
                globalState.breakTargets.namedTargets.put(label, breakTarget);
                globalState.continueTargets.namedTargets.put(label, continueTarget);
                parent = parent.getParent();
            }
        }

        private SpanInfo popTargets(ASTLoopStatement loop, SpanInfo breakTarget, SpanInfo continueTarget) {
            GlobalAlgoState globalState = breakTarget.global;
            globalState.breakTargets.unnamedTargets.pop();
            globalState.continueTargets.unnamedTargets.pop();

            SpanInfo total = breakTarget.absorb(continueTarget);

            Node parent = loop.getParent();
            while (parent instanceof ASTLabeledStatement) {
                String label = ((ASTLabeledStatement) parent).getLabel();
                total = total.absorb(globalState.breakTargets.namedTargets.remove(label));
                total = total.absorb(globalState.continueTargets.namedTargets.remove(label));
                parent = parent.getParent();
            }
            return total;
        }

        private SpanInfo acceptOpt(JavaNode node, SpanInfo before) {
            return node == null ? before : node.acceptVisitor(this, before);
        }

        @Override
        public SpanInfo visit(ASTContinueStatement node, SpanInfo data) {
            return data.global.continueTargets.doBreak(data, node.getImage());
        }

        @Override
        public SpanInfo visit(ASTBreakStatement node, SpanInfo data) {
            return data.global.breakTargets.doBreak(data, node.getImage());
        }

        @Override
        public SpanInfo visit(ASTYieldStatement node, SpanInfo data) {
            super.visit(node, data); // visit expression

            // treat as break, ie abrupt completion + link reaching defs to outer context
            return data.global.breakTargets.doBreak(data, null);
        }


        // both of those exit the scope of the method/ctor, so their assignments go dead

        @Override
        public SpanInfo visit(ASTThrowStatement node, SpanInfo data) {
            super.visit(node, data);
            return data.abruptCompletionByThrow(false);
        }

        @Override
        public SpanInfo visit(ASTReturnStatement node, SpanInfo data) {
            super.visit(node, data);
            return data.abruptCompletion(null);
        }

        // following deals with assignment

        @Override
        public SpanInfo visit(ASTFormalParameter node, SpanInfo data) {
            data.declareBlank(node.getVarId());
            return data;
        }

        @Override
        public SpanInfo visit(ASTCatchParameter node, SpanInfo data) {
            data.declareBlank(node.getVarId());
            return data;
        }

        @Override
        public SpanInfo visit(ASTVariableDeclarator node, SpanInfo data) {
            JVariableSymbol var = node.getVarId().getSymbol();
            ASTExpression rhs = node.getInitializer();
            if (rhs != null) {
                rhs.acceptVisitor(this, data);
                data.assign(var, rhs);
            } else {
                data.declareBlank(node.getVarId());
            }
            return data;
        }


        @Override
        public SpanInfo visit(ASTUnaryExpression node, SpanInfo data) {
            data = acceptOpt(node.getOperand(), data);

            if (node.getOperator().isPure()) {
                return data;
            } else {
                return processAssignment(node.getOperand(), node, true, data);
            }
        }

        @Override
        public SpanInfo visit(ASTAssignmentExpression node, SpanInfo data) {
            // visit operands in order
            data = acceptOpt(node.getRightOperand(), data);
            data = acceptOpt(node.getLeftOperand(), data);

            return processAssignment(node.getLeftOperand(),
                                     node.getRightOperand(),
                                     node.getOperator().isCompound(),
                                     data);

        }

        private SpanInfo processAssignment(ASTExpression lhs, // LHS or unary operand
                                           ASTExpression rhs,  // RHS or unary
                                           boolean useBeforeAssigning,
                                           SpanInfo result) {

            if (lhs instanceof ASTNamedReferenceExpr) {
                JVariableSymbol lhsVar = ((ASTNamedReferenceExpr) lhs).getReferencedSym();
                if (lhsVar != null
                    && (lhsVar instanceof JLocalVariableSymbol
                    || isRelevantField(lhs))) {

                    if (useBeforeAssigning) {
                        // compound assignment, to use BEFORE assigning
                        result.use(lhsVar, (ASTNamedReferenceExpr) lhs);
                    }

                    result.assign(lhsVar, rhs);
                }
            }
            return result;
        }

        private boolean isRelevantField(ASTExpression lhs) {
            if (!(lhs instanceof ASTNamedReferenceExpr)) {
                return false;
            }
            return trackThisInstance() && JavaRuleUtil.isThisFieldAccess(lhs)
                || trackStaticFields() && isStaticFieldOfThisClass(((ASTNamedReferenceExpr) lhs).getReferencedSym());
        }

        private boolean isStaticFieldOfThisClass(JVariableSymbol var) {
            return var instanceof JFieldSymbol
                && ((JFieldSymbol) var).isStatic()
                // must be non-null
                && enclosingClassScope.equals(((JFieldSymbol) var).getEnclosingClass());
        }

        private static JVariableSymbol getVarIfUnaryAssignment(ASTUnaryExpression node) {
            ASTExpression operand = node.getOperand();
            if (!node.getOperator().isPure() && operand instanceof ASTNamedReferenceExpr) {
                return ((ASTNamedReferenceExpr) operand).getReferencedSym();
            }
            return null;
        }

        // variable usage

        @Override
        public SpanInfo visit(ASTVariableAccess node, SpanInfo data) {
            if (node.getAccessType() == AccessType.READ) {
                data.use(node.getReferencedSym(), node);
            }
            return data;
        }

        @Override
        public SpanInfo visit(ASTFieldAccess node, SpanInfo data) {
            data = node.getQualifier().acceptVisitor(this, data);

            if (isRelevantField(node) && node.getAccessType() == AccessType.READ) {
                data.use(node.getReferencedSym(), node);
            }
            return data;
        }

        @Override
        public SpanInfo visit(ASTThisExpression node, SpanInfo data) {
            if (trackThisInstance() && !(node.getParent() instanceof ASTFieldAccess)) {
                data.recordThisLeak(true, enclosingClassScope, node);
            }
            return data;
        }

        @Override
        public SpanInfo visit(ASTMethodCall node, SpanInfo state) {
            return visitInvocationExpr(node, state);
        }

        @Override
        public SpanInfo visit(ASTConstructorCall node, SpanInfo state) {
            state = visitInvocationExpr(node, state);
            acceptOpt(node.getAnonymousClassDeclaration(), state);
            return state;
        }

        private <T extends InvocationNode & QualifiableExpression> SpanInfo visitInvocationExpr(T node, SpanInfo state) {
            state = acceptOpt(node.getQualifier(), state);
            state = acceptOpt(node.getArguments(), state);

            // todo In 7.0, with the precise type/overload resolution, we
            //  could only target methods that throw checked exceptions
            //  (unless some catch block catches an unchecked exceptions)

            state.abruptCompletionByThrow(true); // this is a noop if we're outside a try block that has catch/finally
            return state;
        }


        // ctor/initializer handling

        @Override
        public SpanInfo visitTypeDecl(ASTAnyTypeDeclaration node, SpanInfo data) {
            processInitializers(node.getDeclarations(), data, node.getSymbol());

            for (ASTBodyDeclaration decl : node.getDeclarations()) {
                if (decl instanceof ASTMethodDeclaration) {
                    ASTMethodDeclaration method = (ASTMethodDeclaration) decl;
                    if (method.getBody() != null) {
                        SpanInfo span = data.forkCapturingNonLocal();
                        if (!method.isStatic()) {
                            span.declareSpecialFieldValues(node.getSymbol());
                        }
                        ONLY_LOCALS.acceptOpt(decl, span);
                    }
                } else if (decl instanceof ASTAnyTypeDeclaration) {
                    visitTypeDecl((ASTAnyTypeDeclaration) decl, data.forkEmptyNonLocal());
                }
            }
            return data; // type doesn't contribute anything to the enclosing control flow
        }

        private static void processInitializers(NodeStream<ASTBodyDeclaration> declarations,
                                                SpanInfo beforeLocal,
                                                JClassSymbol classSymbol) {

            ReachingDefsVisitor instanceVisitor = new ReachingDefsVisitor(classSymbol, false);
            ReachingDefsVisitor staticVisitor = new ReachingDefsVisitor(classSymbol, true);

            // All field initializers + instance initializers
            SpanInfo ctorHeader = beforeLocal.forkCapturingNonLocal();
            // All static field initializers + static initializers
            SpanInfo staticInit = beforeLocal.forkEmptyNonLocal();

            List<ASTConstructorDeclaration> ctors = new ArrayList<>();

            for (ASTBodyDeclaration declaration : declarations) {
                final boolean isStatic;
                if (declaration instanceof ASTFieldDeclaration) {
                    isStatic = ((ASTFieldDeclaration) declaration).isStatic();
                } else if (declaration instanceof ASTInitializer) {
                    isStatic = ((ASTInitializer) declaration).isStatic();
                } else if (declaration instanceof ASTConstructorDeclaration) {
                    ctors.add((ASTConstructorDeclaration) declaration);
                    continue;
                } else {
                    continue;
                }

                if (isStatic) {
                    staticInit = staticVisitor.acceptOpt(declaration, staticInit);
                } else {
                    ctorHeader = instanceVisitor.acceptOpt(declaration, ctorHeader);
                }
            }

            SpanInfo ctorEndState = ctors.isEmpty() ? ctorHeader : null;
            for (ASTConstructorDeclaration ctor : ctors) {
                SpanInfo state = instanceVisitor.acceptOpt(ctor, ctorHeader.forkCapturingNonLocal());
                ctorEndState = ctorEndState == null ? state : ctorEndState.absorb(state);
            }

            // assignments that reach the end of any constructor must be considered used
            useAllSelfFields(staticInit, ctorEndState, classSymbol, classSymbol.tryGetNode());
        }

        static void useAllSelfFields(@Nullable SpanInfo staticState, SpanInfo instanceState, JClassSymbol enclosingSym, JavaNode escapingNode) {
            for (JFieldSymbol field : enclosingSym.getDeclaredFields()) {
                if (field.isStatic()) {
                    if (staticState != null) {
                        staticState.assignOutOfScope(field, escapingNode);
                    }
                } else {
                    instanceState.assignOutOfScope(field, escapingNode);
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
            this(new HashSet<>(),
                 new HashSet<>(),
                 new HashMap<>());
        }
    }

    // Information about a variable in a code span.
    static class VarLocalInfo {

        // this is not modified so can be shared between different SpanInfos.
        final Set<AssignmentEntry> reachingDefs;

        VarLocalInfo(Set<AssignmentEntry> reachingDefs) {
            this.reachingDefs = reachingDefs;
        }

        // and produce an independent instance
        VarLocalInfo merge(VarLocalInfo other) {
            if (other == this) {
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

        final Map<JVariableSymbol, VarLocalInfo> symtable;
        private OptionalBool hasCompletedAbruptly = OptionalBool.NO;

        private SpanInfo(GlobalAlgoState global) {
            this(null, global, new HashMap<>());
        }

        private SpanInfo(SpanInfo parent,
                         GlobalAlgoState global,
                         Map<JVariableSymbol, VarLocalInfo> symtable) {
            this.parent = parent;
            this.global = global;
            this.symtable = symtable;
            this.myCatches = Collections.emptyList();
        }

        boolean hasVar(ASTVariableDeclaratorId var) {
            return symtable.containsKey(var.getSymbol());
        }

        void declareBlank(ASTVariableDeclaratorId id) {
            assign(id.getSymbol(), id);
        }

        void assign(JVariableSymbol var, JavaNode rhs) {
            assign(var, rhs, false, false);
        }

        void assign(JVariableSymbol var, JavaNode rhs, boolean outOfScope, boolean isFieldBeforeMethod) {
            ASTVariableDeclaratorId node = var.tryGetNode();
            if (node == null) {
                return; // we don't care about non-local declarations
            }
            AssignmentEntry entry = outOfScope || isFieldBeforeMethod
                                    ? new UnboundAssignment(var, node, rhs, isFieldBeforeMethod)
                                    : new AssignmentEntry(var, node, rhs);
            VarLocalInfo previous = symtable.put(var, new VarLocalInfo(Collections.singleton(entry)));
            if (previous != null) {
                // those assignments were overwritten ("killed")
                for (AssignmentEntry killed : previous.reachingDefs) {
                    if (killed.isBlankLocal()) {
                        continue;
                    }

                    global.killRecord.computeIfAbsent(killed, k -> new HashSet<>(1))
                                     .add(entry);
                }
            }
            global.allAssignments.add(entry);
        }

        void declareSpecialFieldValues(JClassSymbol sym) {
            List<JFieldSymbol> declaredFields = sym.getDeclaredFields();
            for (JFieldSymbol field : declaredFields) {
                ASTVariableDeclaratorId id = field.tryGetNode();
                if (id == null || !SingularFieldRule.mayBeSingular(id)) {
                    // useless to track final fields
                    // static fields are out of scope of this impl for now
                    continue;
                }

                assign(field, id, true, true);
            }
        }


        void assignOutOfScope(@Nullable JVariableSymbol var, JavaNode escapingNode) {
            if (var == null) {
                return;
            }
            use(var, null);
            assign(var, escapingNode, true, false);
        }

        void use(@Nullable JVariableSymbol var, ASTNamedReferenceExpr reachingDefSink) {
            if (var == null) {
                return;
            }
            VarLocalInfo info = symtable.get(var);
            // may be null for implicit assignments, like method parameter
            if (info != null) {
                global.usedAssignments.addAll(info.reachingDefs);
                if (reachingDefSink != null) {
                    ReachingDefinitionSet reaching = new ReachingDefinitionSet(new HashSet<>(info.reachingDefs));
                    reachingDefSink.getUserMap().set(REACHING_DEFS, reaching);
                }
            }
        }

        void deleteVar(JVariableSymbol var) {
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
        public void recordThisLeak(boolean thisIsLeaking, JClassSymbol enclosingClassSym, JavaNode escapingNode) {
            if (thisIsLeaking && enclosingClassSym != null) {
                // all reaching defs to fields until now may be observed
                ReachingDefsVisitor.useAllSelfFields(null, this, enclosingClassSym, escapingNode);
            }
        }

        // Forks duplicate this context, to preserve the reaching defs
        // of the current context while analysing a sub-block
        // Forks must be merged later if control flow merges again, see ::absorb

        SpanInfo fork() {
            return doFork(this, copyTable());
        }

        SpanInfo forkEmpty() {
            return doFork(this, new HashMap<>());
        }


        SpanInfo forkEmptyNonLocal() {
            return doFork(null, new HashMap<>());
        }

        SpanInfo forkCapturingNonLocal() {
            return doFork(null, copyTable());
        }

        private Map<JVariableSymbol, VarLocalInfo> copyTable() {
            return new HashMap<>(this.symtable);
        }

        private SpanInfo doFork(/*nullable*/ SpanInfo parent, Map<JVariableSymbol, VarLocalInfo> reaching) {
            return new SpanInfo(parent, this.global, reaching);
        }

        /** Abrupt completion for return, continue, break. */
        SpanInfo abruptCompletion(SpanInfo target) {
            // if target == null then this will unwind all the parents
            hasCompletedAbruptly = OptionalBool.YES;
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
            if (!byMethodCall) {
                hasCompletedAbruptly = OptionalBool.YES;
            }

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
            if (other == this || other == null || other.symtable.isEmpty()) {
                return this;
            }

            CollectionUtil.mergeMaps(this.symtable, other.symtable, VarLocalInfo::merge);
            this.hasCompletedAbruptly = mergeCertitude(this.hasCompletedAbruptly, other.hasCompletedAbruptly);
            return this;
        }

        private OptionalBool mergeCertitude(OptionalBool first, OptionalBool other) {
            if (first.isKnown() && other.isKnown()) {
                return first == other ? first : OptionalBool.UNKNOWN;
            }
            return OptionalBool.UNKNOWN;
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

    public static class AssignmentEntry implements Comparable<AssignmentEntry> {

        final JVariableSymbol var;
        final ASTVariableDeclaratorId node;

        // this is not necessarily an expression, it may be also the
        // variable declarator of a foreach loop
        final JavaNode rhs;

        AssignmentEntry(JVariableSymbol var, ASTVariableDeclaratorId node, JavaNode rhs) {
            this.var = var;
            this.node = node;
            this.rhs = rhs;
            // This may be overwritten repeatedly in loops, we probably don't care,
            // as normally they're created equal
            // Also for now we don't support getting a field.
            if ((isInitializer() || isBlankDeclaration()) && !isUnbound()) {
                node.getUserMap().set(VAR_DEFINITION, this);
            }
        }

        public boolean isInitializer() {
            return rhs.getParent() instanceof ASTVariableDeclarator
                && rhs.getIndexInParent() > 0;
        }

        public boolean isBlankDeclaration() {
            return rhs instanceof ASTVariableDeclaratorId;
        }

        public boolean isFieldDefaultValue() {
            return isBlankDeclaration() && isField();
        }

        /**
         * A blank local that has no value (ie not a catch param or formal).
         */
        public boolean isBlankLocal() {
            return isBlankDeclaration() && node.isLocalVariable();
        }

        public boolean isUnaryReassign() {
            return rhs instanceof ASTUnaryExpression
                && ReachingDefsVisitor.getVarIfUnaryAssignment((ASTUnaryExpression) rhs) == var;
        }

        @Override
        public int compareTo(AssignmentEntry o) {
            return this.rhs.compareLocation(o.rhs);
        }

        public int getLine() {
            return getLocation().getBeginLine();
        }

        public boolean isField() {
            return var instanceof JFieldSymbol;
        }

        public boolean isForeachVar() {
            return node.isLocalVariable() && node.ancestors().get(2) instanceof ASTForeachStatement;
        }

        public ASTVariableDeclaratorId getVarId() {
            return node;
        }

        public JavaNode getLocation() {
            return rhs;
        }

        /**
         * If true, then this "assignment" is not real. We conservatively
         * assume that the variable may have been set to another value by
         * a call to some external code.
         */
        public boolean isUnbound() {
            return false;
        }

        /**
         * If true, then this "assignment" is the placeholder value given
         * to an instance field before a method starts. This is a subset of
         * {@link #isUnbound()}.
         */
        public boolean isFieldAssignmentAtStartOfMethod() {
            return false;
        }

        /**
         * If true, then this "assignment" is the placeholder value given
         * to a non-final instance field after a ctor ends. This is a subset of
         * {@link #isUnbound()}.
         */
        public boolean isFieldAssignmentAtEndOfCtor() {
            return false;
        }

        @Override
        public String toString() {
            return var.getSimpleName() + " := " + rhs;
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
            return Objects.equals(var, that.var)
                && Objects.equals(rhs, that.rhs);
        }

        @Override
        public int hashCode() {
            return 31 * var.hashCode() + rhs.hashCode();
        }
    }

    static class UnboundAssignment extends AssignmentEntry {

        /**
         * If true, then this is the unknown value of a field
         * before an instance method call.
         */
        private final boolean isFieldStartValue;

        UnboundAssignment(JVariableSymbol var, ASTVariableDeclaratorId node, JavaNode rhs, boolean isFieldStartValue) {
            super(var, node, rhs);
            this.isFieldStartValue = isFieldStartValue;
        }

        @Override
        public boolean isUnbound() {
            return true;
        }

        @Override
        public boolean isFieldAssignmentAtStartOfMethod() {
            return isFieldStartValue;
        }

        @Override
        public boolean isFieldAssignmentAtEndOfCtor() {
            return rhs instanceof ASTAnyTypeDeclaration;
        }
    }
}

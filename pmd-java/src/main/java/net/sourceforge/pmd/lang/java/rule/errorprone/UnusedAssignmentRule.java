/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;


import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledRule;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class UnusedAssignmentRule extends AbstractJavaRule {

    /*
        TODO
           constructors + initializers

     */


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // This analysis can be used to check for unused variables easily
        // See reverted commit somewhere in the PR

        LivenessState bodyData = new LivenessState();

        LivenessState endData = (LivenessState) node.getBody().jjtAccept(new LivenessVisitor(), bodyData);

        if (endData.usedAssignments.size() < endData.allAssignments.size()) {
            HashSet<AssignmentEntry> unused = new HashSet<>(endData.allAssignments);
            unused.removeAll(endData.usedAssignments);
            // allAssignments is the unused assignments now

            for (AssignmentEntry entry : unused) {
                addViolationWithMessage(data, entry.rhs, "The value assigned to variable ''{0}'' is never used", new Object[] {entry.var.getImage()});
            }
        }

        return super.visit(node, data);
    }

    private static class LivenessVisitor extends JavaParserVisitorAdapter {

        private final TargetStack breakTargets = new TargetStack();
        // continue jumps to the condition check, while break jumps to after the loop
        private final TargetStack continueTargets = new TargetStack();

        // following deals with control flow

        @Override
        public Object visit(JavaNode node, Object data) {

            for (JavaNode child : node.children()) {
                // each output is passed as input to the next (most relevant for blocks)
                data = child.jjtAccept(this, data);
            }

            return data;
        }

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            return processSwitch(node, (LivenessState) data, node.getTestedExpression());
        }

        @Override
        public Object visit(ASTSwitchExpression node, Object data) {
            return processSwitch(node, (LivenessState) data, node.getChild(0));
        }

        private LivenessState processSwitch(JavaNode switchLike, LivenessState data, JavaNode testedExpr) {
            LivenessState before = acceptOpt(testedExpr, data);

            breakTargets.push(before.fork());

            LivenessState current = before;
            for (int i = 1; i < switchLike.getNumChildren(); i++) {
                JavaNode child = switchLike.getChild(i);
                if (child instanceof ASTSwitchLabel) {
                    current = before.fork().join(current);
                } else if (child instanceof ASTSwitchLabeledRule) {
                    // 'current' stays == 'before' so that the final join does nothing
                    current = acceptOpt(child.getChild(1), before.fork());
                    current = breakTargets.doBreak(current, null); // process this as if it was followed by a break
                } else {
                    // statement in a regular fallthrough switch block
                    current = acceptOpt(child, current);
                }
            }

            before = breakTargets.pop();

            // join with the last state, which is the exit point of the
            // switch, if it's not closed by a break;
            return before.join(current);
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            LivenessState before = acceptOpt(node.getCondition(), (LivenessState) data);

            LivenessState thenData = acceptOpt(node.getThenBranch(), before.fork());
            LivenessState elseData = acceptOpt(node.getElseBranch(), before.fork());

            return thenData.join(elseData);
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            return handleLoop(node, (LivenessState) data, null, node.getCondition(), null, node.getBody(), true);
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            return handleLoop(node, (LivenessState) data, null, node.getCondition(), null, node.getBody(), false);
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ASTStatement body = node.getBody();
            if (node.isForeach()) {
                // the iterable expression
                JavaNode init = node.getChild(1);
                return handleLoop(node, (LivenessState) data, init, null, null, body, true);
            } else {
                ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
                ASTExpression cond = node.getCondition();
                ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
                return handleLoop(node, (LivenessState) data, init, cond, update, body, true);
            }
        }


        private LivenessState handleLoop(JavaNode loop,
                                         LivenessState before,
                                         JavaNode init,
                                         JavaNode cond,
                                         JavaNode update,
                                         JavaNode body,
                                         boolean checkFirstIter) {

            // perform a few "iterations", to make sure that assignments in
            // the body can affect themselves in the next iteration, and
            // that they affect the condition, etc

            before = acceptOpt(init, before);
            if (checkFirstIter) {
                before = acceptOpt(cond, before);
            }

            LivenessState breakTarget = before.forkEmpty();
            LivenessState continueTarget = before.forkEmpty();

            pushTargets(loop, breakTarget, continueTarget);

            LivenessState iter = acceptOpt(body, before.fork());
            // make the body live in the other parts of the loop,
            // including itself
            iter = acceptOpt(update, iter);
            iter = acceptOpt(cond, iter);
            iter = acceptOpt(body, iter);


            breakTarget = breakTargets.peek();
            continueTarget = continueTargets.peek();
            if (!continueTarget.liveAssignments.isEmpty()) {
                // make assignments that are only live before a continue
                // live inside the other parts of the loop
                continueTarget = acceptOpt(cond, continueTarget);
                continueTarget = acceptOpt(body, continueTarget);
                continueTarget = acceptOpt(update, continueTarget);
            }

            LivenessState result = popTargets(loop, breakTarget, continueTarget);
            result = result.join(iter);
            if (checkFirstIter) {
                result = result.join(before);
            }

            return result;
        }

        private void pushTargets(JavaNode loop, LivenessState breakTarget, LivenessState continueTarget) {
            breakTargets.unnamedTargets.push(breakTarget);
            continueTargets.unnamedTargets.push(continueTarget);

            JavaNode parent = loop.getNthParent(2);
            while (parent instanceof ASTLabeledStatement) {
                String label = parent.getImage();
                breakTargets.namedTargets.put(label, breakTarget);
                continueTargets.namedTargets.put(label, continueTarget);
                parent = parent.getNthParent(2);
            }
        }

        private LivenessState popTargets(JavaNode loop, LivenessState breakTarget, LivenessState continueTarget) {
            breakTargets.unnamedTargets.pop();
            continueTargets.unnamedTargets.pop();

            LivenessState total = breakTarget.join(continueTarget);

            JavaNode parent = loop.getNthParent(2);
            while (parent instanceof ASTLabeledStatement) {
                String label = parent.getImage();
                total = total.join(breakTargets.namedTargets.remove(label));
                total = total.join(continueTargets.namedTargets.remove(label));
                parent = parent.getNthParent(2);
            }
            return total;
        }

        private LivenessState acceptOpt(JavaNode node, LivenessState before) {
            return node == null ? before : (LivenessState) node.jjtAccept(this, before);
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            return continueTargets.doBreak((LivenessState) data, node.getImage());
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            return breakTargets.doBreak((LivenessState) data, node.getImage());
        }

        @Override
        public Object visit(ASTYieldStatement node, Object data) {
            super.visit(node, data); // visit expression
            // treat as break, ie abrupt completion + link live vars to outer context
            return breakTargets.doBreak((LivenessState) data, null);
        }

        private LivenessState doBreak(LivenessState data, Deque<LivenessState> targets) {
            // basically, assignments that are live at the point of the break
            // are also live after the break (wherever it lands)
            targets.push(targets.getFirst().join(data));
            return data.abruptCompletion();
        }


        // both of those exit the scope of the method/ctor, so their assignments go dead

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            super.visit(node, data);
            return ((LivenessState) data).abruptCompletion();
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            super.visit(node, data);
            return ((LivenessState) data).abruptCompletion();
        }

        // following deals with assignment


        @Override
        public Object visit(ASTVariableDeclarator node, Object data) {
            VariableNameDeclaration var = node.getVariableId().getNameDeclaration();
            ASTVariableInitializer rhs = node.getInitializer();
            if (rhs != null) {
                rhs.jjtAccept(this, data);
                ((LivenessState) data).assign(var, rhs);
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
            LivenessState result = (LivenessState) data;
            if (node.getNumChildren() == 3) {
                // assignment
                assert node.getChild(1) instanceof ASTAssignmentOperator;

                // visit the rhs as it is evaluated before
                JavaNode rhs = node.getChild(2);
                result = acceptOpt(rhs, result);

                VariableNameDeclaration lhsVar = getLhsVar(node.getChild(0), true);
                if (lhsVar != null) {
                    if (node.getChild(1).getImage().length() >= 2) {
                        // compound assignment, to use BEFORE assigning
                        ((LivenessState) data).use(lhsVar);
                    }

                    ((LivenessState) data).assign(lhsVar, rhs);
                } else {
                    result = acceptOpt(node.getChild(0), result);
                }
                return result;
            } else {
                return super.visit(node, data);
            }
        }

        @Override
        public Object visit(ASTPreDecrementExpression node, Object data) {
            return checkIncOrDecrement(node, (LivenessState) data);
        }

        @Override
        public Object visit(ASTPreIncrementExpression node, Object data) {
            return checkIncOrDecrement(node, (LivenessState) data);
        }

        @Override
        public Object visit(ASTPostfixExpression node, Object data) {
            return checkIncOrDecrement(node, (LivenessState) data);
        }

        private LivenessState checkIncOrDecrement(JavaNode unary, LivenessState data) {
            VariableNameDeclaration var = getLhsVar(unary.getChild(0), true);
            if (var != null) {
                data.use(var);
                data.assign(var, unary);
            }
            return data;
        }

        // variable usage

        @Override
        public Object visit(ASTPrimaryExpression node, Object data) {
            super.visit(node, data); // visit subexpressions

            VariableNameDeclaration var = getLhsVar(node, false);
            if (var != null) {
                ((LivenessState) data).use(var);
            }
            return data;
        }

        private VariableNameDeclaration getLhsVar(JavaNode primary, boolean inLhs) {
            if (primary instanceof ASTPrimaryExpression) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primary.getChild(0);

                if (prefix.usesThisModifier()) {
                    if (primary.getNumChildren() != 2 && inLhs) {
                        return null;
                    }
                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) primary.getChild(1);
                    if (suffix.isArguments() || suffix.isArrayDereference()) {
                        return null;
                    }
                    return findVar(primary.getScope(), true, substringBeforeFirst(suffix.getImage(), '.'));
                } else {
                    if (inLhs && primary.getNumChildren() > 1) {
                        return null;
                    }

                    if (prefix.getChild(0) instanceof ASTName) {
                        return findVar(prefix.getScope(), false, substringBeforeFirst(prefix.getChild(0).getImage(), '.'));
                    }
                }
            }

            return null;
        }

        private static String substringBeforeFirst(String str, char delim) {
            int i = str.indexOf(delim);
            return i < 0 ? str : str.substring(0, i);
        }

        private VariableNameDeclaration findVar(Scope scope, boolean isThis, String name) {
            if (name == null) {
                return null;
            }
            if (isThis) {
                scope = scope.getEnclosingScope(ClassScope.class);
            }

            while (scope != null) {
                for (VariableNameDeclaration decl : scope.getDeclarations(VariableNameDeclaration.class).keySet()) {
                    if (decl.getImage().equals(name)) {
                        return decl;
                    }
                }

                scope = scope.getParent();
            }

            return null;
        }
    }

    private static class LivenessState {

        final Set<AssignmentEntry> allAssignments;
        final Set<AssignmentEntry> usedAssignments;

        final Map<VariableNameDeclaration, Set<AssignmentEntry>> liveAssignments;

        private LivenessState() {
            this(new HashSet<AssignmentEntry>(), new HashSet<AssignmentEntry>(), new HashMap<VariableNameDeclaration, Set<AssignmentEntry>>());
        }

        private LivenessState(Set<AssignmentEntry> allAssignments,
                              Set<AssignmentEntry> usedAssignments,
                              Map<VariableNameDeclaration, Set<AssignmentEntry>> liveAssignments) {
            this.allAssignments = allAssignments;
            this.usedAssignments = usedAssignments;
            this.liveAssignments = liveAssignments;
        }

        void assign(VariableNameDeclaration var, JavaNode rhs) {
            AssignmentEntry entry = new AssignmentEntry(var, rhs);
            liveAssignments.put(var, Collections.singleton(entry)); // kills the previous value
            allAssignments.add(entry);
        }

        void use(VariableNameDeclaration var) {
            Set<AssignmentEntry> live = liveAssignments.get(var);
            // may be null for implicit assignments, like method parameter
            if (live != null) {
                usedAssignments.addAll(live);
            }
        }

        LivenessState fork() {
            return new LivenessState(this.allAssignments, this.usedAssignments, new HashMap<>(this.liveAssignments));
        }

        LivenessState forkEmpty() {
            return new LivenessState(this.allAssignments, this.usedAssignments, new HashMap<>());
        }

        LivenessState abruptCompletion() {
            this.liveAssignments.clear();
            return this;
        }


        LivenessState join(LivenessState sub) {
            // Merge live assignments of forked scopes
            if (sub == this || sub.liveAssignments.isEmpty()) {
                return this;
            }

            for (VariableNameDeclaration var : this.liveAssignments.keySet()) {
                Set<AssignmentEntry> myAssignments = this.liveAssignments.get(var);
                Set<AssignmentEntry> subScopeAssignments = sub.liveAssignments.get(var);
                if (subScopeAssignments == null) {
                    continue;
                }
                joinLive(var, myAssignments, subScopeAssignments);
            }

            for (VariableNameDeclaration var : sub.liveAssignments.keySet()) {
                Set<AssignmentEntry> subScopeAssignments = sub.liveAssignments.get(var);
                Set<AssignmentEntry> myAssignments = this.liveAssignments.get(var);
                if (myAssignments == null) {
                    this.liveAssignments.put(var, subScopeAssignments);
                    continue;
                }
                joinLive(var, myAssignments, subScopeAssignments);
            }

            return this;
        }

        private void joinLive(VariableNameDeclaration var, Set<AssignmentEntry> live1, Set<AssignmentEntry> live2) {
            Set<AssignmentEntry> newLive = new HashSet<>(live1.size() + live2.size());
            newLive.addAll(live2);
            newLive.addAll(live1);
            this.liveAssignments.put(var, newLive);
        }

        LivenessState join(Iterable<LivenessState> scopes) {
            for (LivenessState sub : scopes) {
                this.join(sub);
            }
            return this;
        }

        @Override
        public String toString() {
            return liveAssignments.toString();
        }
    }

    static class TargetStack {

        final Deque<LivenessState> unnamedTargets = new ArrayDeque<>();
        final Map<String, LivenessState> namedTargets = new HashMap<>();


        void push(LivenessState state) {
            unnamedTargets.push(state);
        }

        LivenessState pop() {
            return unnamedTargets.pop();
        }

        LivenessState peek() {
            return unnamedTargets.getFirst();
        }

        LivenessState doBreak(LivenessState data,/* nullable */ String label) {
            // basically, assignments that are live at the point of the break
            // are also live after the break (wherever it lands)
            if (label == null) {
                unnamedTargets.push(unnamedTargets.getFirst().join(data));
            } else {
                LivenessState target = namedTargets.get(label);
                if (target != null) {
                    // otherwise CT error
                    target.join(data);
                }
            }
            return data.abruptCompletion();
        }
    }

    static class AssignmentEntry {

        final VariableNameDeclaration var;
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
            return Objects.hash(rhs);
        }
    }
}

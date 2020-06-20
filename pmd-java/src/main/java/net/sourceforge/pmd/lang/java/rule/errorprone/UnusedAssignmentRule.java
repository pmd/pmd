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
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
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
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class UnusedAssignmentRule extends AbstractJavaRule {

    /*
        TODO
           named break targets
           continue;
           yield
           constructors + initializers

     */


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        ScopeData bodyData = new ScopeData();

        //        for (ASTFormalParameter param : node.getFormalParameters()) {
        //            bodyData.varsThatMustBeUsed.add(param.getVariableDeclaratorId().getNameDeclaration());
        //        }

        ScopeData endData = (ScopeData) node.getBody().jjtAccept(new LivenessVisitor(), bodyData);

        if (endData.usedAssignments.size() < endData.allAssignments.size()) {
            HashSet<AssignmentEntry> unused = new HashSet<>(endData.allAssignments);
            unused.removeAll(endData.usedAssignments);
            // allAssignments is the unused assignments now

            for (AssignmentEntry entry : unused) {
                addViolationWithMessage(data, entry.rhs, "The value assigned to variable ''{0}'' is never used", new Object[] {entry.var.getImage()});
            }
        }
        //        if (!endData.varsThatMustBeUsed.isEmpty()) {
        //            HashSet<VariableNameDeclaration> assignedVars = new HashSet<>();
        //            for (AssignmentEntry assignment : endData.allAssignments) {
        //                assignedVars.add(assignment.var);
        //            }

        //            for (VariableNameDeclaration var : endData.varsThatMustBeUsed) {
        //                if (assignedVars.contains(var)) {
        //                    addViolationWithMessage(data, var.getNode(), "The variable ''{0}'' is assigned, but never accessed", new Object[] {var.getImage()});
        //                } else {
        //                    addViolationWithMessage(data, var.getNode(), "The variable ''{0}'' is never used", new Object[] {var.getImage()});
        //                }
        //            }
        //        }

        return super.visit(node, data);
    }

    private static class LivenessVisitor extends JavaParserVisitorAdapter {

        private final Deque<ScopeData> unnamedBreakTargets = new ArrayDeque<>();
        private final Map<String, ScopeData> namedBreakTargets = new HashMap<>();

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
            return processSwitch(node, (ScopeData) data, node.getTestedExpression());
        }

        @Override
        public Object visit(ASTSwitchExpression node, Object data) {
            return processSwitch(node, (ScopeData) data, node.getChild(0));
        }

        private ScopeData processSwitch(JavaNode switchLike, ScopeData data, JavaNode testedExpr) {
            ScopeData before = acceptOpt(testedExpr, data);

            unnamedBreakTargets.push(before.fork());

            ScopeData current = before;
            for (int i = 1; i < switchLike.getNumChildren(); i++) {
                JavaNode child = switchLike.getChild(i);
                if (child instanceof ASTSwitchLabel) {
                    current = before.fork().join(current);
                } else if (child instanceof ASTSwitchLabeledRule) {
                    // 'current' stays == 'before' so that the final join does nothing
                    current = acceptOpt(child.getChild(1), before.fork());
                    current = doBreak(current); // process this as if it was followed by a break
                } else {
                    // statement in a regular fallthrough switch block
                    current = acceptOpt(child, current);
                }
            }

            before = unnamedBreakTargets.pop();

            // join with the last state, which is the exit point of the
            // switch, if it's not closed by a break;
            return before.join(current);
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            ScopeData before = acceptOpt(node.getCondition(), (ScopeData) data);

            ScopeData thenData = acceptOpt(node.getThenBranch(), before.fork());
            ScopeData elseData = acceptOpt(node.getElseBranch(), before.fork());

            return thenData.join(elseData);
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            // perform a few "iterations", to make sure that assignments in
            // the body can affect themselves in the next iteration, and
            // that they affect the condition
            ScopeData before = acceptOpt(node.getCondition(), (ScopeData) data);

            unnamedBreakTargets.push(before);

            ScopeData iter = acceptOpt(node.getBody(), before.fork());
            iter = acceptOpt(node.getCondition(), iter);
            iter = acceptOpt(node.getBody(), iter);

            unnamedBreakTargets.pop();

            return before.join(iter);
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            // same as while but don't check the condition first
            ScopeData before = (ScopeData) data;

            unnamedBreakTargets.push(before);

            ScopeData iter = acceptOpt(node.getBody(), before.fork());
            iter = acceptOpt(node.getCondition(), iter);
            iter = acceptOpt(node.getBody(), iter);

            unnamedBreakTargets.pop();

            return before.join(iter);
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ASTStatement body = node.getBody();
            if (node.isForeach()) {
                // the iterable expression
                ScopeData before = (ScopeData) node.getChild(1).jjtAccept(this, data);

                unnamedBreakTargets.push(before);

                ScopeData iter = acceptOpt(body, before.fork());
                iter = acceptOpt(body, iter); // the body must be able to affect itself

                unnamedBreakTargets.pop();

                return before.join(iter);
            } else {
                ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
                ASTExpression cond = node.getCondition();
                ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);

                ScopeData before = (ScopeData) data;
                before = acceptOpt(init, before);
                before = acceptOpt(cond, before);

                unnamedBreakTargets.push(before);

                ScopeData iter = acceptOpt(body, before.fork());
                iter = acceptOpt(update, iter);
                iter = acceptOpt(cond, iter);
                iter = acceptOpt(body, iter); // the body must be able to affect itself

                unnamedBreakTargets.pop();

                return before.join(iter);
            }
        }

        private ScopeData acceptOpt(JavaNode node, ScopeData before) {
            return node == null ? before : (ScopeData) node.jjtAccept(this, before);
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            if (node.getImage() == null) {
                return doBreak((ScopeData) data);
            } else {
                // TODO
                return ((ScopeData) data).abruptCompletion();
            }
        }

        public ScopeData doBreak(ScopeData data) {
            unnamedBreakTargets.getFirst().join(data);
            return data.abruptCompletion();
        }

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            super.visit(node, data);
            return ((ScopeData) data).abruptCompletion();
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            super.visit(node, data);
            return ((ScopeData) data).abruptCompletion();
        }

        // following deals with assignment


        @Override
        public Object visit(ASTVariableDeclarator node, Object data) {
            VariableNameDeclaration var = node.getVariableId().getNameDeclaration();
            ASTVariableInitializer rhs = node.getInitializer();
            if (rhs != null) {
                rhs.jjtAccept(this, data);
                ((ScopeData) data).assign(var, rhs);
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
            ScopeData result = (ScopeData) data;
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
                        ((ScopeData) data).use(lhsVar);
                    }

                    ((ScopeData) data).assign(lhsVar, rhs);
                } else {
                    result = acceptOpt(node.getChild(0), result);
                }
                return result;
            } else {
                return super.visit(node, data);
            }
        }

        // variable usage

        @Override
        public Object visit(ASTPrimaryExpression node, Object data) {
            super.visit(node, data); // visit subexpressions

            VariableNameDeclaration var = getLhsVar(node, false);
            if (var != null) {
                ((ScopeData) data).use(var);
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

    private static class ScopeData {

        // final Set<VariableNameDeclaration> varsThatMustBeUsed = new HashSet<>();

        final Set<AssignmentEntry> allAssignments;
        final Set<AssignmentEntry> usedAssignments;

        final Map<VariableNameDeclaration, Set<AssignmentEntry>> liveAssignments;

        private ScopeData() {
            this(new HashSet<AssignmentEntry>(), new HashSet<AssignmentEntry>(), new HashMap<VariableNameDeclaration, Set<AssignmentEntry>>());
        }

        private ScopeData(Set<AssignmentEntry> allAssignments,
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

        ScopeData fork() {
            return new ScopeData(this.allAssignments, this.usedAssignments, new HashMap<>(this.liveAssignments));
        }

        ScopeData abruptCompletion() {
            this.liveAssignments.clear();
            return this;
        }

        ScopeData join(ScopeData sub) {
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

        ScopeData join(Iterable<ScopeData> scopes) {
            for (ScopeData sub : scopes) {
                this.join(sub);
            }
            return this;
        }

        @Override
        public String toString() {
            return liveAssignments.toString();
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

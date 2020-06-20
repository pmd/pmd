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
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
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

    static class LivenessVisitor extends JavaParserVisitorAdapter {


        private final Deque<ScopeData> breakAddresses = new ArrayDeque<>();
        private final Map<String, ScopeData> namedBreaks = new HashMap<>();

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
        public Object visit(ASTIfStatement node, Object data) {
            ScopeData before = (ScopeData) node.getCondition().jjtAccept(this, data);

            ScopeData thenData = before.fork();
            thenData = (ScopeData) node.getThenBranch().jjtAccept(this, thenData);
            if (node.hasElse()) {
                ScopeData elseData = (ScopeData) node.getElseBranch().jjtAccept(this, before.fork());
                return thenData.join(elseData);
            } else {
                return before.join(thenData);
            }
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            ScopeData before = (ScopeData) node.getCondition().jjtAccept(this, data);

            ScopeData iter = (ScopeData) node.getBody().jjtAccept(this, before.fork());
            iter = (ScopeData) node.getCondition().jjtAccept(this, iter);
            iter = (ScopeData) node.getBody().jjtAccept(this, iter);

            return before.join(iter);
        }

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            data = super.visit(node, data);
            return ((ScopeData) data).abruptCompletion();
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            data = super.visit(node, data);
            return ((ScopeData) data).abruptCompletion();
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            data = super.visit(node, data);
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
            if (node.getNumChildren() == 3) {
                // assignment
                assert node.getChild(1) instanceof ASTAssignmentOperator;

                // visit the rhs as it is evaluated before
                ASTExpression rhs = (ASTExpression) node.getChild(2);
                rhs.jjtAccept(this, data);

                VariableNameDeclaration lhsVar = getLhsVar(node.getChild(0), true);
                if (lhsVar != null) {
                    if (node.getChild(1).getImage().length() >= 2) {
                        // compound assignment, to use BEFORE assigning
                        ((ScopeData) data).use(lhsVar);
                    }

                    ((ScopeData) data).assign(lhsVar, rhs);
                } else {
                    node.getChild(0).jjtAccept(this, data);
                }
                return data;
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
                    return findVar(primary.getScope(), true, suffix.getImage());
                } else {
                    if (inLhs && primary.getNumChildren() > 1) {
                        return null;
                    }

                    if (prefix.getChild(0) instanceof ASTName) {
                        return findVar(prefix.getScope(), false, prefix.getChild(0).getImage());
                    }
                }
            }

            return null;
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
            if (sub != this) {
                for (VariableNameDeclaration var : this.liveAssignments.keySet()) {
                    Set<AssignmentEntry> myAssignments = this.liveAssignments.get(var);
                    Set<AssignmentEntry> subScopeAssignments = sub.liveAssignments.get(var);
                    if (subScopeAssignments == null) {
                        this.liveAssignments.put(var, myAssignments);
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
    }

    static class AssignmentEntry {
        final VariableNameDeclaration var;
        final JavaNode rhs;

        AssignmentEntry(VariableNameDeclaration var, JavaNode rhs) {
            this.var = var;
            this.rhs = rhs;
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

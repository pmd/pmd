/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import java.util.Iterator;
import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;


/**
 * Visitor for the Cognitive Complexity metric.
 *
 * @author Denis Borovikov
 * @since 6.35.0
 */
public class CognitiveComplexityVisitor extends JavaVisitorBase<CognitiveComplexityVisitor.State, Void> {

    /** Instance. */
    public static final CognitiveComplexityVisitor INSTANCE = new CognitiveComplexityVisitor();

    public enum BooleanOp {AND, OR}

    public static class State {


        private int complexity = 0;
        private int nestingLevel = 0;

        private BooleanOp currentBooleanOperation = null;
        private Stack<ASTMethodDeclaration> methodStack = new Stack<>();

        public int getComplexity() {
            return complexity;
        }

        void hybridComplexity() {
            complexity++;
            nestingLevel++;
        }

        void fundamentalComplexity() {
            complexity++;
        }

        void structuralComplexity() {
            complexity++;
            complexity += nestingLevel;
            nestingLevel++;
        }

        void increaseNestingLevel() {
            nestingLevel++;
        }

        void decreaseNestingLevel() {
            nestingLevel--;
        }

        void booleanOperation(BooleanOp op) {
            if (currentBooleanOperation != op) {
                if (op != null) {
                    fundamentalComplexity();
                }

                currentBooleanOperation = op;
            }
        }

        void pushMethod(ASTMethodDeclaration calledMethod) {
            methodStack.push(calledMethod);
        }

        void popMethod() {
            methodStack.pop();
        }

        void callMethod(ASTMethodDeclaration calledMethod) {
            if (methodStack.contains(calledMethod)) {
                // This means it's a recursive call.
                // Note that we consider the entire stack and not just the top.
                // This is an arbitrary decision that may cause FPs...
                // Specifically it matters when anonymous classes are involved.
                // void outer() {
                //     Runnable r = new Runnable(){
                //       public void run(){
                //         outer();
                //       }
                //     };
                //
                //     r = () -> outer();
                // }
                //
                // If we only consider the top of the stack, then within the anonymous class, `outer()`
                // is not counted as a recursive call. This means the anonymous class
                // has lower complexity than the lambda (because in the lambda the top
                // of the stack is `outer`).
                //
                // TODO Arguably this could be improved by adding a complexity point
                // for anonymous classes, because they're syntactically heavyweight.
                // This would incentivize using lambdas.
                fundamentalComplexity();
            }
        }
    }

    @Override
    public Void visit(ASTIfStatement node, State state) {
        boolean isNotElseIf = !(node.getNthParent(2) instanceof ASTIfStatement);

        node.getCondition().acceptVisitor(this, state);

        if (isNotElseIf) {
            state.structuralComplexity();
        }
        node.getThenBranch().acceptVisitor(this, state);
        if (isNotElseIf) {
            state.decreaseNestingLevel();
        }

        if (node.hasElse()) {
            state.hybridComplexity();
            node.getElseBranch().acceptVisitor(this, state);
            state.decreaseNestingLevel();
        }

        return null;
    }

    @Override
    public Void visit(ASTForStatement node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTContinueStatement node, State state) {

        // hack to detect if there is a label
        boolean hasLabel = node.getImage() != null;

        if (hasLabel) {
            state.fundamentalComplexity();
        }
        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTBreakStatement node, State state) {

        // hack to detect if there is a label
        boolean hasLabel = node.getImage() != null;

        if (hasLabel) {
            state.fundamentalComplexity();
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTWhileStatement node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTCatchClause node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTDoStatement node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTConditionalExpression node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTInfixExpression node, State state) {
        switch (node.getOperator()) {
        case CONDITIONAL_AND:
            state.booleanOperation(BooleanOp.AND);
            break;
        case CONDITIONAL_OR:
            state.booleanOperation(BooleanOp.OR);
            break;
        }
        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTUnaryExpression node, State state) {

        if (node.getOperator() == UnaryOp.NEGATION) {
            state.booleanOperation(null);
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTBlock node, State state) {

        for (JavaNode child : node.children()) {
            // This needs to happen because the current 'run' of boolean operations is terminated
            // once we finish a statement.
            state.booleanOperation(null);

            child.acceptVisitor(this, state);
        }

        return null;
    }

    @Override
    public Void visit(ASTMethodDeclaration node, State state) {

        state.pushMethod(node);
        super.visit(node, state);
        state.popMethod();

        return null;
    }

    @Override
    public Void visit(ASTMethodCall node, State state) {

        // check if this primary prefix is a method call
        Iterator<? extends JavaNode> it = node.children().iterator();
        if (it.hasNext()) {
            final JavaNode child = it.next();
            if (child instanceof ASTName) {
                ASTName name = (ASTName) child;
                if (name.getNameDeclaration() instanceof MethodNameDeclaration) {
                    ASTMethodDeclaration parent = (ASTMethodDeclaration) name.getNameDeclaration().getNode().getParent();
                    state.callMethod(parent);
                }
            }
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTSwitchStatement node, State state) {

        state.structuralComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTLambdaExpression node, State state) {

        state.increaseNestingLevel();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTClassOrInterfaceBody node, State state) {

        state.increaseNestingLevel();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }
}

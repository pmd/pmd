/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.internal;

import java.util.Iterator;

import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.metrics.impl.internal.CognitiveComplexityVisitor.State.BooleanOp;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;


/**
 * Visitor for the Cognitive Complexity metric.
 *
 * @author Denis Borovikov
 * @since 6.35.0
 */
public class CognitiveComplexityVisitor extends JavaParserVisitorAdapter {

    /** Instance. */
    public static final CognitiveComplexityVisitor INSTANCE = new CognitiveComplexityVisitor();


    public static class State {
        public enum BooleanOp { AND, OR }

        private int complexity = 0;
        private int nestingLevel = 0;

        private BooleanOp currentBooleanOperation = null;
        private String methodName = null;

        public double getComplexity() {
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

        void methodCall(String methodCalledName) {
            if (methodCalledName.equals(methodName)) {
                fundamentalComplexity();
            }
        }

        void setMethodName(String name) {
            methodName = name;
        }
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        State state = (State) data;
        boolean isNotElseIf = !(node.getNthParent(2) instanceof ASTIfStatement);

        node.getCondition().jjtAccept(this, data);

        if (isNotElseIf) {
            state.structuralComplexity();
        }
        node.getThenBranch().jjtAccept(this, data);
        if (isNotElseIf) {
            state.decreaseNestingLevel();
        }

        if (node.hasElse()) {
            state.hybridComplexity();
            node.getElseBranch().jjtAccept(this, data);
            state.decreaseNestingLevel();
        }

        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        State state = (State) data;

        // hack to detect if there is a label
        boolean hasLabel = node.getImage() != null;

        if (hasLabel) {
            state.fundamentalComplexity();
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        State state = (State) data;

        // hack to detect if there is a label
        boolean hasLabel = node.getImage() != null;

        if (hasLabel) {
            state.fundamentalComplexity();
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        State state = (State) data;
        state.booleanOperation(BooleanOp.AND);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        State state = (State) data;
        state.booleanOperation(BooleanOp.OR);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        State state = (State) data;

        String op = node.getOperator();
        if ("!".equals(op)) {
            state.booleanOperation(null);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        State state = (State) data;

        for (JavaNode child : node.children()) {
            // This needs to happen because the current 'run' of boolean operations is terminated
            // once we finish a statement.
            state.booleanOperation(null);

            child.jjtAccept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        State state = (State) data;
        state.setMethodName(node.getQualifiedName().toString());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        State state = (State) data;

        // check if this primary prefix is a method call
        Iterator<? extends JavaNode> it = node.children().iterator();
        if (it.hasNext()) {
            final JavaNode child = it.next();
            if (child instanceof ASTName) {
                ASTName name = (ASTName) child;
                if (name.getNameDeclaration() instanceof MethodNameDeclaration) {
                    ASTMethodDeclaration parent = (ASTMethodDeclaration) name.getNameDeclaration().getNode().getParent();
                    state.methodCall(parent.getQualifiedName().toString());
                }
            }
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        State state = (State) data;

        state.structuralComplexity();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return state;
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return state;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return state;
    }
}

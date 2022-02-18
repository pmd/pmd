package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * A pattern to match over nodes.
 *
 * @author Clément Fournier
 */
public final class JavaNodeMatchers {

    private JavaNodeMatchers() {
        // utility class
    }


    /** Matches a boolean negation expr whose operand matches. */
    public static <N extends JavaNode> NodeMatcher<N> neg(NodeMatcher<? super ASTExpression> operand) {
        return n -> JavaAstUtil.isBooleanNegation(n) && operand.matches(((ASTUnaryExpression) n).getOperand());
    }

    /** Matches an infix expression whose expr whose operands and operator match. */
    public static <N extends JavaNode> NodeMatcher<N> infix(NodeMatcher<? super ASTExpression> left,
                                                            BinaryOp op,
                                                            NodeMatcher<? super ASTExpression> right) {
        return n -> {
            if (BinaryOp.isInfixExprWithOperator(n, op)) {
                ASTInfixExpression infix = (ASTInfixExpression) n;
                return left.matches(infix.getLeftOperand())
                    && right.matches(infix.getRightOperand());
            }
            return false;
        };
    }

    /** Matches an infix expression whose expr whose operands and operator match. */
    public static <N extends JavaNode> NodeMatcher<N> infix(NodeMatcher<? super ASTExpression> left,
                                                            Set<BinaryOp> anyOp,
                                                            NodeMatcher<? super ASTExpression> right) {
        return n -> {
            if (BinaryOp.isInfixExprWithOperator(n, anyOp)) {
                ASTInfixExpression infix = (ASTInfixExpression) n;
                return left.matches(infix.getLeftOperand())
                    && right.matches(infix.getRightOperand());
            }
            return false;
        };
    }

    /** Matches a literal boolean with the given value. */
    public static <N extends JavaNode> NodeMatcher<N> bool(boolean b) {
        return n -> JavaAstUtil.isBooleanLiteral(n, b);
    }

    /** Matches any literal boolean. */
    public static <N extends JavaNode> NodeMatcher<N> bool() {
        return JavaAstUtil::isBooleanLiteral;
    }

    private static <N extends JavaNode> NodeMatcher<N> literal() {
        return is(ASTLiteral.class);
    }


    /**
     * A pattern that matches a node whose static.
     */
    public static <N extends TypeNode> NodeMatcher<N> hasType(JTypeMirror type) {
        Objects.requireNonNull(type);
        return n -> n.getTypeMirror().equals(type);
    }


    /**
     * A pattern that matches a node instance of the given class.
     */
    public static <T, N extends Node> NodeMatcher<N> is(Class<T> type) {
        return type::isInstance;
    }

    /**
     * Matches any node.
     */
    public static <N extends Node> NodeMatcher<N> any() {
        return n -> true;
    }

    public static <N extends Node> NodeMatcher<N> capture(Capture<N> obj, NodeMatcher<N> pattern) {
        return n -> {
            if (pattern.matches(n)) {
                obj.setValue(n);
            }
            return false;
        };
    }

    public static <N extends Node> boolean capture(Capture<N> obj, N n) {
        return obj.setValue(n);
    }

    public static <N extends Node> boolean match(N node, NodeMatcher<? super N> pattern) {
        return pattern.matches(node);
    }

    public static final class Capture<N> {

        N value;

        private boolean setValue(N s) {
            boolean isUnset = value == null;
            value = s;
            return isUnset;
        }

        public N get() {
            if (value == null) {
                throw new IllegalStateException("Pattern has not matched");
            }
            return value;
        }

        public static <N> Capture<N> uninit() {
            return new Capture<>();
        }

    }

}

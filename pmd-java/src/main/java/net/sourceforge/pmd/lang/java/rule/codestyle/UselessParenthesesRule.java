/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.ALWAYS;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.BALANCING;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.CLARIFYING;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.NEVER;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.definitely;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity.necessaryIf;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.AssertionUtil;


public final class UselessParenthesesRule extends AbstractJavaRulechainRule {
    // todo rename to UnnecessaryParentheses

    private static final PropertyDescriptor<Boolean> IGNORE_CLARIFYING =
        PropertyFactory.booleanProperty("ignoreClarifying")
                       .defaultValue(true)
                       .desc("Ignore parentheses that separate expressions of difference precedence,"
                                 + " like in `(a % 2 == 0) ? x : -x`")
                       .build();

    private static final PropertyDescriptor<Boolean> IGNORE_BALANCING =
        PropertyFactory.booleanProperty("ignoreBalancing")
                       .defaultValue(true)
                       .desc("Ignore unnecessary parentheses that appear balanced around an equality "
                                 + "operator, because the other operand requires parentheses."
                                 + "For example, in `(a == null) == (b == null)`, only the second pair "
                                 + "of parentheses is necessary, but the expression is clearer that way.")
                       .build();

    public UselessParenthesesRule() {
        super(ASTExpression.class);
        definePropertyDescriptor(IGNORE_CLARIFYING);
        definePropertyDescriptor(IGNORE_BALANCING);
    }

    private boolean reportClarifying() {
        return !getProperty(IGNORE_CLARIFYING);
    }

    private boolean reportBalancing() {
        return !getProperty(IGNORE_BALANCING);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        if (node instanceof ASTExpression) {
            checkExpr((ASTExpression) node, data);
        } else {
            throw new IllegalArgumentException("Expected an expression, got " + node);
        }
        return null;
    }

    private void checkExpr(ASTExpression e, Object data) {
        if (!e.isParenthesized()) {
            return;
        }

        Necessity necessity = needsParentheses(e, e.getParent());

        if (necessity == NEVER
            || reportClarifying() && necessity == CLARIFYING
            || reportBalancing() && necessity == BALANCING) {
            addViolation(data, e);
        }

    }


    static Necessity needsParentheses(ASTExpression inner, JavaNode outer) {
        // Note: as of jdk 15, PatternExpression cannot be parenthesized
        // TypeExpression may never be parenthesized either
        assert inner.isParenthesized() : inner + " is not parenthesized";


        if (inner.getParenthesisDepth() > 1
            || !(outer instanceof ASTExpression)) {
            // ((a + b))        unnecessary
            // new int[(2)]     unnecessary
            // return (1 + 2);
            return NEVER;
        }

        if (inner instanceof ASTPrimaryExpression
            || inner instanceof ASTSwitchExpression) {
            return NEVER;
        }

        if (outer instanceof ASTLambdaExpression) {
            // () -> (a + b)        unnecessary
            // () -> (() -> b)      unnecessary
            // () -> (a ? b : c);   unnecessary unless the parent of the lambda is itself a ternary
            if (inner instanceof ASTLambdaExpression) {
                return NEVER;
            }
            return definitely(inner instanceof ASTConditionalExpression && outer.getParent() instanceof ASTConditionalExpression);
        }

        if (inner instanceof ASTAssignmentExpression) {
            //  a * (b = c)          necessary
            //  a ? (b = c) : d      necessary
            //  a = (b = c)          associative
            //  (a = b) = c          (impossible)
            return outer instanceof ASTAssignmentExpression ? NEVER : ALWAYS;
        }

        if (inner instanceof ASTConditionalExpression) {

            // a ? (b ? c : d) : e    necessary
            // a ? b : (c ? d : e)    associative
            // (a ? b : c) ? d : e    necessary
            if (outer instanceof ASTConditionalExpression) {
                return inner.getIndexInParent() == 2 ? NEVER  // last child
                                                     : ALWAYS;
            } else {
                return necessaryIf(!(outer instanceof ASTAssignmentExpression));
            }
        }

        if (inner instanceof ASTLambdaExpression) {
            // a ? (() -> b) + c : d     invalid, but necessary
            // a ? (() -> b) : d         clarifying
            return outer instanceof ASTConditionalExpression ? CLARIFYING
                                                             : definitely(!(outer instanceof ASTAssignmentExpression));
        }

        if (inner instanceof ASTInfixExpression) {
            if (outer instanceof ASTInfixExpression) {
                BinaryOp inop = ((ASTInfixExpression) inner).getOperator();
                BinaryOp outop = ((ASTInfixExpression) outer).getOperator();
                int comp = outop.comparePrecedence(inop);

                // (a * b) + c       unnecessary
                // a * (b + c)       necessary
                // a + (b + c)       unnecessary
                // (a + b) + c       unnecessary
                // (a - b) + c       clarifying

                if (comp > 0) {
                    return ALWAYS; // outer has greater precedence
                } else if (comp < 0) {
                    return CLARIFYING; // outer has lower precedence, but the operators are different
                }

                // the rest deals with ties in precedence

                if (inner.getIndexInParent() == 1) {
                    // parentheses are on the right
                    // eg a - (b + c)

                    if (associatesRightWith(outop, inop, (ASTInfixExpression) inner, (ASTInfixExpression) outer)) {
                        // a & (b & c)
                        // a | (b | c)
                        // a ^ (b ^ c)
                        // a && (b && c)
                        // a || (b || c)
                        return NEVER;
                    } else {
                        return ALWAYS;
                    }
                } else {
                    // parentheses are on the left
                    // eg (a + b) + c
                    if (outop.hasSamePrecedenceAs(BinaryOp.EQ) // EQ or NE
                        && ((ASTInfixExpression) outer).getRightOperand().isParenthesized()) {
                        // (a == null) == (b == null)
                        return BALANCING;
                    } else if (outop == BinaryOp.ADD
                        && inop.hasSamePrecedenceAs(BinaryOp.ADD) && inner.getTypeMirror().isNumeric()
                        && !((ASTInfixExpression) outer).getTypeMirror().isNumeric()) {
                        return CLARIFYING;
                    }

                    return NEVER;
                }
            } else { // outer !is ASTInfixExpression
                if (outer instanceof ASTConditionalExpression && inner.getIndexInParent() == 0) {
                    // (a == b) ? .. : ..
                    return CLARIFYING;
                }

                return necessaryIf(isUnary(outer) || outer instanceof ASTPrimaryExpression);
            }
        }

        if (isUnary(inner)) {
            return isUnary(outer) ? NEVER : necessaryIf(outer instanceof ASTPrimaryExpression);
        }


        throw AssertionUtil.shouldNotReachHere("Unhandled case inside " + outer);
    }

    private static boolean isUnary(JavaNode expr) {
        return expr instanceof ASTUnaryExpression || expr instanceof ASTCastExpression;
    }

    // Returns true if it is safe to remove parentheses in the expression `A outop (B inop C)`
    // outop and inop have the same precedence class
    private static boolean associatesRightWith(BinaryOp outop, BinaryOp inop, ASTInfixExpression inner, ASTInfixExpression outer) {

        /*
         Notes about associativity:
         - Integer multiplication/addition is associative when the operands are all of the same type.
         - Floating-point multiplication/addition is not associative.
         - The boolean equality operators are associative, but input type != output type in general
         - Bitwise and logical operators are associative
         - The conditional-OR/AND operator is fully associative with respect to both side effects and result value.
         */

        switch (inop) {
        case AND:
        case OR:
        case XOR:
        case CONDITIONAL_AND:
        case CONDITIONAL_OR:
            return true;
        case MUL:
            // a * (b * c)      -- yes
            // a * (b / c)      -- no, could change semantics
            // a * (b % c)      -- no

            // a / (b * c)      -- no
            // a / (b / c)      -- no
            // a / (b % c)      -- no

            // a % (b * c)      -- no
            // a % (b / c)      -- no
            // a % (b % c)      -- no

            return inop == outop; // == MUL
        case SUB:
        case ADD:
            // a + (b + c)      -- yes, unless outop is concatenation and inop is addition, or operands are floats
            // a + (b - c)      -- yes

            // a - (b + c)      -- no
            // a - (b - c)      -- no
            return outop == BinaryOp.ADD
                && outer.getTypeMirror().isPrimitive() == inner.getTypeMirror().isPrimitive()
                && !inner.getTypeMirror().isFloatingPoint();
        default:
            return false;
        }
    }

    enum Necessity {
        ALWAYS,
        NEVER,
        CLARIFYING,
        BALANCING;

        static Necessity definitely(boolean b) {
            return b ? ALWAYS : NEVER;
        }

        static Necessity necessaryIf(boolean b) {
            return b ? ALWAYS : CLARIFYING;
        }
    }
}

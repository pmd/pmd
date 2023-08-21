/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Computes constant expression values.
 */
// strictfp because constant expressions are FP-strict (not sure if this is really important)
final strictfp class ConstantFolder extends JavaVisitorBase<Void, Object> {

    static final ConstantFolder INSTANCE = new ConstantFolder();
    private static final Pair<Object, Object> FAILED_BIN_PROMOTION = Pair.of(null, null);

    private ConstantFolder() {

    }

    @Override
    public Object visitJavaNode(JavaNode node, Void data) {
        return null;
    }

    @Override
    public @NonNull Number visitLiteral(ASTLiteral num, Void data) {
        throw new AssertionError("Literal nodes implement getConstValue directly");
    }

    @Override
    public Object visit(ASTVariableAccess node, Void data) {
        return fetchConstFieldReference(node);
    }

    @Override
    public Object visit(ASTFieldAccess node, Void data) {
        return fetchConstFieldReference(node);
    }

    private @Nullable Object fetchConstFieldReference(ASTNamedReferenceExpr node) {
        JVariableSymbol symbol = node.getReferencedSym();
        if (symbol instanceof JFieldSymbol) {
            return ((JFieldSymbol) symbol).getConstValue();
        }
        return null;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Void data) {
        Object condition = node.getCondition().getConstValue();
        if (condition instanceof Boolean) {
            Object thenValue = node.getThenBranch().getConstValue();
            Object elseValue = node.getElseBranch().getConstValue();
            if (thenValue == null || elseValue == null) {
                return null; // not a constexpr
            }
            if ((Boolean) condition) {
                return thenValue;
            } else {
                return elseValue;
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTCastExpression node, Void data) {
        JTypeMirror t = node.getCastType().getTypeMirror();
        if (t.isNumeric()) {
            return numericCoercion(node.getOperand().getConstValue(), t);
        } else if (TypeTestUtil.isExactlyA(String.class, node.getCastType())) {
            return stringCoercion(node.getOperand().getConstValue());
        }
        return null;
    }

    @Override
    public Object visit(ASTUnaryExpression node, Void data) {
        UnaryOp operator = node.getOperator();
        if (!operator.isPure()) {
            return null;
        }

        ASTExpression operand = node.getOperand();
        Object operandValue = operand.getConstValue();
        if (operandValue == null) {
            return null;
        }

        switch (operator) {
        case UNARY_PLUS:
            return unaryPromotion(operandValue);
        case UNARY_MINUS: {
            Number promoted = unaryPromotion(operandValue);
            if (promoted == null) {
                return null; // compile-time error
            } else if (promoted instanceof Integer) {
                return -promoted.intValue();
            } else if (promoted instanceof Long) {
                return -promoted.longValue();
            } else if (promoted instanceof Float) {
                return -promoted.floatValue();
            } else {
                assert promoted instanceof Double;
                return -promoted.doubleValue();
            }
        }
        case COMPLEMENT: {
            Number promoted = unaryPromotion(operandValue);
            if (promoted instanceof Integer) {
                return ~promoted.intValue();
            } else if (promoted instanceof Long) {
                return ~promoted.longValue();
            } else {
                return null; // compile-time error
            }
        }
        case NEGATION: {
            return booleanInvert(operandValue);
        }

        default: // increment ops
            throw new AssertionError("unreachable");
        }
    }

    @Override
    public strictfp Object visit(ASTInfixExpression node, Void data) {
        Object left = node.getLeftOperand().getConstValue();
        Object right = node.getRightOperand().getConstValue();
        if (left == null || right == null) {
            return null;
        }

        switch (node.getOperator()) {
        case CONDITIONAL_OR: {
            if (left instanceof Boolean && right instanceof Boolean) {
                return (Boolean) left || (Boolean) right;
            }
            return null;
        }

        case CONDITIONAL_AND: {
            if (left instanceof Boolean && right instanceof Boolean) {
                return (Boolean) left && (Boolean) right;
            }
            return null;
        }

        case OR: {
            Pair<Object, Object> promoted = booleanAwareBinaryPromotion(left, right);
            left = promoted.getLeft();
            right = promoted.getRight();

            if (left instanceof Integer) {
                return intValue(left) | intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) | longValue(right);
            } else if (left instanceof Boolean) {
                return booleanValue(left) | booleanValue(right);
            }
            return null;
        }
        case XOR: {
            Pair<Object, Object> promoted = booleanAwareBinaryPromotion(left, right);
            left = promoted.getLeft();
            right = promoted.getRight();

            if (left instanceof Integer) {
                return intValue(left) ^ intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) ^ longValue(right);
            } else if (left instanceof Boolean) {
                return booleanValue(left) ^ booleanValue(right);
            }
            return null;
        }
        case AND: {
            Pair<Object, Object> promoted = booleanAwareBinaryPromotion(left, right);
            left = promoted.getLeft();
            right = promoted.getRight();

            if (left instanceof Integer) {
                return intValue(left) & intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) & longValue(right);
            } else if (left instanceof Boolean) {
                return booleanValue(left) & booleanValue(right);
            }
            return null;
        }

        case EQ:
            return eqResult(left, right);
        case NE:
            return booleanInvert(eqResult(left, right));

        case LE:
            return compLE(left, right);
        case GT:
            return booleanInvert(compLE(left, right));
        case LT:
            return compLT(left, right);
        case GE:
            return booleanInvert(compLT(left, right));

        case INSTANCEOF:
            // disallowed, actually dead code because the
            // right operand is the type, which is no constexpr
            return null;

        // for shift operators, unary promotion is performed on operators separately
        case LEFT_SHIFT: {
            left = unaryPromotion(left);
            right = unaryPromotion(right);
            if (!(right instanceof Integer) && !(right instanceof Long)) {
                return null; // shift distance must be integral
            }

            // only use intValue for the left operand
            if (left instanceof Integer) {
                return intValue(left) << intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) << intValue(right);
            }
            return null;
        }
        case RIGHT_SHIFT: {
            left = unaryPromotion(left);
            right = unaryPromotion(right);
            if (!(right instanceof Integer) && !(right instanceof Long)) {
                return null; // shift distance must be integral
            }

            // only use intValue for the left operand
            if (left instanceof Integer) {
                return intValue(left) >> intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) >> intValue(right);
            }
            return null;
        }
        case UNSIGNED_RIGHT_SHIFT: {
            left = unaryPromotion(left);
            right = unaryPromotion(right);
            if (!(right instanceof Integer) && !(right instanceof Long)) {
                return null; // shift distance must be integral
            }

            // only use intValue for the left operand
            if (left instanceof Integer) {
                return intValue(left) >>> intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) >>> intValue(right);
            }
            return null;
        }
        case ADD: {
            if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
                Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
                left = promoted.getLeft();
                right = promoted.getRight();

                if (left instanceof Integer) {
                    return intValue(left) + intValue(right);
                } else if (left instanceof Long) {
                    return longValue(left) + longValue(right);
                } else if (left instanceof Float) {
                    return floatValue(left) + floatValue(right);
                } else {
                    return doubleValue(left) + doubleValue(right);
                }
            } else if (left instanceof String) {
                // string concat
                return (String) left + right;
            } else if (right instanceof String) {
                // string concat
                return left + (String) right;
            }
            return null;
        }
        case SUB: {
            if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
                Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
                left = promoted.getLeft();
                right = promoted.getRight();

                if (left instanceof Integer) {
                    return intValue(left) - intValue(right);
                } else if (left instanceof Long) {
                    return longValue(left) - longValue(right);
                } else if (left instanceof Float) {
                    return floatValue(left) - floatValue(right);
                } else {
                    return doubleValue(left) - doubleValue(right);
                }
            }
            return null;
        }
        case MUL: {
            if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
                Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
                left = promoted.getLeft();
                right = promoted.getRight();

                if (left instanceof Integer) {
                    return intValue(left) * intValue(right);
                } else if (left instanceof Long) {
                    return longValue(left) * longValue(right);
                } else if (left instanceof Float) {
                    return floatValue(left) * floatValue(right);
                } else {
                    return doubleValue(left) * doubleValue(right);
                }
            }
            return null;
        }
        case DIV: {
            if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
                Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
                left = promoted.getLeft();
                right = promoted.getRight();

                if (left instanceof Integer) {
                    return intValue(left) / intValue(right);
                } else if (left instanceof Long) {
                    return longValue(left) / longValue(right);
                } else if (left instanceof Float) {
                    return floatValue(left) / floatValue(right);
                } else {
                    return doubleValue(left) / doubleValue(right);
                }
            }
            return null;
        }
        case MOD: {
            if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
                Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
                left = promoted.getLeft();
                right = promoted.getRight();

                if (left instanceof Integer) {
                    return intValue(left) % intValue(right);
                } else if (left instanceof Long) {
                    return longValue(left) % longValue(right);
                } else if (left instanceof Float) {
                    return floatValue(left) % floatValue(right);
                } else {
                    return doubleValue(left) % doubleValue(right);
                }
            }
            return null;
        }
        default:
            throw AssertionUtil.shouldNotReachHere("Unknown operator in " + node);
        }
    }

    private static @Nullable Object compLE(Object left, Object right) {
        if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
            Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
            left = promoted.getLeft();
            right = promoted.getRight();

            if (left instanceof Integer) {
                return intValue(left) <= intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) <= longValue(right);
            } else if (left instanceof Float) {
                return floatValue(left) <= floatValue(right);
            } else if (left instanceof Double) {
                return doubleValue(left) <= doubleValue(right);
            }
        }
        return null;
    }

    private static @Nullable Boolean compLT(Object left, Object right) {
        if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
            Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
            left = promoted.getLeft();
            right = promoted.getRight();

            if (left instanceof Integer) {
                return intValue(left) < intValue(right);
            } else if (left instanceof Long) {
                return longValue(left) < longValue(right);
            } else if (left instanceof Float) {
                return floatValue(left) < floatValue(right);
            } else if (left instanceof Double) {
                return doubleValue(left) < doubleValue(right);
            }
        }
        return null;
    }

    private static @Nullable Boolean booleanInvert(@Nullable Object b) {
        if (b instanceof Boolean) {
            return !(Boolean) b;
        }
        return null;
    }

    private static @Nullable Boolean eqResult(Object left, Object right) {
        if (isConvertibleToNumber(left) && isConvertibleToNumber(right)) {
            Pair<Object, Object> promoted = binaryNumericPromotion(left, right);
            return promoted.getLeft().equals(promoted.getRight()); // fixme Double.NaN
        } else {
            return null; // not a constant expr on reference types
        }
    }

    private static boolean isConvertibleToNumber(Object o) {
        return o instanceof Number || o instanceof Character;
    }


    private static @Nullable Number unaryPromotion(Object t) {
        if (t instanceof Character) {
            return (int) (Character) t;
        } else if (t instanceof Number) {
            if (t instanceof Byte || t instanceof Short) {
                return intValue(t);
            } else {
                return (Number) t;
            }
        }
        return null;
    }

    /**
     * This returns a pair in which both numbers have the dynamic type.
     * Both right and left need to be {@link #isConvertibleToNumber(Object)},
     * otherwise fails with ClassCastException.
     */
    private static Pair<Object, Object> binaryNumericPromotion(Object left, Object right) {
        left = projectCharOntoInt(left);
        right = projectCharOntoInt(right);
        if (left instanceof Double || right instanceof Double) {
            return Pair.of(doubleValue(left), doubleValue(right));
        } else if (left instanceof Float || right instanceof Float) {
            return Pair.of(floatValue(left), floatValue(right));
        } else if (left instanceof Long || right instanceof Long) {
            return Pair.of(longValue(left), longValue(right));
        } else {
            return Pair.of(intValue(left), intValue(right));
        }
    }

    private static Pair<Object, Object> booleanAwareBinaryPromotion(Object left, Object right) {
        if (left instanceof Boolean || right instanceof Boolean) {
            if (left instanceof Boolean && right instanceof Boolean) {
                return Pair.of(left, right);
            }
            return FAILED_BIN_PROMOTION;
        } else if (!isConvertibleToNumber(left) || !isConvertibleToNumber(right)) {
            return FAILED_BIN_PROMOTION;
        } else {
            return binaryNumericPromotion(left, right);
        }
    }

    private static Object projectCharOntoInt(Object v) {
        if (v instanceof Character) {
            return (int) (Character) v;
        }
        return v;
    }

    private static Object numericCoercion(Object v, JTypeMirror target) {
        v = projectCharOntoInt(v); // map chars to a Number (widen it to int, which may be narrowed in the switch)

        if (target.isNumeric() && v instanceof Number) {
            switch (((JPrimitiveType) target).getKind()) {
            case BOOLEAN:
                throw new AssertionError("unreachable");
            case CHAR:
                return (char) intValue(v);
            case BYTE:
                return (byte) intValue(v);
            case SHORT:
                return (short) intValue(v);
            case INT:
                return intValue(v);
            case LONG:
                return longValue(v);
            case FLOAT:
                return floatValue(v);
            case DOUBLE:
                return doubleValue(v);
            default:
                throw AssertionUtil.shouldNotReachHere("exhaustive enum");
            }
        }
        return null;
    }

    private static Object stringCoercion(Object v) {
        if (v instanceof String) {
            return v;
        }
        return null;
    }

    private static boolean booleanValue(Object x) {
        return (Boolean) x;
    }

    private static int intValue(Object x) {
        return ((Number) x).intValue();
    }

    private static long longValue(Object x) {
        return ((Number) x).longValue();
    }

    private static float floatValue(Object x) {
        return ((Number) x).floatValue();
    }

    private static double doubleValue(Object x) {
        return ((Number) x).doubleValue();
    }

}

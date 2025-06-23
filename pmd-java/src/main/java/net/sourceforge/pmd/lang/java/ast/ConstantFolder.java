/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.ast.ASTExpression.ConstResult;
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
final strictfp class ConstantFolder extends JavaVisitorBase<Void, @NonNull ConstResult> {

    static final ConstantFolder INSTANCE = new ConstantFolder();
    private static final Pair<Object, Object> FAILED_BIN_PROMOTION = Pair.of(null, null);

    private ConstantFolder() {

    }

    @Override
    public @NonNull ConstResult visitJavaNode(JavaNode node, Void data) {
        return ConstResult.NO_CONST_VALUE;
    }

    @Override
    public @NonNull ConstResult visitLiteral(ASTLiteral num, Void data) {
        throw new AssertionError("Literal nodes implement getConstValue directly");
    }

    @Override
    public @NonNull ConstResult visit(ASTNumericLiteral node, Void data) {
        // don't use ternaries, the compiler messes up autoboxing.
        Object result;
        if (node.isIntegral()) {
            if (node.isIntLiteral()) {
                result = node.getValueAsInt();
            } else {
                result = node.getValueAsLong();
            }
        } else {
            if (node.isFloatLiteral()) {
                result = node.getValueAsFloat();
            } else {
                result = node.getValueAsDouble();
            }
        }
        return ConstResult.ctConst(result);
    }

    @Override
    public @NonNull ConstResult visit(ASTBooleanLiteral node, Void data) {
        return node.isTrue() ? ConstResult.BOOL_TRUE : ConstResult.BOOL_FALSE;
    }

    @Override
    public @NonNull ConstResult visit(ASTStringLiteral node, Void data) {
        String result;
        if (node.isTextBlock()) {
            result = ASTStringLiteral.determineTextBlockContent(node.getLiteralText());
        } else {
            result = ASTStringLiteral.determineStringContent(node.getLiteralText());
        }
        return ConstResult.ctConst(result);
    }

    @Override
    public @NonNull ConstResult visit(ASTNullLiteral node, Void data) {
        return ConstResult.NO_CONST_VALUE;
    }

    @Override
    public @NonNull ConstResult visit(ASTCharLiteral node, Void data) {
        Chars image = node.getLiteralText();
        Chars woDelims = image.subSequence(1, image.length() - 1);
        Character result = StringEscapeUtils.UNESCAPE_JAVA.translate(woDelims).charAt(0);
        return ConstResult.ctConst(result);
    }

    @Override
    public @NonNull ConstResult visit(ASTVariableAccess node, Void data) {
        JVariableSymbol symbol = node.getReferencedSym();
        if (symbol == null || !symbol.isFinal()) {
            return ConstResult.NO_CONST_VALUE;
        }

        if (symbol instanceof JFieldSymbol) {
            @Nullable Object cv = ((JFieldSymbol) symbol).getConstValue();
            if (cv != null) {
                return ConstResult.ctConst(cv);
            }
        }

        @Nullable
        ASTVariableId declaratorId = symbol.tryGetNode();
        if (declaratorId != null) {
            ASTExpression initializer = declaratorId.getInitializer();
            if (initializer != null) {
                ConstResult initRes = initializer.getConstFoldingResult();
                if (initRes.hasValue()) {
                    boolean isCompileTimeConstant = symbol instanceof JFieldSymbol
                        && ((JFieldSymbol) symbol).isStatic();
                    return new ConstResult(isCompileTimeConstant, initRes.getValue());
                }
                return initRes;
            }
        }

        return ConstResult.NO_CONST_VALUE;
    }

    @Override
    public @NonNull ConstResult visit(ASTFieldAccess node, Void data) {
        JFieldSymbol symbol = node.getReferencedSym();
        if (symbol != null) {
            return ConstResult.ctConstIfNotNull(symbol.getConstValue());
        }
        return ConstResult.NO_CONST_VALUE;
    }

    @Override
    public @NonNull ConstResult visit(ASTArrayInitializer node, Void data) {
        int length = node.length();
        boolean isCtConst = true;
        Object[] result = new Object[length];
        int index = 0;
        for (ASTExpression expr : node) {
            ConstResult itemResult = expr.getConstFoldingResult();
            if (!itemResult.hasValue()) {
                return ConstResult.NO_CONST_VALUE;
            }
            result[index++] = itemResult.getValue();
            isCtConst &= itemResult.isCompileTimeConstant();
        }

        return new ConstResult(isCtConst, result);
    }

    @Override
    public @NonNull ConstResult visit(ASTConditionalExpression node, Void data) {
        ConstResult condition = node.getCondition().getConstFoldingResult();
        if (condition.hasValue()) {
            ConstResult thenValue = node.getThenBranch().getConstFoldingResult();
            ConstResult elseValue = node.getElseBranch().getConstFoldingResult();
            boolean ctConst = condition.isCompileTimeConstant()
                && thenValue.isCompileTimeConstant()
                && elseValue.isCompileTimeConstant();
            if (!thenValue.hasValue() || !elseValue.hasValue()) {
                return ConstResult.NO_CONST_VALUE; // not a constexpr
            }
            if (condition.getValue() instanceof Boolean && (boolean) condition.getValue()) {
                return new ConstResult(ctConst, thenValue.getValue());
            } else {
                return new ConstResult(ctConst, elseValue.getValue());
            }
        }
        return ConstResult.NO_CONST_VALUE;
    }

    @Override
    public @NonNull ConstResult visit(ASTCastExpression node, Void data) {
        JTypeMirror t = node.getCastType().getTypeMirror();
        ConstResult castValue = node.getOperand().getConstFoldingResult();
        if (!castValue.hasValue()) {
            return ConstResult.NO_CONST_VALUE;
        }
        Object res;
        if (t.isNumeric()) {
            res = numericCoercion(castValue.getValue(), t);
        } else if (TypeTestUtil.isExactlyA(String.class, node.getCastType())) {
            res = stringCoercion(castValue.getValue());
        } else {
            return ConstResult.NO_CONST_VALUE;
        }

        return new ConstResult(castValue.isCompileTimeConstant(), res);
    }

    @Override
    public @NonNull ConstResult visit(ASTUnaryExpression node, Void data) {
        UnaryOp operator = node.getOperator();
        if (!operator.isPure()) {
            return ConstResult.NO_CONST_VALUE;
        }

        ASTExpression operand = node.getOperand();
        ConstResult operandValue = operand.getConstFoldingResult();
        if (!operandValue.hasValue()) {
            return ConstResult.NO_CONST_VALUE;
        }
        Object value = computeUnary(operandValue.getValue(), node);
        return new ConstResult(operandValue.isCompileTimeConstant(), value);
    }

    private @Nullable Object computeUnary(Object operandValue, ASTUnaryExpression node) {

        switch (node.getOperator()) {
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
    public strictfp ConstResult visit(ASTInfixExpression node, Void data) {
        ConstResult left = node.getLeftOperand().getConstFoldingResult();
        ConstResult right = node.getRightOperand().getConstFoldingResult();
        if (!left.hasValue() || !right.hasValue()) {
            return ConstResult.NO_CONST_VALUE;
        }
        Object res = computeInfix(left.getValue(), right.getValue(), node);
        if (res == null) {
            return ConstResult.NO_CONST_VALUE;
        }

        boolean ctConst = left.isCompileTimeConstant() && right.isCompileTimeConstant();
        return new ConstResult(ctConst, res);
    }

    private strictfp @Nullable Object computeInfix(Object left, Object right, ASTInfixExpression node) {
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
            throw AssertionUtil.shouldNotReachHere("Unknown operator '" + node.getOperator() + "' in " + node);
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
                throw AssertionUtil.shouldNotReachHere("exhaustive enum: " + target);
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

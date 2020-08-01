/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.ast;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.binaryNumericPromotion;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.unaryNumericPromotion;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimensions;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCharLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeTestPattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;

/**
 * Lazy version of ClassTypeResolver. This is not appropriate for
 * all nodes, though it is appropriate for standalone expressions.
 */
public class LazyTypeResolver extends JavaVisitorBase<Void, JTypeMirror> {

    private final TypeSystem ts;
    private final PolyResolution polyResolution;
    private final JClassType stringType;


    public LazyTypeResolver(JavaAstProcessor processor, TypeInferenceLogger logger) {
        this.ts = processor.getTypeSystem();
        this.polyResolution = new PolyResolution(new Infer(ts, processor.getJdkVersion(), logger));
        this.stringType = (JClassType) TypesFromReflection.fromReflect(String.class, ts);
    }

    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JTypeMirror visit(JavaNode node, Void data) {
        // don't recurse
        return ts.NO_TYPE;
    }


    @Override
    public JTypeMirror visit(ASTVariableDeclarator node, Void data) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode (do you mean type of variable, or type of initializer?)
    }

    @Override
    public JTypeMirror visit(ASTResultType node, Void data) {
        return node.isVoid() ? ts.NO_TYPE : node.getTypeNode().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTFormalParameter node, Void data) {
        return node.getVarId().getTypeMirror();
    }


    @Override
    public JTypeMirror visit(ASTTypeParameter node, Void data) {
        return node.getSymbol().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTAnyTypeDeclaration node, Void data) {
        return ts.declaration(node.getSymbol());
    }

    @Override
    public JTypeMirror visit(ASTAnnotation node, Void data) {
        return node.getTypeNode().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTType node, Void data) {
        return TypesFromAst.fromAst(ts, Substitution.EMPTY, node);
    }

    @Override
    public JTypeMirror visit(ASTVariableDeclaratorId node, Void data) {
        boolean isTypeInferred = node.isTypeInferred();
        if (isTypeInferred && node.getInitializer() != null) {
            // var k = foo()

            ASTExpression initializer = node.getInitializer();
            return initializer == null ? ts.ERROR_TYPE : TypeOps.projectUpwards(initializer.getTypeMirror());

        } else if (isTypeInferred && node.ancestors().get(2) instanceof ASTForeachStatement) {
            // for (var k : map.keySet())

            JTypeMirror iterableType = ((ASTForeachStatement) node.ancestors().get(2)).getIterableExpr().getTypeMirror();

            if (iterableType instanceof JArrayType) {
                return ((JArrayType) iterableType).getComponentType();
            } else {
                JTypeMirror asSuper = iterableType.getAsSuper(ts.getClassSymbol(Iterable.class));
                if (asSuper instanceof JClassType) {
                    return asSuper.isRaw() ? ts.OBJECT : ((JClassType) asSuper).getTypeArgs().get(0);
                } else {
                    return ts.ERROR_TYPE;
                }
            }

        } else if (isTypeInferred && node.isLambdaParameter()) {
            ASTLambdaParameter param = (ASTLambdaParameter) node.getParent();
            ASTLambdaExpression lambda = (ASTLambdaExpression) node.getNthParent(3);
            JMethodSig mirror = lambda.getFunctionalMethod();
            if (mirror == null || mirror == ts.UNRESOLVED_METHOD) {
                return ts.UNRESOLVED_TYPE;
            }
            return mirror.getFormalParameters().get(param.getIndexInParent());

        } else if (node.isEnumConstant()) {

            TypeNode enumClass = node.getEnclosingType();
            return enumClass.getTypeMirror();

        }

        ASTType typeNode = node.getTypeNode();
        if (typeNode == null) {
            return ts.ERROR_TYPE;
        }

        // Type common to all declarations in the same statement
        JTypeMirror baseType = typeNode.getTypeMirror();
        ASTArrayDimensions extras = node.getExtraDimensions();

        return extras != null
               ? ts.arrayType(baseType, extras.size())
               : baseType;
    }

    /*
        EXPRESSIONS
     */

    @Override
    public JTypeMirror visit(ASTAssignmentExpression node, Void data) {
        // The type of the assignment expression is the type of the variable after capture conversion
        return TypeConversion.capture(node.getLeftOperand().getTypeMirror());
    }

    /**
     * Poly expressions need context and are resolved by {@link PolyResolution}.
     */
    private JTypeMirror handlePoly(TypeNode node) {
        return polyResolution.computePolyType(node);
    }

    @Override
    public JTypeMirror visit(ASTMethodCall node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTConditionalExpression node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTLambdaExpression node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTSwitchExpression node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTMethodReference node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTConstructorCall node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTExplicitConstructorInvocation node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTEnumConstant node, Void data) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTInfixExpression node, Void data) {
        BinaryOp op = node.getOperator();
        switch (op) {
        case CONDITIONAL_OR:
        case CONDITIONAL_AND:
        case EQ:
        case NE:
        case LE:
        case GE:
        case GT:
        case INSTANCEOF:
        case LT:
            // HMM so we don't even check?
            return ts.BOOLEAN;
        case OR:
        case XOR:
        case AND: {
            // those may be boolean or bitwise
            final JTypeMirror lhs = node.getLeftOperand().getTypeMirror();
            final JTypeMirror rhs = node.getRightOperand().getTypeMirror();

            if ((lhs.isPrimitive() || isUnresolved(rhs)) && lhs.equals(rhs)
                || isUnresolved(rhs)) {
                // BOOL       & BOOL        -> BOOL
                // UNRESOLVED & UNRESOLVED  -> UNRESOLVED
                // BOOL       & UNRESOLVED  -> BOOL
                // NUMERIC(N) & UNRESOLVED  -> N
                // NUMERIC(N) & NUMERIC(N)  -> N
                return lhs;
            } else if (isUnresolved(lhs)) {
                // UNRESOLVED & BOOL        -> BOOL
                // UNRESOLVED & NUMERIC(N)  -> N
                return rhs;
            } else {
                // NUMERIC(N) & NUMERIC(M)  -> promote(N, M)
                // anything else, including error types & such: ERROR_TYPE
                return binaryNumericPromotion(lhs, rhs);
            }
        }
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT:
            return unaryNumericPromotion(node.getLeftOperand().getTypeMirror());
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case MOD:
            final JTypeMirror lhs = node.getLeftOperand().getTypeMirror();
            final JTypeMirror rhs = node.getRightOperand().getTypeMirror();
            if (op == ADD && (lhs.equals(stringType) || rhs.equals(stringType))) {
                // string concatenation
                return stringType;
            } else if (isUnresolved(lhs)) {
                return rhs;
            } else if (isUnresolved(rhs)) {
                return lhs;
            } else {
                return binaryNumericPromotion(lhs, rhs);
            }

        default:
            throw new IllegalStateException("Unknown operator " + op);
        }
    }

    private boolean isUnresolved(JTypeMirror t) {
        return t == ts.UNRESOLVED_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTUnaryExpression node, Void data) {
        switch (node.getOperator()) {
        case UNARY_PLUS:
        case UNARY_MINUS:
        case COMPLEMENT:
            return unaryNumericPromotion(node.getOperand().getTypeMirror());
        case NEGATION:
            return ts.BOOLEAN;
        case PRE_INCREMENT:
        case PRE_DECREMENT:
        case POST_INCREMENT:
        case POST_DECREMENT:
            return node.getOperand().getTypeMirror();
        default:
            throw new IllegalStateException("Unknown operator for " + node);
        }
    }

    @Override
    public JTypeMirror visit(ASTPatternExpression node, Void data) {
        ASTPattern pattern = node.getPattern();
        if (pattern instanceof ASTTypeTestPattern) {
            return ((ASTTypeTestPattern) pattern).getTypeNode().getTypeMirror();
        }
        return ts.NO_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTCastExpression node, Void data) {
        return node.getCastType().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTNullLiteral node, Void data) {
        return ts.NULL_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTCharLiteral node, Void data) {
        return ts.CHAR;
    }

    @Override
    public JTypeMirror visit(ASTStringLiteral node, Void data) {
        return stringType;
    }

    @Override
    public JTypeMirror visit(ASTNumericLiteral node, Void data) {
        if (node.isIntegral()) {
            return node.isLongLiteral() ? ts.LONG : ts.INT;
        } else {
            return node.isFloatLiteral() ? ts.FLOAT : ts.DOUBLE;
        }
    }

    @Override
    public JTypeMirror visit(ASTClassLiteral node, Void data) {
        JClassSymbol klassSym = ts.getClassSymbol(Class.class);
        if (node.getTypeNode() == null) {
            // void.class : Class<Void>
            return ts.parameterise(klassSym, listOf(ts.BOXED_VOID));
        } else {
            return ts.parameterise(klassSym, listOf(node.getTypeNode().getTypeMirror().box()));
        }
    }


    @Override
    public JTypeMirror visit(ASTArrayAllocation node, Void data) {
        return node.getTypeNode().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTArrayInitializer node, Void data) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTArrayAllocation) {
            return ((ASTArrayAllocation) parent).getTypeMirror();
        } else if (parent instanceof ASTVariableDeclarator) {
            ASTVariableDeclaratorId id = ((ASTVariableDeclarator) parent).getVarId();
            return id.isTypeInferred() ? ts.ERROR_TYPE : id.getTypeMirror();
        }
        return ts.ERROR_TYPE;
    }


    @Override
    public JTypeMirror visit(ASTVariableAccess node, Void data) {
        @Nullable JVariableSig result = node.getSymbolTable().variables().resolveFirst(node.getVariableName());
        if (result == null) {
            return null;
        }
        return result.getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTFieldAccess node, Void data) {
        JVariableSig sig = TypeConversion.capture(node.getQualifier().getTypeMirror()).getField(node.getFieldName());
        return sig != null ? sig.getTypeMirror() : ts.UNRESOLVED_TYPE;
    }


    @Override
    public JTypeMirror visit(ASTArrayAccess node, Void data) {
        JTypeMirror comp = node.getQualifier().getTypeMirror();
        if (comp instanceof JArrayType) {
            return ((JArrayType) comp).getComponentType();
        } else if (comp == ts.UNRESOLVED_TYPE) {
            return comp;
        } else {
            return ts.ERROR_TYPE;
        }
    }

    @Override
    public JTypeMirror visit(ASTSuperExpression node, Void data) {
        if (node.getQualifier() != null) {
            return node.getQualifier().getTypeMirror();
        } else {
            return node.getEnclosingType().getTypeMirror().getSuperClass();
        }
    }

    @Override
    public JTypeMirror visit(ASTThisExpression node, Void data) {
        return node.getQualifier() != null
               ? node.getQualifier().getTypeMirror()
               : node.getEnclosingType().getTypeMirror();
    }

    @Override
    public JTypeMirror visit(ASTAmbiguousName node, Void data) {
        return ts.UNRESOLVED_TYPE;
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.binaryNumericPromotion;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.capture;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.unaryNumericPromotion;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
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
final class LazyTypeResolver extends JavaVisitorBase<Void, @NonNull JTypeMirror> {

    private final TypeSystem ts;
    private final PolyResolution polyResolution;
    private final JClassType stringType;
    private final JavaAstProcessor processor;


    LazyTypeResolver(JavaAstProcessor processor, TypeInferenceLogger logger) {
        this.ts = processor.getTypeSystem();
        this.polyResolution = new PolyResolution(new Infer(ts, processor.getJdkVersion(), logger));
        this.stringType = (JClassType) TypesFromReflection.fromReflect(String.class, ts);
        this.processor = processor;
    }

    public JavaAstProcessor getProcessor() {
        return processor;
    }

    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JTypeMirror visitJavaNode(JavaNode node, Void data) {
        throw new IllegalArgumentException("Not a type node:" + node);
    }


    @Override
    public JTypeMirror visit(ASTVariableDeclarator node, Void data) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode (do you mean type of variable, or type of initializer?)
    }

    @Override
    public JTypeMirror visit(ASTName node, Void data) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode (basically an AmbiguousName)
    }

    @Override
    public JTypeMirror visit(ASTCompilationUnit node, Void data) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode
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
    public JTypeMirror visitTypeDecl(ASTAnyTypeDeclaration node, Void data) {
        return ts.declaration(node.getSymbol());
    }

    @Override
    public JTypeMirror visit(ASTAnnotation node, Void data) {
        return node.getTypeNode().getTypeMirror();
    }

    @Override
    public JTypeMirror visitType(ASTType node, Void data) {
        return TypesFromAst.fromAst(ts, Substitution.EMPTY, node);
    }

    @Override
    public JTypeMirror visit(ASTVoidType node, Void data) {
        return ts.NO_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTPrimitiveType node, Void data) {
        return ts.getPrimitive(node.getKind());
    }

    @Override
    public JTypeMirror visit(ASTVariableDeclaratorId node, Void data) {
        boolean isTypeInferred = node.isTypeInferred();
        if (isTypeInferred && node.getInitializer() != null) {
            // var k = foo()

            ASTExpression initializer = node.getInitializer();
            return initializer == null ? ts.ERROR : TypeOps.projectUpwards(initializer.getTypeMirror());

        } else if (isTypeInferred && node.ancestors().get(2) instanceof ASTForeachStatement) {
            // for (var k : map.keySet())

            JTypeMirror iterableType = ((ASTForeachStatement) node.ancestors().get(2)).getIterableExpr().getTypeMirror();
            iterableType = capture(iterableType);

            if (iterableType instanceof JArrayType) {
                return ((JArrayType) iterableType).getComponentType(); // component type is necessarily a type
            } else {
                JTypeMirror asSuper = iterableType.getAsSuper(ts.getClassSymbol(Iterable.class));
                if (asSuper instanceof JClassType) {
                    if (asSuper.isRaw()) {
                        return ts.OBJECT;
                    }
                    JTypeMirror componentType = ((JClassType) asSuper).getTypeArgs().get(0);
                    return TypeOps.projectUpwards(componentType);
                } else {
                    return ts.ERROR;
                }
            }

        } else if (isTypeInferred && node.isLambdaParameter()) {
            ASTLambdaParameter param = (ASTLambdaParameter) node.getParent();
            ASTLambdaExpression lambda = (ASTLambdaExpression) node.getNthParent(3);
            // force resolution of the enclosing lambda
            JMethodSig mirror = lambda.getFunctionalMethod();
            if (mirror == null || mirror == ts.UNRESOLVED_METHOD) {
                return ts.UNKNOWN;
            }
            return mirror.getFormalParameters().get(param.getIndexInParent());

        } else if (node.isEnumConstant()) {

            TypeNode enumClass = node.getEnclosingType();
            return enumClass.getTypeMirror();

        }

        ASTType typeNode = node.getTypeNode();
        if (typeNode == null) {
            return ts.ERROR;
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
     *
     * <p>Note that some poly expression are only poly part of the time.
     * In particular, method calls with explicit type arguments, and non-diamond
     * constructor calls, are standalone. To reduce the number of branches in the
     * code they still go through Infer, so that their method type is set like all
     * the others.
     *
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
            throw new AssertionError("Unknown operator for " + node);
        }
    }

    private boolean isUnresolved(JTypeMirror t) {
        return t == ts.UNKNOWN;
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
            throw new AssertionError("Unknown operator for " + node);
        }
    }

    @Override
    public JTypeMirror visit(ASTPatternExpression node, Void data) {
        ASTPattern pattern = node.getPattern();
        if (pattern instanceof ASTTypeTestPattern) {
            return ((ASTTypeTestPattern) pattern).getTypeNode().getTypeMirror();
        }
        throw new IllegalArgumentException("Unknown pattern " + pattern);
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
    public JTypeMirror visit(ASTBooleanLiteral node, Void data) {
        return ts.BOOLEAN;
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
            return id.isTypeInferred() ? ts.ERROR : id.getTypeMirror();
        } else if (parent instanceof ASTArrayInitializer) {
            JTypeMirror tm = ((ASTArrayInitializer) parent).getTypeMirror();
            return tm instanceof JArrayType ? ((JArrayType) tm).getComponentType()
                                            : ts.ERROR;
        }
        return ts.ERROR;
    }


    @Override
    public JTypeMirror visit(ASTVariableAccess node, Void data) {
        if (node.getParent() instanceof ASTSwitchLabel) {
            // may be an enum constant, in which case symbol table doesn't help (this is a documented thing)
            ASTSwitchLike switchParent = node.ancestors(ASTSwitchLike.class).firstOrThrow();
            JTypeMirror testedType = switchParent.getTestedExpression().getTypeMirror();
            JTypeDeclSymbol testedSym = testedType.getSymbol();
            if (testedSym instanceof JClassSymbol && ((JClassSymbol) testedSym).isEnum()) {
                JFieldSymbol enumConstant = ((JClassSymbol) testedSym).getDeclaredField(node.getName());
                if (enumConstant == null) {
                    // no symbol, but type is there
                    return ts.declaration((JClassSymbol) testedSym);
                } else {
                    node.setTypedSym(ts.sigOf(testedType, enumConstant));
                    return testedType;
                }
            } // fallthrough
        }

        @Nullable JVariableSig result = node.getSymbolTable().variables().resolveFirst(node.getName());
        if (result == null) {
            // An out-of-scope field. Use context to resolve it.
            return polyResolution.getContextTypeForStandaloneFallback(node);
        }
        node.setTypedSym(result);

        JVariableSymbol symbol = result.getSymbol();
        if (symbol instanceof JLocalVariableSymbol) {
            ASTVariableDeclaratorId id = symbol.tryGetNode();
            assert id != null : "Expected a local declaration";
            if (id.isLambdaParameter()) {
                // then the type of the parameter depends on the type
                // of the lambda, which most likely depends on the overload
                // resolution of an enclosing invocation context
                ASTLambdaExpression lambda = id.ancestors(ASTLambdaExpression.class).firstOrThrow();
                lambda.getTypeMirror(); // force resolution, noop if we're already doing its resolution
            }
        }

        // https://docs.oracle.com/javase/specs/jls/se14/html/jls-6.html#jls-6.5.6
        // Only capture if the name is on the RHS
        return node.getAccessType() == AccessType.READ ? TypeConversion.capture(result.getTypeMirror())
                                                       : result.getTypeMirror();
    }

    @Override
    public @NonNull JTypeMirror visit(ASTLambdaParameter node, Void data) {
        if (node.getTypeNode() != null) {
            // explicitly typed
            return node.getTypeNode().getTypeMirror();
        }
        ASTLambdaExpression lambda = node.ancestors(ASTLambdaExpression.class).firstOrThrow();
        lambda.getTypeMirror();

        JMethodSig m = lambda.getFunctionalMethod(); // this forces resolution of the lambda
        if (m != getTypeSystem().UNRESOLVED_METHOD) {
            return m.getFormalParameters().get(node.getIndexInParent());
        }
        return ts.UNKNOWN;
    }

    @Override
    public JTypeMirror visit(ASTFieldAccess node, Void data) {
        JTypeMirror qualifierT = capture(node.getQualifier().getTypeMirror());
        if (qualifierT == ts.UNKNOWN) {
            return polyResolution.getContextTypeForStandaloneFallback(node);
        }

        NameResolver<FieldSig> fieldResolver = TypeOps.getMemberFieldResolver(qualifierT, node.getRoot().getPackageName(), node.getEnclosingType().getSymbol(), node.getName());

        FieldSig sig = fieldResolver.resolveFirst(node.getName()); // could be an ambiguity error
        node.setTypedSym(sig);

        if (sig == null) {
            return polyResolution.getContextTypeForStandaloneFallback(node);
        }

        // https://docs.oracle.com/javase/specs/jls/se14/html/jls-6.html#jls-6.5.6
        // Only capture if the name is on the RHS
        return node.getAccessType() == AccessType.READ ? TypeConversion.capture(sig.getTypeMirror())
                                                       : sig.getTypeMirror();
    }


    @Override
    public JTypeMirror visit(ASTArrayAccess node, Void data) {
        JTypeMirror compType;
        JTypeMirror arrType = node.getQualifier().getTypeMirror();
        if (arrType instanceof JArrayType) {
            compType = ((JArrayType) arrType).getComponentType();
        } else if (arrType == ts.UNKNOWN) {
            compType = polyResolution.getContextTypeForStandaloneFallback(node);
        } else {
            compType = ts.ERROR;
        }
        return capture(compType);
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
        return ts.UNKNOWN;
    }
}

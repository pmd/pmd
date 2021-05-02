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
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;

/**
 * Lazy version of ClassTypeResolver. This is not appropriate for
 * all nodes, though it is appropriate for standalone expressions.
 */
final class LazyTypeResolver extends JavaVisitorBase<TypingContext, @NonNull JTypeMirror> {

    private final TypeSystem ts;
    private final PolyResolution polyResolution;
    private final JClassType stringType;
    private final JavaAstProcessor processor;
    private final Infer infer;


    LazyTypeResolver(JavaAstProcessor processor, TypeInferenceLogger logger) {
        this.ts = processor.getTypeSystem();
        this.infer = new Infer(ts, processor.getJdkVersion(), logger);
        this.polyResolution = new PolyResolution(infer);
        this.stringType = (JClassType) TypesFromReflection.fromReflect(String.class, ts);
        this.processor = processor;
    }

    ExprContext getConversionContextForExternalUse(ASTExpression e) {
        return polyResolution.getConversionContextForExternalUse(e);
    }

    public Infer getInfer() {
        return infer;
    }

    public JavaAstProcessor getProcessor() {
        return processor;
    }

    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JTypeMirror visitJavaNode(JavaNode node, TypingContext ctx) {
        throw new IllegalArgumentException("Not a type node:" + node);
    }


    @Override
    public JTypeMirror visit(ASTVariableDeclarator node, TypingContext ctx) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode (do you mean type of variable, or type of initializer?)
    }

    @Override
    public JTypeMirror visit(ASTName node, TypingContext ctx) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode (basically an AmbiguousName)
    }

    @Override
    public JTypeMirror visit(ASTCompilationUnit node, TypingContext ctx) {
        return ts.NO_TYPE; // TODO shouldn't be a typenode
    }


    @Override
    public JTypeMirror visit(ASTFormalParameter node, TypingContext ctx) {
        return node.getVarId().getTypeMirror(ctx);
    }


    @Override
    public JTypeMirror visit(ASTTypeParameter node, TypingContext ctx) {
        return node.getSymbol().getTypeMirror();
    }

    @Override
    public JTypeMirror visitTypeDecl(ASTAnyTypeDeclaration node, TypingContext ctx) {
        return ts.declaration(node.getSymbol());
    }

    @Override
    public JTypeMirror visit(ASTAnnotation node, TypingContext ctx) {
        return node.getTypeNode().getTypeMirror(ctx);
    }

    @Override
    public JTypeMirror visitType(ASTType node, TypingContext ctx) {
        return TypesFromAst.fromAst(ts, Substitution.EMPTY, node);
    }

    @Override
    public JTypeMirror visit(ASTVoidType node, TypingContext ctx) {
        return ts.NO_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTPrimitiveType node, TypingContext ctx) {
        return ts.getPrimitive(node.getKind());
    }

    @Override
    public JTypeMirror visit(ASTVariableDeclaratorId node, TypingContext ctx) {
        boolean isTypeInferred = node.isTypeInferred();
        if (isTypeInferred && node.getInitializer() != null) {
            // var k = foo()

            ASTExpression initializer = node.getInitializer();
            return initializer == null ? ts.ERROR : TypeOps.projectUpwards(initializer.getTypeMirror(ctx));

        } else if (isTypeInferred && node.isForeachVariable()) {
            // for (var k : map.keySet())

            ASTForeachStatement foreachStmt = node.ancestors(ASTForeachStatement.class).firstOrThrow();
            JTypeMirror iterableType = foreachStmt.getIterableExpr().getTypeMirror(ctx);
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
            JTypeMirror contextualResult = ctx.apply(node.getSymbol());
            if (contextualResult != null) {
                return contextualResult;
            }
            // force resolution of the enclosing lambda
            JMethodSig mirror = lambda.getFunctionalMethod();
            if (isUnresolved(mirror)) {
                return ts.UNKNOWN;
            }
            return mirror.getFormalParameters().get(param.getIndexInParent());

        } else if (node.isEnumConstant()) {

            TypeNode enumClass = node.getEnclosingType();
            return enumClass.getTypeMirror(ctx);

        }

        ASTType typeNode = node.getTypeNode();
        if (typeNode == null) {
            return ts.ERROR;
        }

        // Type common to all declarations in the same statement
        JTypeMirror baseType = typeNode.getTypeMirror(ctx);
        ASTArrayDimensions extras = node.getExtraDimensions();

        return extras != null
               ? ts.arrayType(baseType, extras.size())
               : baseType;
    }

    /*
        EXPRESSIONS
     */

    @Override
    public JTypeMirror visit(ASTAssignmentExpression node, TypingContext ctx) {
        // The type of the assignment expression is the type of the variable after capture conversion
        return TypeConversion.capture(node.getLeftOperand().getTypeMirror(ctx));
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
    public JTypeMirror visit(ASTMethodCall node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTConditionalExpression node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTLambdaExpression node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTSwitchExpression node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTMethodReference node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTConstructorCall node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTExplicitConstructorInvocation node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTEnumConstant node, TypingContext ctx) {
        return handlePoly(node);
    }

    @Override
    public JTypeMirror visit(ASTInfixExpression node, TypingContext ctx) {
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
            final JTypeMirror lhs = node.getLeftOperand().getTypeMirror(ctx);
            final JTypeMirror rhs = node.getRightOperand().getTypeMirror(ctx);

            if (lhs.isNumeric() && rhs.isNumeric()) {
                // NUMERIC(N) & NUMERIC(M)  -> promote(N, M)
                return binaryNumericPromotion(lhs, rhs);
            } else if (lhs.equals(rhs)) {
                // BOOL       & BOOL        -> BOOL
                // UNRESOLVED & UNRESOLVED  -> UNKNOWN
                return lhs;
            } else if (isUnresolved(lhs) ^ isUnresolved(rhs)) {
                // UNRESOLVED & NUMERIC(N)  -> promote(N)
                // NUMERIC(N) & UNRESOLVED  -> promote(N)

                // BOOL       & UNRESOLVED  -> BOOL
                // UNRESOLVED & BOOL        -> BOOL

                // UNRESOLVED & anything    -> ERROR

                JTypeMirror resolved = isUnresolved(lhs) ? rhs : lhs;
                return resolved.isNumeric() ? unaryNumericPromotion(resolved)
                                            : resolved == ts.BOOLEAN ? resolved  // NOPMD #3205
                                                                     : ts.ERROR;
            } else {
                // anything else, including error types & such: ERROR
                return ts.ERROR;
            }
        }
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT:
            return unaryNumericPromotion(node.getLeftOperand().getTypeMirror(ctx));
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case MOD:
            final JTypeMirror lhs = node.getLeftOperand().getTypeMirror(ctx);
            final JTypeMirror rhs = node.getRightOperand().getTypeMirror(ctx);
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
        return t == ts.UNKNOWN;  // NOPMD CompareObjectsWithEquals
    }

    private boolean isUnresolved(JMethodSig m) {
        return m == null || m == ts.UNRESOLVED_METHOD;  // NOPMD CompareObjectsWithEquals
    }

    @Override
    public JTypeMirror visit(ASTUnaryExpression node, TypingContext ctx) {
        switch (node.getOperator()) {
        case UNARY_PLUS:
        case UNARY_MINUS:
        case COMPLEMENT:
            return unaryNumericPromotion(node.getOperand().getTypeMirror(ctx));
        case NEGATION:
            return ts.BOOLEAN;
        case PRE_INCREMENT:
        case PRE_DECREMENT:
        case POST_INCREMENT:
        case POST_DECREMENT:
            return node.getOperand().getTypeMirror(ctx);
        default:
            throw new AssertionError("Unknown operator for " + node);
        }
    }

    @Override
    public JTypeMirror visit(ASTPatternExpression node, TypingContext ctx) {
        ASTPattern pattern = node.getPattern();
        if (pattern instanceof ASTTypePattern) {
            return ((ASTTypePattern) pattern).getTypeNode().getTypeMirror(ctx);
        }
        throw new IllegalArgumentException("Unknown pattern " + pattern);
    }

    @Override
    public JTypeMirror visit(ASTCastExpression node, TypingContext ctx) {
        return node.getCastType().getTypeMirror(ctx);
    }

    @Override
    public JTypeMirror visit(ASTNullLiteral node, TypingContext ctx) {
        return ts.NULL_TYPE;
    }

    @Override
    public JTypeMirror visit(ASTCharLiteral node, TypingContext ctx) {
        return ts.CHAR;
    }

    @Override
    public JTypeMirror visit(ASTStringLiteral node, TypingContext ctx) {
        return stringType;
    }

    @Override
    public JTypeMirror visit(ASTNumericLiteral node, TypingContext ctx) {
        if (node.isIntegral()) {
            return node.isLongLiteral() ? ts.LONG : ts.INT;
        } else {
            return node.isFloatLiteral() ? ts.FLOAT : ts.DOUBLE;
        }
    }

    @Override
    public JTypeMirror visit(ASTBooleanLiteral node, TypingContext ctx) {
        return ts.BOOLEAN;
    }

    @Override
    public JTypeMirror visit(ASTClassLiteral node, TypingContext ctx) {
        JClassSymbol klassSym = ts.getClassSymbol(Class.class);
        assert klassSym != null : Class.class + " is missing from the classpath?";
        if (node.getTypeNode() instanceof ASTVoidType) {
            // void.class : Class<Void>
            return ts.parameterise(klassSym, listOf(ts.BOXED_VOID));
        } else {
            return ts.parameterise(klassSym, listOf(node.getTypeNode().getTypeMirror(ctx).box()));
        }
    }


    @Override
    public JTypeMirror visit(ASTArrayAllocation node, TypingContext ctx) {
        return node.getTypeNode().getTypeMirror(ctx);
    }

    @Override
    public JTypeMirror visit(ASTArrayInitializer node, TypingContext ctx) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTArrayAllocation) {
            return ((ASTArrayAllocation) parent).getTypeMirror(ctx);
        } else if (parent instanceof ASTVariableDeclarator) {
            ASTVariableDeclaratorId id = ((ASTVariableDeclarator) parent).getVarId();
            return id.isTypeInferred() ? ts.ERROR : id.getTypeMirror(ctx);
        } else if (parent instanceof ASTArrayInitializer) {
            JTypeMirror tm = ((ASTArrayInitializer) parent).getTypeMirror(ctx);
            return tm instanceof JArrayType ? ((JArrayType) tm).getComponentType()
                                            : ts.ERROR;
        }
        return ts.ERROR;
    }


    @Override
    public JTypeMirror visit(ASTVariableAccess node, TypingContext ctx) {
        if (node.getParent() instanceof ASTSwitchLabel) {
            // may be an enum constant, in which case symbol table doesn't help (this is documented on JSymbolTable#variables())
            ASTSwitchLike switchParent = node.ancestors(ASTSwitchLike.class).firstOrThrow();
            JTypeMirror testedType = switchParent.getTestedExpression().getTypeMirror(ctx);
            JTypeDeclSymbol testedSym = testedType.getSymbol();
            if (testedSym instanceof JClassSymbol && ((JClassSymbol) testedSym).isEnum()) {
                JFieldSymbol enumConstant = ((JClassSymbol) testedSym).getDeclaredField(node.getName());
                if (enumConstant != null) {
                    // field exists and can be resolved
                    node.setTypedSym(ts.sigOf(testedType, enumConstant));
                }
                return testedType;
            } // fallthrough
        }

        @Nullable JVariableSig result = node.getSymbolTable().variables().resolveFirst(node.getName());
        if (result == null) {
            // An out-of-scope field. Use context to resolve it.
            return polyResolution.getContextTypeForStandaloneFallback(node);
        }
        node.setTypedSym(result);

        JTypeMirror resultMirror = null;

        if (result.getSymbol() instanceof JLocalVariableSymbol) {
            ASTVariableDeclaratorId id = result.getSymbol().tryGetNode();
            // id may be null if this is a fake formal param sym, for record components
            if (id != null && id.isLambdaParameter()) {
                // then the type of the parameter depends on the type
                // of the lambda, which most likely depends on the overload
                // resolution of an enclosing invocation context
                resultMirror = id.getTypeMirror();
            }
        }

        if (resultMirror == null) {
            resultMirror = result.getTypeMirror();
        }

        // https://docs.oracle.com/javase/specs/jls/se14/html/jls-6.html#jls-6.5.6
        // Only capture if the name is on the RHS
        return node.getAccessType() == AccessType.READ ? TypeConversion.capture(resultMirror)
                                                       : resultMirror;
    }

    @Override
    public @NonNull JTypeMirror visit(ASTLambdaParameter node, TypingContext ctx) {
        if (node.getTypeNode() != null) {
            // explicitly typed
            return node.getTypeNode().getTypeMirror(ctx);
        }
        ASTLambdaExpression lambda = node.ancestors(ASTLambdaExpression.class).firstOrThrow();
        lambda.getTypeMirror(ctx);

        JMethodSig m = lambda.getFunctionalMethod(); // this forces resolution of the lambda
        if (!isUnresolved(m)) {
            return m.getFormalParameters().get(node.getIndexInParent());
        }
        return ts.UNKNOWN;
    }

    @Override
    public JTypeMirror visit(ASTFieldAccess node, TypingContext ctx) {
        JTypeMirror qualifierT = capture(node.getQualifier().getTypeMirror(ctx));
        if (isUnresolved(qualifierT)) {
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
    public JTypeMirror visit(ASTArrayAccess node, TypingContext ctx) {
        JTypeMirror compType;
        JTypeMirror arrType = node.getQualifier().getTypeMirror(ctx);
        if (arrType instanceof JArrayType) {
            compType = ((JArrayType) arrType).getComponentType();
        } else if (isUnresolved(arrType)) {
            compType = polyResolution.getContextTypeForStandaloneFallback(node);
        } else {
            compType = ts.ERROR;
        }
        return capture(compType);
    }

    @Override
    public JTypeMirror visit(ASTSuperExpression node, TypingContext ctx) {
        if (node.getQualifier() != null) {
            return node.getQualifier().getTypeMirror(ctx);
        } else {
            return ((JClassType) node.getEnclosingType().getTypeMirror(ctx)).getSuperClass();
        }
    }

    @Override
    public JTypeMirror visit(ASTThisExpression node, TypingContext ctx) {
        return node.getQualifier() != null
               ? node.getQualifier().getTypeMirror(ctx)
               : node.getEnclosingType().getTypeMirror(ctx);
    }

    @Override
    public JTypeMirror visit(ASTAmbiguousName node, TypingContext ctx) {
        return ts.UNKNOWN;
    }
}

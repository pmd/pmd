/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 *
 */
public class UnnecessaryBoxingRule extends AbstractJavaRulechainRule {

    private static final Set<String> INTERESTING_NAMES = setOf(
        "valueOf",
        "booleanValue",
        "charValue",
        "byteValue",
        "shortValue",
        "intValue",
        "longValue",
        "floatValue",
        "doubleValue"
    );

    public UnnecessaryBoxingRule() {
        super(ASTMethodCall.class, ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (node.getTypeMirror().isBoxedPrimitive()) {
            ASTExpression arg = ASTList.singleOrNull(node.getArguments());
            if (arg == null) {
                return null;
            }
            JTypeMirror argT = arg.getTypeMirror();
            if (argT.isPrimitive()) {
                checkBox(ctx, node, arg);
            }
        }
        return null;
    }


    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (INTERESTING_NAMES.contains(node.getMethodName())) {
            OverloadSelectionResult overload = node.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }
            JMethodSig m = overload.getMethodType();
            boolean isValueOf = "valueOf".equals(node.getMethodName());
            ASTExpression qualifier = node.getQualifier();

            if (isValueOf && isWrapperValueOf(m)) {
                checkBox(ctx, node, node.getArguments().get(0));
            } else if (isValueOf && isBoxValueOfString(m)) {
                checkUnboxing(ctx, node, m.getDeclaringType());
            } else if (!isValueOf && qualifier != null && isUnboxingCall(m)) {
                checkBox(ctx, node, qualifier);
            }
        }
        return null;
    }

    private boolean isUnboxingCall(JMethodSig m) {
        return !m.isStatic() && m.getDeclaringType().isBoxedPrimitive() && m.getArity() == 0
            && m.getReturnType().isPrimitive();
    }

    private boolean isWrapperValueOf(JMethodSig m) {
        return m.isStatic()
            && m.getArity() == 1
            && m.getDeclaringType().isBoxedPrimitive()
            && m.getFormalParameters().get(0).isPrimitive();
    }

    private boolean isBoxValueOfString(JMethodSig m) {
        // eg Integer.valueOf("2")
        return m.isStatic()
                && (m.getArity() == 1 || m.getArity() == 2)
                && m.getDeclaringType().isBoxedPrimitive()
                && TypeTestUtil.isA(String.class, m.getFormalParameters().get(0));
    }

    private void checkBox(
        RuleContext rctx,
        ASTExpression conversionExpr,
        ASTExpression convertedExpr
    ) {
        // the conversion looks like
        //  CTX _ = conversion(sourceExpr)

        // we have the following data flow:
        //      sourceExpr -> convInput -> convOutput -> ctx
        //                 1            2             3
        // where 1 and 3 are implicit conversions which we assume are
        // valid because the code should compile.

        // we want to report a violation if this is equivalent to
        //      sourceExpr -> ctx

        // which means testing that
        // 1. the result of the implicit conversion of sourceExpr
        // with context type ctx is the same type as the result of conversion 3
        // 2. conversion 2 does not truncate the value

        // We cannot just test compatibility of the source to the ctx,
        // because of situations like
        //   int i = integer.byteValue()
        // where the conversion actually truncates the input value
        // (here we do sourceExpr=Integer (-> convInput=Integer) -> convOutput=byte -> ctx=int).

        JTypeMirror sourceType = convertedExpr.getTypeMirror();
        JTypeMirror conversionOutput = conversionExpr.getTypeMirror();
        ExprContext ctx = conversionExpr.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();

        if (sourceType.isPrimitive()
            && !conversionOutput.isPrimitive()
            && ctxType == null
            && isObjectConversionNecessary(conversionExpr)) {
            // eg Integer.valueOf(2).equals(otherInteger)
            return;
        }

        String reason = null;
        if (sourceType.equals(conversionOutput)) {
            reason = "boxing of boxed value";
        } else if (isImplicitlyTypedLambdaReturnExpr(conversionExpr)
            || ctxType != null && conversionIsImplicitlyRealisable(sourceType, ctxType, ctx, conversionOutput)) {
            
            // Check if this unboxing is required for correct overload selection
            if (sourceType.isBoxedPrimitive() && conversionOutput.isPrimitive() 
                && isUnboxingRequiredForOverloadSelection(conversionExpr, convertedExpr)) {
                return;
            }
            if (sourceType.unbox().equals(conversionOutput)) {
                reason = "explicit unboxing";
            } else if (sourceType.box().equals(conversionOutput)) {
                reason = "explicit boxing";
            } else if (ctxType != null) {
                reason = "explicit conversion from " + TypePrettyPrint.prettyPrintWithSimpleNames(sourceType)
                    + " to " + TypePrettyPrint.prettyPrintWithSimpleNames(ctxType);
                if (!conversionOutput.equals(ctxType)) {
                    reason += " through " + TypePrettyPrint.prettyPrintWithSimpleNames(conversionOutput);
                }
            }

        }
        if (reason != null) {
            rctx.addViolation(conversionExpr, reason);
        }
    }


    private static boolean conversionIsImplicitlyRealisable(JTypeMirror sourceType, JTypeMirror ctxType, ExprContext ctx, JTypeMirror conversionOutput) {
        JTypeMirror conv = implicitConversionResult(sourceType, ctxType, ctx.getKind());
        return conv != null
            && conv.equals(implicitConversionResult(conversionOutput, ctxType, ctx.getKind()))
            && conversionDoesNotChangesValue(sourceType, conversionOutput);
    }

    /**
     * Check if the unboxing conversion is required for correct method overload selection.
     * Returns true if removing the unboxing could cause a different method overload to be selected,
     * or make the call ambiguous.
     */
    private boolean isUnboxingRequiredForOverloadSelection(ASTExpression conversionExpr, ASTExpression convertedExpr) {
        // Find the invocation and argument index
        JavaNode parent = conversionExpr.getParent();
        InvocationNode invocation;
        int argIndex;
        
        if (parent instanceof ASTList && parent.getParent() instanceof InvocationNode) {
            invocation = (InvocationNode) parent.getParent();
            argIndex = conversionExpr.getIndexInParent();
        } else if (parent instanceof InvocationNode) {
            invocation = (InvocationNode) parent;
            argIndex = 0;
        } else {
            return false;
        }
        
        // Get the method and validate we have a boxed->primitive conversion
        OverloadSelectionResult selection = invocation.getOverloadSelectionInfo();
        if (selection.isFailed() || invocation.getArguments() == null) {
            return false;
        }
        
        JTypeMirror boxedType = convertedExpr.getTypeMirror();
        if (!boxedType.isBoxedPrimitive() || !conversionExpr.getTypeMirror().isPrimitive()
            || !selection.ithFormalParam(argIndex).isPrimitive()) {
            return false;
        }
        // For a plain unboxing like intValue() on an Integer, loose invocation accepts the boxed value for the same
        // parameter types as the unboxed one (JLS 5.3). Without the unboxing, the second and third phases then find
        // the same methods (JLS 15.12.2.3, 15.12.2.4), so a selected variable arity method stays selected.
        boolean pureUnboxing = boxedType.unbox().equals(conversionExpr.getTypeMirror());
        
        // The argument types as they would be without the unboxing
        List<ASTExpression> args = invocation.getArguments().toList();
        List<JTypeMirror> argTypes = new ArrayList<>();
        for (ASTExpression arg : args) {
            argTypes.add(arg.getTypeMirror());
        }
        argTypes.set(argIndex, boxedType);

        JMethodSig currentMethod = selection.getMethodType();
        boolean isDiamond = invocation instanceof ASTConstructorCall && ((ASTConstructorCall) invocation).usesDiamondTypeArgs();
        for (JMethodSig overload : getOverloads(invocation, currentMethod, isDiamond)) {
            if (overload.getSymbol().equals(currentMethod.getSymbol())) {
                continue;
            }
            // Types that mention a type variable inferred from the arguments are compared by erasure
            List<JTypeVar> inferredVars = new ArrayList<>(overload.getTypeParameters());
            if (isDiamond) {
                inferredVars.addAll(overload.getSymbol().getEnclosingClass().getTypeParameters());
            }
            boolean byFixedArity = overload.getArity() == args.size()
                && isApplicable(overload.getFormalParameters(), args, argTypes, argIndex, inferredVars);
            if (selection.isVarargsCall()) {
                // The selected method needs variable arity invocation (JLS 15.12.2.4), so an overload applicable
                // by fixed arity would be selected instead, and one applicable by variable arity may be, unless the
                // selected one is more specific
                if (byFixedArity || !pureUnboxing && isApplicableByVariableArity(overload, args, argTypes, argIndex, inferredVars)
                    && !isMoreSpecific(currentMethod, overload, Math.max(args.size(), overload.getArity()), true)) {
                    return true;
                }
            } else if (byFixedArity && !isMoreSpecific(currentMethod, overload, args.size(), false)) {
                // The selected parameter is primitive, so without the unboxing the selected method is applicable by
                // loose invocation only (JLS 15.12.2.3). It stays selected only if it is more specific than every
                // other applicable overload (JLS 15.12.2.5), which an overload accepting the boxed argument as a
                // reference never is.
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the methods or constructors that the compiler searches together with the selected one
     * (JLS 15.9.3, 15.12.1).
     */
    private static Iterable<JMethodSig> getOverloads(InvocationNode invocation, JMethodSig selected, boolean isDiamond) {
        ASTTypeDeclaration enclosingType = invocation.getEnclosingType();
        if (enclosingType == null) {
            return Collections.emptyList();
        }
        JClassSymbol site = enclosingType.getSymbol();
        if (selected.isConstructor()) {
            if (!(selected.getDeclaringType() instanceof JClassType)) {
                return Collections.emptyList();
            }
            JClassType classType = (JClassType) selected.getDeclaringType();
            if (isDiamond) {
                // The class type arguments are inferred from the arguments too
                JClassType outer = classType.getEnclosingType();
                classType = outer != null
                    ? outer.selectInner(classType.getSymbol(), classType.getSymbol().getTypeParameters())
                    : classType.getGenericTypeDeclaration();
            }
            // Outside its package, a protected constructor is accessible to super(...) and to an anonymous class, but not
            // to an ordinary class instance creation (JLS 6.6.2.2)
            boolean isAnonymous = invocation instanceof ASTConstructorCall && ((ASTConstructorCall) invocation).isAnonymousClass();
            JClassSymbol accessSite = isAnonymous
                ? ((ASTConstructorCall) invocation).getAnonymousClassDeclaration().getSymbol()
                : site;
            List<JMethodSig> constructors = new ArrayList<>();
            for (JMethodSig constructor : TypeOps.filterAccessible(classType.getConstructors(), accessSite)) {
                JExecutableSymbol symbol = constructor.getSymbol();
                if (!(invocation instanceof ASTConstructorCall) || isAnonymous || !Modifier.isProtected(symbol.getModifiers())
                    || symbol.getEnclosingClass().getPackageName().equals(site.getPackageName())) {
                    constructors.add(constructor);
                }
            }
            return constructors;
        }
        if (!(invocation instanceof ASTMethodCall)) {
            return Collections.emptyList();
        }
        ASTMethodCall call = (ASTMethodCall) invocation;
        ASTExpression qualifier = call.getQualifier();
        if (qualifier != null) {
            JTypeMirror qualifierType = qualifier instanceof ASTConstructorCall && ((ASTConstructorCall) qualifier).isAnonymousClass()
                ? ((ASTConstructorCall) qualifier).getAnonymousClassDeclaration().getTypeMirror()
                : qualifier.getTypeMirror();
            // Instance methods are candidates for a type name qualifier too, selecting one is an error (JLS 15.12.3)
            JTypeMirror memberSource = TypeOps.getMemberSource(qualifierType);
            List<JMethodSig> methods = new ArrayList<>();
            for (JMethodSig m : withoutInheritedInterfaceStatics(
                getAccessibleMethods(memberSource, call.getMethodName(), enclosingType), memberSource)) {
                if (isAccessibleThrough(m, qualifier, memberSource, enclosingType)) {
                    methods.add(m);
                }
            }
            return methods;
        }
        // The innermost enclosing type that has a member method with that name, otherwise a static import
        for (ASTTypeDeclaration type = enclosingType; type != null; type = type.getEnclosingType()) {
            List<JMethodSig> methods = withoutInheritedInterfaceStatics(
                getAccessibleMethods(type.getTypeMirror(), call.getMethodName(), enclosingType), type.getTypeMirror());
            if (!methods.isEmpty()) {
                return methods;
            }
        }
        return call.getSymbolTable().methods().resolve(call.getMethodName());
    }

    /**
     * Returns the member methods of the type with that name that are accessible from the call (JLS 6.6). Code
     * nested in a subclass may use the protected members of its superclasses, so each enclosing class is
     * an access site (JLS 6.6.2).
     */
    private static List<JMethodSig> getAccessibleMethods(JTypeMirror type, String name, ASTTypeDeclaration enclosingType) {
        List<JMethodSig> methods = new ArrayList<>();
        for (ASTTypeDeclaration site = enclosingType; site != null; site = site.getEnclosingType()) {
            for (JMethodSig m : TypeOps.getMethodsOf(type, name, false, site.getSymbol())) {
                if (methods.stream().noneMatch(it -> it.getSymbol().equals(m.getSymbol()))) {
                    methods.add(m);
                }
            }
        }
        return methods;
    }

    /**
     * Whether the method may be called on the qualifier. Outside its package, a protected instance method may
     * only be called on an expression whose type is an enclosing subclass or a subclass of it (JLS 6.6.2.1).
     */
    private static boolean isAccessibleThrough(JMethodSig method, ASTExpression qualifier, JTypeMirror qualifierType,
                                               ASTTypeDeclaration enclosingType) {
        JExecutableSymbol symbol = method.getSymbol();
        JClassSymbol declaringClass = symbol.getEnclosingClass();
        if (!Modifier.isProtected(symbol.getModifiers()) || method.isStatic()
            || qualifier instanceof ASTSuperExpression || qualifier instanceof ASTTypeExpression
            || declaringClass.getPackageName().equals(enclosingType.getSymbol().getPackageName())
            || !(qualifierType.getSymbol() instanceof JClassSymbol)) {
            return true;
        }
        JClassSymbol qualifierClass = (JClassSymbol) qualifierType.getSymbol();
        for (ASTTypeDeclaration site = enclosingType; site != null; site = site.getEnclosingType()) {
            if (isSubclass(site.getSymbol(), declaringClass) && isSubclass(qualifierClass, site.getSymbol())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSubclass(JClassSymbol sub, JClassSymbol cls) {
        for (JClassSymbol c = sub; c != null; c = c.getSuperclass()) {
            if (c.equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /** Static methods of an interface are not inherited (JLS 8.4.8, 9.4.1). */
    private static List<JMethodSig> withoutInheritedInterfaceStatics(List<JMethodSig> methods, JTypeMirror type) {
        List<JMethodSig> result = new ArrayList<>();
        for (JMethodSig m : methods) {
            JClassSymbol owner = m.getSymbol().getEnclosingClass();
            if (!m.isStatic() || !owner.isInterface() || owner.equals(type.getSymbol())) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Whether a method with the given parameter types is applicable to the arguments by strict or loose
     * invocation (JLS 15.12.2.2, 15.12.2.3). This does not run the inference: parameter types that mention
     * one of the {@code inferredVars} are compared by their erasure, and so are the types of the arguments
     * other than the changed one. Lambdas and method references are checked for potential compatibility,
     * other arguments whose type depends on the target type are assumed to fit.
     */
    private static boolean isApplicable(List<JTypeMirror> params, List<ASTExpression> args,
                                        List<JTypeMirror> argTypes, int changedArgIndex, List<JTypeVar> inferredVars) {
        for (int i = 0; i < params.size(); i++) {
            JTypeMirror param = TypeOps.mentionsAny(params.get(i), inferredVars) ? params.get(i).getErasure() : params.get(i);
            ASTExpression arg = args.get(i);
            boolean applicable;
            if (i == changedArgIndex) {
                applicable = isInvocationConvertible(argTypes.get(i), param, false);
            } else if (arg instanceof ASTLambdaExpression || arg instanceof ASTMethodReference) {
                // A lambda or method reference is potentially compatible with a type parameter of the method, and is
                // then not pertinent to applicability (JLS 15.12.2.1, 15.12.2.2)
                applicable = inferredVars.contains(params.get(i)) || isCompatibleFunction(arg, param);
            } else if (arg instanceof ASTNullLiteral) {
                // The null type converts to every reference type, but not to a primitive type (JLS 5.3)
                applicable = !param.isPrimitive();
            } else {
                applicable = mayFit(arg, param);
            }
            if (!applicable) {
                return false;
            }
        }
        return true;
    }

    private static boolean isApplicableByVariableArity(JMethodSig overload, List<ASTExpression> args,
                                                       List<JTypeMirror> argTypes, int changedArgIndex, List<JTypeVar> inferredVars) {
        if (!overload.isVarargs() || args.size() < overload.getArity() - 1) {
            return false;
        }
        List<JTypeMirror> params = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            params.add(overload.ithFormalParam(i, true));
        }
        return isApplicable(params, args, argTypes, changedArgIndex, inferredVars);
    }
        
    /**
     * Whether a value of the argument type can be passed to the parameter by widening, boxing or unboxing
     * (JLS 5.3).
     */
    private static boolean isInvocationConvertible(JTypeMirror argType, JTypeMirror param, boolean byErasure) {
        if (param.isPrimitive()) {
            JTypeMirror type = byErasure ? argType.getErasure() : argType;
            JTypeMirror primitive = type.isBoxedPrimitive() ? type.unbox() : type;
            return primitive.isPrimitive() && primitive.isSubtypeOf(param);
        }
        // The argument is not erased, so that a type variable or an intersection keeps all its bounds
        JTypeMirror reference = argType.isPrimitive() ? argType.box() : argType;
        if (byErasure) {
            // Unchecked conversion is allowed too (JLS 5.3), eg a raw ArrayList to RandomAccess, whose supertypes are erased
            return TypeOps.isUnresolved(param) || reference.isConvertibleTo(param.getErasure()).somehow();
        }
        return reference.isSubtypeOf(param);
    }

    /**
     * Whether another argument may be passed to the parameter. A conditional expression of reference type and any
     * switch expression are compatible with the target when each result expression is (JLS 15.25.3, 15.28.1), so each
     * result expression is checked. A generic method call that returns one of its type parameters may fit any
     * parameter, unless an argument fixes it (JLS 18.4). Other generic calls keep their erasure. An expression whose
     * type is not known is assumed to fit.
     */
    private static boolean mayFit(ASTExpression arg, JTypeMirror param) {
        JTypeMirror type = arg.getTypeMirror();
        if (type instanceof JClassType && TypeOps.isUnresolved(type) && param.isPrimitive()) {
            // An unresolved class is not a box type, so it is not convertible to a primitive type
            return false;
        } else if (arg instanceof ASTNullLiteral || arg instanceof ASTLambdaExpression || arg instanceof ASTMethodReference
            || TypeOps.isUnresolved(type)
            || arg.descendantsOrSelf().filterIs(InvocationNode.class).any(it -> it.getOverloadSelectionInfo().isFailed())) {
            return true;
        } else if (arg instanceof ASTConditionalExpression) {
            ASTConditionalExpression conditional = (ASTConditionalExpression) arg;
            ASTExpression thenBranch = conditional.getThenBranch();
            ASTExpression elseBranch = conditional.getElseBranch();
            if (!type.isPrimitive()) {
                return mayFit(thenBranch, param) && mayFit(elseBranch, param);
            }
            // The type of a numeric conditional is the narrower operand type when the other operand is an int constant
            // that it can represent (JLS 15.25.2), eg char for b ? 'a' : 1, which PMD types as int
            return isInvocationConvertible(type, param, true)
                || TypeOps.isUnresolved(thenBranch.getTypeMirror()) || TypeOps.isUnresolved(elseBranch.getTypeMirror())
                || isNarrowedBy(thenBranch, elseBranch, param) || isNarrowedBy(elseBranch, thenBranch, param);
        } else if (arg instanceof ASTSwitchExpression) {
            return ((ASTSwitchExpression) arg).getYieldExpressions().all(it -> mayFit(it, param));
        } else if (arg instanceof ASTMethodCall && ((ASTMethodCall) arg).getExplicitTypeArguments() == null) {
            JExecutableSymbol method = ((ASTMethodCall) arg).getMethodType().getSymbol();
            JTypeMirror returnType = method.getReturnType(Substitution.EMPTY);
            JTypeMirror component = returnType;
            int dimensions = 0;
            while (component.isArray()) {
                component = ((JArrayType) component).getComponentType();
                dimensions++;
            }
            if (method.getTypeParameters().contains(component) && !isFixedByArgument((ASTMethodCall) arg, returnType)) {
                // An array of a type variable is still an array of references, so it never fits a List or an int[]
                JTypeMirror objects = arg.getTypeSystem().arrayType(arg.getTypeSystem().OBJECT, dimensions);
                JTypeMirror erasedParam = param.getErasure();
                return dimensions == 0 || TypeOps.isUnresolved(param)
                    || erasedParam.isSubtypeOf(objects) || objects.isSubtypeOf(erasedParam);
            }
        }
        return isInvocationConvertible(type, param, true);
    }

    private static boolean isNarrowedBy(ASTExpression operand, ASTExpression constant, JTypeMirror param) {
        JTypeMirror narrow = operand.getTypeMirror().unbox();
        Object value = constant.getConstValue();
        if (!(value instanceof Integer) || !constant.getTypeMirror().isPrimitive(PrimitiveTypeKind.INT)) {
            return false;
        }
        int v = (Integer) value;
        boolean representable = narrow.isPrimitive(PrimitiveTypeKind.BYTE) && v == (byte) v
            || narrow.isPrimitive(PrimitiveTypeKind.SHORT) && v == (short) v
            || narrow.isPrimitive(PrimitiveTypeKind.CHAR) && v == (char) v;
        return representable && isInvocationConvertible(narrow, param, true);
    }
        
    /**
     * Whether a standalone argument of the call is passed to a parameter of the same type as the return type, and each
     * other argument whose parameter mentions a type parameter of the method is standalone too. The type of the first
     * is then a proper lower bound of the type variable, which is inferred from its lower bounds whatever the target
     * (JLS 18.4), eg {@code Objects.requireNonNull(list)} or {@code Arrays.copyOf(array, n)}. A poly expression may
     * add a lower bound that depends on the target, eg {@code V[]} for a generic call returning an array.
     */
    private static boolean isFixedByArgument(ASTMethodCall call, JTypeMirror returnType) {
        JExecutableSymbol method = call.getMethodType().getSymbol();
        List<JTypeMirror> params = method.getFormalParameterTypes(Substitution.EMPTY);
        int fixedArity = method.isVarargs() ? params.size() - 1 : params.size();
        boolean fixed = false;
        for (int i = 0; i < call.getArguments().size(); i++) {
            ASTExpression arg = call.getArguments().get(i);
            JTypeMirror param = params.get(Math.min(i, params.size() - 1));
            boolean standalone = !TypeOps.isUnresolved(arg.getTypeMirror())
                && !(arg instanceof InvocationNode || arg instanceof ASTConditionalExpression || arg instanceof ASTSwitchExpression
                     || arg instanceof ASTLambdaExpression || arg instanceof ASTMethodReference);
            if (arg instanceof ASTNullLiteral) {
                continue;
            } else if (!standalone && TypeOps.mentionsAny(param, method.getTypeParameters())) {
                return false;
            } else if (standalone && i < fixedArity && param.equals(returnType)) {
                fixed = true;
            }
        }
        return fixed;
    }
        
    /**
     * Whether a lambda or method reference may be compatible with the parameter type (JLS 15.12.2.1, 15.12.2.2).
     * Like the type inference, this checks the arity of a lambda and whether its body produces a value.
     */
    private static boolean isCompatibleFunction(ASTExpression arg, JTypeMirror param) {
        JMethodSig function = TypeOps.findFunctionalInterfaceMethod(param);
        if (function == null) {
            return TypeOps.isUnresolved(param);
        }
        boolean voidFunction = function.getReturnType().isVoid();
        if (arg instanceof ASTLambdaExpression) {
            ASTLambdaExpression lambda = (ASTLambdaExpression) arg;
            return lambda.getArity() == function.getArity()
                && (voidFunction ? isVoidCompatible(lambda) : isValueCompatible(lambda));
        }
        return voidFunction || !isExactVoidMethodRef((ASTMethodReference) arg);
    }
        
    // Same criteria as LambdaMirrorImpl, which the type inference uses, except for parenthesized expressions and
    // endless loops
    private static boolean isVoidCompatible(ASTLambdaExpression lambda) {
        ASTBlock block = lambda.getBlockBody();
        if (block == null) {
            ASTExpression body = lambda.getExpressionBody();
            // A parenthesized expression is not a statement expression (JLS 14.8)
            return !body.isParenthesized()
                && (body instanceof ASTMethodCall || body instanceof ASTConstructorCall || body instanceof ASTAssignmentExpression
                    || body instanceof ASTUnaryExpression && !((ASTUnaryExpression) body).getOperator().isPure());
        }
        return block.descendants(ASTReturnStatement.class).none(it -> it.getExpr() != null);
    }

    private static boolean isValueCompatible(ASTLambdaExpression lambda) {
        ASTBlock block = lambda.getBlockBody();
        if (block == null) {
            // An implicitly typed lambda is not pertinent to applicability, any expression body is then compatible
            return !lambda.isExplicitlyTyped() || !lambda.getExpressionBody().getTypeMirror().isVoid();
        }
        // A block that cannot complete normally is value-compatible if each of its return statements has an
        // expression (JLS 15.27.2), eg one with a while (true) loop
        if (block.descendants(ASTReturnStatement.class).any(it -> it.getExpr() == null)) {
            return false;
        }
        return block.descendants(ASTReturnStatement.class).any(it -> it.getExpr() != null)
            || block.descendants(ASTThrowStatement.class).nonEmpty()
            || block.descendants(ASTLoopStatement.class).any(UnnecessaryBoxingRule::isEndlessLoop);
    }

    private static boolean isEndlessLoop(ASTLoopStatement loop) {
        // The condition is absent or may be a constant expression with value true (JLS 14.22), and no break
        // statement leaves the loop
        ASTExpression condition = loop.getCondition();
        return !(loop instanceof ASTForeachStatement)
            && (condition == null || mayBeConstantTrue(condition))
            && loop.descendants(ASTBreakStatement.class)
                   .all(it -> it.getTarget() != null && it.getTarget().ancestors().any(a -> a == loop));
    }

    /**
     * Whether the condition may be a constant expression with value true (JLS 15.29). PMD does not fold every
     * constant expression, eg {@code (boolean) true} or {@code "a" == "a"}, so a condition that is not folded is
     * assumed to be one unless it contains something that a constant expression cannot contain.
     */
    private static boolean mayBeConstantTrue(ASTExpression condition) {
        Object value = condition.getConstValue();
        return value != null ? Boolean.TRUE.equals(value) : mayBeConstant(condition, 0);
    }

    private static boolean mayBeConstant(ASTExpression expr, int depth) {
        return expr.descendantsOrSelf().all(it -> it instanceof ASTLiteral && !(it instanceof ASTNullLiteral)
            || it instanceof ASTCastExpression || it instanceof ASTType || it instanceof ASTTypeExpression
            || it instanceof ASTConditionalExpression || it instanceof ASTAmbiguousName
            || it instanceof ASTInfixExpression && ((ASTInfixExpression) it).getOperator() != BinaryOp.INSTANCEOF
            || it instanceof ASTUnaryExpression && ((ASTUnaryExpression) it).getOperator().isPure()
            || it instanceof ASTNamedReferenceExpr && mayBeConstantVariable((ASTNamedReferenceExpr) it, depth));
    }

    /**
     * Whether the name may refer to a constant variable, a final variable of primitive type or type String that is
     * initialized with a constant expression (JLS 4.12.4).
     */
    private static boolean mayBeConstantVariable(ASTNamedReferenceExpr name, int depth) {
        JVariableSymbol symbol = name.getReferencedSym();
        if (symbol == null) {
            return true;
        } else if (!symbol.isFinal()
            || !name.getTypeMirror().isPrimitive() && !TypeTestUtil.isExactlyA(String.class, name.getTypeMirror())) {
            return false;
        } else if (symbol instanceof JFieldSymbol && ((JFieldSymbol) symbol).getConstValue() != null) {
            return true;
        }
        JavaNode declaration = symbol.tryGetNode();
        ASTExpression initializer = declaration instanceof ASTVariableId ? ((ASTVariableId) declaration).getInitializer() : null;
        return initializer != null && depth < 8 && mayBeConstant(initializer, depth + 1);
    }

    /**
     * Whether the method reference is exact and its method returns void (JLS 15.13.1). Only an exact method reference
     * is pertinent to applicability (JLS 15.12.2.2), so only then does the void method make it incompatible with
     * a function type that returns a value (JLS 15.13.2).
     */
    private static boolean isExactVoidMethodRef(ASTMethodReference methodRef) {
        JMethodSig referenced = methodRef.getReferencedMethod();
        JTypeMirror searchType = methodRef.getQualifier().getTypeMirror();
        ASTTypeDeclaration site = methodRef.getEnclosingType();
        if (methodRef.isConstructorReference() || site == null || TypeOps.isUnresolved(searchType)
            || methodRef.getQualifier() instanceof ASTTypeExpression && searchType.isRaw()) {
            return false;
        }
        return referenced.getReturnType().isVoid()
            && !referenced.isVarargs()
            && !referenced.isGeneric()
            && getAccessibleMethods(TypeOps.getMemberSource(searchType), methodRef.getMethodName(), site).size() == 1;
    }

    /**
     * Whether the selected method is more specific than the other overload (JLS 15.12.2.5). Each of the first
     * {@code count} parameter types of the selected method, expanded for a variable arity invocation, has to be
     * a subtype of the erasure of the overload's. Then both methods were applicable in the same phase of the call
     * with the unboxing, so the compiler already found the selected one more specific. This does not depend on the
     * changed argument, whose parameter is primitive in both methods.
     */
    private static boolean isMoreSpecific(JMethodSig selected, JMethodSig overload, int count, boolean varargs) {
        for (int i = 0; i < count; i++) {
            if (!selected.ithFormalParam(i, varargs).isSubtypeOf(overload.ithFormalParam(i, varargs).getErasure())) {
                return false;
            }
        }
        return true;
    }


    private boolean isImplicitlyTypedLambdaReturnExpr(ASTExpression e) {
        JavaNode parent = e.getParent();
        if (isImplicitlyTypedLambda(parent)) {
            return true;
        } else if (parent instanceof ASTReturnStatement) {
            JavaNode target = JavaAstUtils.getReturnTarget((ASTReturnStatement) parent);
            return isImplicitlyTypedLambda(target);
        }
        return false;
    }


    private static boolean isImplicitlyTypedLambda(JavaNode e) {
        return e instanceof ASTLambdaExpression && !((ASTLambdaExpression) e).isExplicitlyTyped();
    }

    private boolean isObjectConversionNecessary(ASTExpression e) {
        JavaNode parent = e.getParent();
        return e.getIndexInParent() == 0 && parent instanceof QualifiableExpression;
    }


    private void checkUnboxing(
            RuleContext rctx,
            ASTMethodCall methodCall,
            JTypeMirror conversionOutput
    ) {
        // methodCall is e.g. Integer.valueOf("42")
        // this checks, whether the resulting type "Integer" is e.g. assigned to an "int"
        // which triggers implicit unboxing.
        ExprContext ctx = methodCall.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();

        if (ctxType != null) {
            if (isImplicitlyConvertible(conversionOutput, ctxType)) {
                if (conversionOutput.unbox().equals(ctxType)) {
                    rctx.addViolation(methodCall, "implicit unboxing. Use "
                            + conversionOutput.getSymbol().getSimpleName() + ".parse"
                            + StringUtils.capitalize(ctxType.getSymbol().getSimpleName()) + "(...) instead");
                }
            }
        }
    }

    private boolean isImplicitlyConvertible(JTypeMirror i, JTypeMirror o) {
        if (i.isBoxedPrimitive() && o.isBoxedPrimitive()) {
            // There is no implicit conversions between box types,
            // only between primitives
            return i.equals(o);
        } else if (i.isPrimitive() && o.isPrimitive()) {
            return i.isSubtypeOf(o);
        } else {
            return i.unbox().equals(o.unbox());
        }
    }


    /**
     * Type of the converted i in context ctx. If no implicit
     * conversion is possible then return null.
     */
    private static @Nullable JTypeMirror implicitConversionResult(JTypeMirror i, JTypeMirror ctx, ExprContextKind kind) {
        if (kind == ExprContextKind.CAST) {
            // In cast contexts conversions are less restrictive.
            if (i.isPrimitive() != ctx.isPrimitive()) {
                // Whether an unboxing or boxing conversion may occur depends on whether
                // the expression has a primitive type or not (not on the cast type).
                // https://docs.oracle.com/javase/specs/jls/se22/html/jls-5.html#jls-5.5
                return i.isPrimitive() ? i.box() : i.unbox();
            } else if (i.isNumeric() && ctx.isNumeric()) {
                // then narrowing or widening conversions occur to transform i to ctx
                return ctx;
            }
            // otherwise no conversion occurs
            return i;
        }
        if (!ctx.isPrimitive()) {
            // boxing
            return i.box().isSubtypeOf(ctx) ? i.box() : null;
        } else if (i.isBoxedPrimitive()) {
            // unboxing then optional widening
            return i.unbox().isSubtypeOf(ctx) ? ctx : null;
        } else if (i.isPrimitive()) {
            // widening
            return i.isSubtypeOf(ctx) ? ctx : null;
        }
        return null;
    }


    /**
     * Whether the explicit conversion from i to o changes the value.
     * This is e.g. truncating an integer.
     */
    private static boolean conversionDoesNotChangesValue(JTypeMirror i, JTypeMirror o) {
        return i.box().isSubtypeOf(o.box()) || i.unbox().isSubtypeOf(o.unbox());
    }

}

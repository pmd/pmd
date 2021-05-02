/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;

/**
 * Checks usages of explicity type arguments in a constructor call that
 * may be replaced by a diamond ({@code <>}). In order to determine this,
 * we mock a type resolution call site, which is equivalent to the expression
 * as if it had a diamond instead of explicit type arguments. We then perform
 * overload resolution for this fake call site. If overload resolution fails,
 * resolves to another overload, or if the inferred type is not compatible
 * with the expected context type, then the type arguments are unnecessary,
 * and removing them will not break the program.
 *
 * <p>Note that type inference in Java 8+ works differently from Java 7.
 * In Java 7, type arguments may be necessary in more places. The specifics
 * are however implemented within the type resolution code, and this rule does
 * not need to know about it.
 */
public class UseDiamondOperatorRule extends AbstractJavaRulechainRule {

    private static final String REPLACE_TYPE_ARGS_MESSAGE = "Explicit type arguments can be replaced by a diamond: `{0}`";
    private static final String RAW_TYPE_MESSAGE = "Raw type use may be avoided by using a diamond: `{0}`";
    /**
     * Maximum length of the argument list (including parentheses) for
     * it to be included in the violation message instead of an ellipsis {@code (...)}.
     */
    private static final int MAX_ARGS_LENGTH = 25;

    public UseDiamondOperatorRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall ctorCall, Object data) {
        ASTClassOrInterfaceType newTypeNode = ctorCall.getTypeNode();
        JTypeMirror newType = newTypeNode.getTypeMirror();

        ASTTypeArguments targs = newTypeNode.getTypeArguments();
        if (targs != null && targs.isDiamond()
            // if unresolved we can't know whether the class is generic or not
            || TypeOps.isUnresolved(newType)) {
            return null;
        }

        if (!newType.isGeneric() // targs may be null, in which case this would be a raw type
            || ctorCall.isAnonymousClass() && !supportsDiamondOnAnonymousClass(ctorCall)) {
            return null;
        }

        if (hasUnnecessaryTypeArgs(ctorCall)) {
            // report it
            JavaNode reportNode = targs == null ? newTypeNode : targs;
            String message = targs == null ? RAW_TYPE_MESSAGE : REPLACE_TYPE_ARGS_MESSAGE;
            String replaceWith = produceSuggestedExprImage(ctorCall);
            addViolationWithMessage(data, reportNode, message, new String[] { replaceWith });
        }
        return null;
    }

    private static boolean supportsDiamondOnAnonymousClass(ASTConstructorCall ctorCall) {
        return ctorCall.getAstInfo().getLanguageVersion().compareToVersion("9") >= 0;
    }


    /** Redo inference as described in the javadoc of this class. */
    private static boolean hasUnnecessaryTypeArgs(ASTConstructorCall call) {

        ExprContext context = call.getConversionContext();
        if (context.isMissing()) {
            return false;
        }

        SpyInvocMirror mirror = doOverloadResolutionWithoutTypeArgs(call, context.getTargetType());
        MethodCtDecl result = Objects.requireNonNull(mirror.getCtDecl());

        // inference succeeded without errors
        return !result.isFailed()
            // doesn't change bindings of lambdas
            && mirror.isEquivalentToUnderlyingAst()
            // inferred type is compatible with context
            && context.acceptsType(result.getMethodType().getReturnType());
    }

    private static SpyInvocMirror doOverloadResolutionWithoutTypeArgs(ASTConstructorCall call, JTypeMirror expectedType) {
        Infer infer = InternalApiBridge.getInferenceEntryPoint(call);
        SpyInvocMirror spyMirror = makeSpy(infer, call);
        MethodCallSite fakeCallSite = infer.newCallSite(spyMirror, expectedType);
        infer.inferInvocationRecursively(fakeCallSite);
        return spyMirror;
    }

    private static SpyInvocMirror makeSpy(Infer infer, ASTConstructorCall ctorCall) {
        // this may not mutate the AST
        JavaExprMirrors factory = JavaExprMirrors.forObservation(infer);
        CtorInvocationMirror baseMirror = (CtorInvocationMirror) factory.getTopLevelInvocationMirror(ctorCall);
        JTypeMirror newType = ctorCall.getTypeNode().getTypeMirror();
        return new SpyInvocMirror(baseMirror, (JClassType) newType);
    }


    private static String produceSuggestedExprImage(ASTConstructorCall ctor) {
        StringBuilder sb = new StringBuilder(30);
        sb.append("new ");
        produceSameTypeWithDiamond(ctor.getTypeNode(), sb, true);
        ASTArgumentList arguments = ctor.getArguments();
        String argsString;
        if (arguments.size() == 0) {
            argsString = "()";
        } else {
            CharSequence text = arguments.getText();
            if (text.length() <= MAX_ARGS_LENGTH && !StringUtils.contains(text, '\n')) {
                argsString = text.toString();
            } else {
                argsString = "(...)";
            }
        }
        return sb.append(argsString).toString();
    }

    private static StringBuilder produceSameTypeWithDiamond(ASTClassOrInterfaceType type, StringBuilder sb, boolean topLevel) {
        if (type.isFullyQualified()) {
            JTypeDeclSymbol sym = type.getTypeMirror().getSymbol();
            Objects.requireNonNull(sym);
            sb.append(sym.getPackageName()).append('.');
        } else {
            ASTClassOrInterfaceType qualifier = type.getQualifier();
            if (qualifier != null) {
                produceSameTypeWithDiamond(qualifier, sb, false).append('.');
            }
        }
        sb.append(type.getSimpleName());
        return topLevel ? sb.append("<>") : sb;
    }


    /** Proxy that pretends it has diamond type args. */
    private static final class SpyInvocMirror implements CtorInvocationMirror {

        private final CtorInvocationMirror base;
        private final JClassType modifiedNewType;

        SpyInvocMirror(CtorInvocationMirror base, JClassType baseNewType) {
            this.base = base;
            // see doc of CtorInvocationMirror#getNewType
            this.modifiedNewType = baseNewType.getGenericTypeDeclaration();
        }

        // overridden methods

        @Override
        public void setInferredType(JTypeMirror mirror) {
            // do nothing, we shouldn't affect the AST from here
        }

        @Override
        public @NonNull JTypeMirror getNewType() {
            return modifiedNewType;
        }

        @Override
        public boolean isDiamond() {
            return true; // pretend it is
        }

        @Override
        public List<JTypeMirror> getExplicitTypeArguments() {
            return Collections.emptyList(); // pretend they're not there
        }

        @Override
        public JavaNode getExplicitTargLoc(int i) {
            throw new IndexOutOfBoundsException();
        }

        // delegated methods

        @Override
        public void setCtDecl(MethodCtDecl methodType) {
            base.setCtDecl(methodType);
        }

        @Override
        public @Nullable MethodCtDecl getCtDecl() {
            return base.getCtDecl();
        }

        @Override
        public JavaNode getLocation() {
            return base.getLocation();
        }

        @Override
        public @NonNull JClassType getEnclosingType() {
            return base.getEnclosingType();
        }

        @Override
        public boolean isAnonymous() {
            return base.isAnonymous();
        }

        @Override
        public Iterable<JMethodSig> getAccessibleCandidates(JTypeMirror newType) {
            return base.getAccessibleCandidates(newType);
        }

        @Override
        public @Nullable JTypeMirror getReceiverType() {
            return base.getReceiverType();
        }

        @Override
        public String getName() {
            return base.getName();
        }

        @Override
        public List<ExprMirror> getArgumentExpressions() {
            return base.getArgumentExpressions();
        }

        @Override
        public int getArgumentCount() {
            return base.getArgumentCount();
        }

        @Override
        public String toString() {
            return base.toString();
        }

        @Override
        public TypingContext getTypingContext() {
            return base.getTypingContext();
        }

        @Override
        public boolean isEquivalentToUnderlyingAst() {
            return base.isEquivalentToUnderlyingAst();
        }
    }

}

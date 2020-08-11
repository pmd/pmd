/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.mentionsAnyTvar;

import java.lang.reflect.Modifier;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadComparator;

class MethodInvocMirror extends BaseInvocMirror<ASTMethodCall> implements InvocationMirror {


    MethodInvocMirror(JavaExprMirrors mirrors, ASTMethodCall call) {
        super(mirrors, call);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        if (myNode.getExplicitTypeArguments() == null) {
            return null;
        }

        MethodCallSite site = factory.infer.newCallSite(this, null);

        JMethodSig ctdecl = factory.infer.getCompileTimeDecl(site) // this is cached for later anyway
                                         .getMethodType();

        if (!ctdecl.getTypeParameters().isEmpty()
            && mentionsAnyTvar(ctdecl.getReturnType(), ctdecl.getTypeParameters())) {
            return null;
        }

        return ctdecl.getReturnType();
    }

    @Override
    public List<JMethodSig> getAccessibleCandidates() {
        TypeNode lhs = myNode.getQualifier();
        if (lhs == null) {
            // already filters accessibility
            return myNode.getSymbolTable().methods().resolve(getName());
        } else {
            JTypeMirror lhsType = TypeConversion.capture(lhs.getTypeMirror());
            boolean staticOnly = lhs instanceof ASTTypeExpression;

            return getMethodsOf(lhsType, getName(), staticOnly);
        }
    }

    private List<JMethodSig> getMethodsOf(JTypeMirror type, String name, boolean staticOnly) {
        JClassSymbol enclosingType = myNode.getEnclosingType().getSymbol();
        return type.streamMethods(
            it -> (!staticOnly || Modifier.isStatic(it.getModifiers()))
                && it.getSimpleName().equals(name)
                && it.isAccessible(enclosingType)
        ).collect(OverloadComparator.collectMostSpecific(type));
    }

    @Override
    public JTypeMirror getErasedReceiverType() {
        return getReceiverType().getErasure();
    }

    @Override
    public @NonNull JTypeMirror getReceiverType() {
        ASTExpression qualifier = myNode.getQualifier();
        if (qualifier != null) {
            return qualifier.getTypeMirror();
        } else {
            return myNode.getEnclosingType().getTypeMirror();
        }
    }

    @Override
    public String getName() {
        return myNode.getMethodName();
    }


}

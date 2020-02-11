/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.mentionsAnyTvar;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;

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
    public List<JMethodSig> getVisibleCandidates() {
        TypeNode lhs = myNode.getQualifier();
        if (lhs == null) {
            // already filters accessibility
            return myNode.getSymbolTable().methods().resolve(getName());
        } else {
            JTypeMirror lhsType = TypeConversion.capture(lhs.getTypeMirror());
            List<JMethodSig> result = new ArrayList<>();
            boolean staticOnly = lhs instanceof ASTTypeExpression;

            getMethodsOf(lhsType, getName(), staticOnly, result);

            if (lhsType.isInterface()) {
                // then it's missing the methods from Object
                getMethodsOf(myNode.getTypeSystem().OBJECT, getName(), staticOnly, result);
            }
            return result;
        }
    }

    @Override
    public JTypeMirror getErasedReceiverType() {
        ASTExpression qualifier = myNode.getQualifier();
        if (qualifier != null) {
            return qualifier.getTypeMirror().getErasure();
        } else {
            return myNode.getEnclosingType().getTypeMirror().getErasure();
        }
    }

    private void getMethodsOf(JTypeMirror type, String name, boolean staticOnly, List<JMethodSig> list) {
        type.streamMethods(it -> it.getSimpleName().equals(name) && (!staticOnly || Modifier.isStatic(it.getModifiers())))
            .collect(Collectors.toCollection(() -> list));
    }

    @Override
    public String getName() {
        return myNode.getMethodName();
    }


}

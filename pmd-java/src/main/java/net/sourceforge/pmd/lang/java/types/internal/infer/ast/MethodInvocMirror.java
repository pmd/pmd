/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;

class MethodInvocMirror extends BaseInvocMirror<ASTMethodCall> implements InvocationMirror {


    MethodInvocMirror(JavaExprMirrors mirrors, ASTMethodCall call) {
        super(mirrors, call);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        JMethodSig ctdecl = getCtdecl();
        return TypeOps.isContextDependent(ctdecl) ? null : ctdecl.getReturnType();
    }

    private JMethodSig getCtdecl() {
        MethodCallSite site = factory.infer.newCallSite(this, null);
        // this is cached for later anyway
        return factory.infer.getCompileTimeDecl(site).getMethodType();
    }

    @Override
    public TypeSpecies getStandaloneSpecies() {
        return TypeSpecies.getSpecies(getCtdecl().getReturnType());
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

            return TypeOps.getMethodsOf(lhsType, getName(), staticOnly, myNode.getEnclosingType().getSymbol());
        }
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

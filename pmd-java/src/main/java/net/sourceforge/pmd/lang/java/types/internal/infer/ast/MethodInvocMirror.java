/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;

class MethodInvocMirror extends BaseInvocMirror<ASTMethodCall> implements InvocationMirror {


    MethodInvocMirror(JavaExprMirrors mirrors, ASTMethodCall call, @Nullable ExprMirror parent) {
        super(mirrors, call, parent);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        JMethodSig ctdecl = getStandaloneCtdecl().getMethodType();
        return isContextDependent(ctdecl) ? null : ctdecl.getReturnType();
    }

    private static boolean isContextDependent(JMethodSig m) {
        m = m.internalApi().adaptedMethod();
        return m.isGeneric() && TypeOps.mentionsAny(m.getReturnType(), m.getTypeParameters());
    }

    @Override
    public @NonNull TypeSpecies getStandaloneSpecies() {
        return TypeSpecies.getSpecies(getStandaloneCtdecl().getMethodType().getReturnType());
    }

    @Override
    public List<JMethodSig> getAccessibleCandidates() {
        ASTExpression lhs = myNode.getQualifier();
        if (lhs == null) {
            // already filters accessibility
            return myNode.getSymbolTable().methods().resolve(getName());
        } else {
            JTypeMirror lhsType;
            if (lhs instanceof ASTConstructorCall) {
                ASTConstructorCall ctor = (ASTConstructorCall) lhs;
                ASTAnonymousClassDeclaration anon = ctor.getAnonymousClassDeclaration();
                // put methods declared in the anonymous class in scope
                lhsType = anon != null ? anon.getTypeMirror(getTypingContext())
                                       : ctor.getTypeMirror(getTypingContext()); // may resolve diamonds
            } else {
                lhsType = lhs.getTypeMirror(getTypingContext());
            }
            lhsType = TypeConversion.capture(lhsType);
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
            return qualifier.getTypeMirror(getTypingContext());
        } else {
            return myNode.getEnclosingType().getTypeMirror();
        }
    }

    @Override
    public String getName() {
        return myNode.getMethodName();
    }


}

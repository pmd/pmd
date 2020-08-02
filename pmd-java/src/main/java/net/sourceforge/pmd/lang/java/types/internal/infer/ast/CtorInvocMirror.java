/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;

class CtorInvocMirror extends BaseInvocMirror<ASTConstructorCall> implements CtorInvocationMirror {


    CtorInvocMirror(JavaExprMirrors mirrors, ASTConstructorCall call) {
        super(mirrors, call);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        if (myNode.isDiamond()) {
            return null;
        }
        return myNode.getTypeNode().getTypeMirror();
    }

    @Override
    public @NonNull JClassType getEnclosingType() {
        if (myNode.isAnonymousClass()) {
            // protected constructors are visible when building
            // an anonymous class instance
            return myNode.getAnonymousClassDeclaration().getTypeMirror();
        }

        return super.getEnclosingType();
    }

    @Override
    public List<JMethodSig> getVisibleCandidates() {
        JClassType newType = getNewType();
        if (newType == null) {
            return Collections.emptyList();
        } else if (myNode.isAnonymousClass()) {
            return newType.isInterface() ? myNode.getTypeSystem().OBJECT.getConstructors()
                                         : newType.getConstructors();
        }
        return newType.getConstructors();
    }

    @Override
    public JClassType getNewType() {
        return (JClassType) myNode.getTypeNode().getTypeMirror();
    }

    @Override
    public boolean isDiamond() {
        return myNode.isDiamond();
    }

    @Override
    public boolean isAnonymous() {
        return myNode.isAnonymousClass();
    }

    static class EnumCtorInvocMirror extends BaseInvocMirror<ASTEnumConstant> implements CtorInvocationMirror {


        EnumCtorInvocMirror(JavaExprMirrors mirrors, ASTEnumConstant call) {
            super(mirrors, call);
        }

        @Override
        public List<JMethodSig> getVisibleCandidates() {
            return getNewType().getConstructors();
        }

        @Override
        public JClassType getNewType() {
            return getEnclosingType();
        }

        @Override
        public boolean isAnonymous() {
            return myNode.isAnonymousClass();
        }

        @Override
        public boolean isDiamond() {
            return false;
        }

    }

    static class ExplicitCtorInvocMirror extends BaseInvocMirror<ASTExplicitConstructorInvocation> implements CtorInvocationMirror {


        ExplicitCtorInvocMirror(JavaExprMirrors mirrors, ASTExplicitConstructorInvocation call) {
            super(mirrors, call);
        }

        @Override
        public List<JMethodSig> getVisibleCandidates() {
            return getNewType().getConstructors();
        }

        @Override
        public JClassType getNewType() {
            JClassType encl = getEnclosingType();
            return myNode.isThis()
                   ? encl
                   // this may cause NPE, but only if a super ctor call occurs in class Object
                   : encl.getSuperClass().getGenericTypeDeclaration();
        }

        @Override
        public boolean isAnonymous() {
            return false;
        }

        @Override
        public boolean isDiamond() {
            return false;
        }

    }
}

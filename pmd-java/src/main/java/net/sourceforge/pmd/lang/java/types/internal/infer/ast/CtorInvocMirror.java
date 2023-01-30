/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.lazyFilterAccessible;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaResolvers;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors.MirrorMaker;
import net.sourceforge.pmd.util.IteratorUtil;

class CtorInvocMirror extends BaseInvocMirror<ASTConstructorCall> implements CtorInvocationMirror {

    CtorInvocMirror(JavaExprMirrors mirrors, ASTConstructorCall call, ExprMirror parent, MirrorMaker subexprMaker) {
        super(mirrors, call, parent, subexprMaker);
    }

    @Override
    public @NonNull JClassType getEnclosingType() {
        if (myNode.isAnonymousClass()) {
            // protected constructors are visible when building
            // an anonymous class instance todo is that tested?
            return myNode.getAnonymousClassDeclaration().getTypeMirror();
        }

        return super.getEnclosingType();
    }

    @Override
    public JTypeMirror getStandaloneType() {
        if (isDiamond()) {
            return null;
        }
        return getNewType();
    }

    @Override
    public void finishStandaloneInference(@NonNull JTypeMirror standaloneType) {
        if (mayMutateAst()) {
            setCtDecl(getStandaloneCtdecl());
        }
    }

    @Override
    public @NonNull TypeSpecies getStandaloneSpecies() {
        return TypeSpecies.REFERENCE;
    }

    @Override
    public @Nullable JTypeMirror unresolvedType() {
        JTypeMirror newT = getNewType();
        if (myNode.usesDiamondTypeArgs()) {
            if (myNode.getParent() instanceof ASTVariableDeclarator) {
                // Foo<String> s = new Foo<>();
                ASTType explicitType = ((ASTVariableDeclarator) myNode.getParent()).getVarId().getTypeNode();
                if (explicitType != null) {
                    return explicitType.getTypeMirror();
                }
            }
            if (newT instanceof JClassType) {
                JClassType classt = (JClassType) newT;
                // eg new Foo<>() -> Foo</*error*/>
                List<JTypeMirror> fakeTypeArgs = Collections.nCopies(classt.getSymbol().getTypeParameterCount(), factory.ts.ERROR);
                newT = classt.withTypeArguments(fakeTypeArgs);
            }
        }
        return newT;
    }


    private List<JMethodSig> getVisibleCandidates(@NonNull JTypeMirror newType) {
        if (myNode.isAnonymousClass()) {
            return newType.isInterface() ? myNode.getTypeSystem().OBJECT.getConstructors()
                                         : newType.getConstructors();
        }
        return newType.getConstructors();
    }

    @Override
    public Iterable<JMethodSig> getAccessibleCandidates(JTypeMirror newType) {
        List<JMethodSig> visibleCandidates = getVisibleCandidates(newType);
        return lazyFilterAccessible(visibleCandidates, getEnclosingType().getSymbol());
    }

    @Override
    public @NonNull JTypeMirror getNewType() {
        JTypeMirror typeMirror = myNode.getTypeNode().getTypeMirror();
        if (typeMirror instanceof JClassType) {
            JClassType classTypeMirror = (JClassType) typeMirror;
            if (isDiamond()) {
                classTypeMirror = classTypeMirror.getGenericTypeDeclaration();
            }
            return classTypeMirror;
        } else {
            // this might happen if the type is not known (e.g. ts.UNKNOWN)
            // or invalid (eg new T()), where T is a type variable
            return typeMirror;
        }
    }

    @Override
    public boolean isDiamond() {
        return myNode.usesDiamondTypeArgs();
    }

    @Override
    public boolean isAnonymous() {
        return myNode.isAnonymousClass();
    }

    static class EnumCtorInvocMirror extends BaseInvocMirror<ASTEnumConstant> implements CtorInvocationMirror {


        EnumCtorInvocMirror(JavaExprMirrors mirrors, ASTEnumConstant call, ExprMirror parent, MirrorMaker subexprMaker) {
            super(mirrors, call, parent, subexprMaker);
        }

        @Override
        public Iterable<JMethodSig> getAccessibleCandidates(JTypeMirror newType) {
            return newType.getConstructors();
        }

        @Override
        public @NonNull JClassType getNewType() {
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

        @Override
        public @Nullable JTypeMirror unresolvedType() {
            return getNewType();
        }
    }

    static class ExplicitCtorInvocMirror extends BaseInvocMirror<ASTExplicitConstructorInvocation> implements CtorInvocationMirror {


        ExplicitCtorInvocMirror(JavaExprMirrors mirrors, ASTExplicitConstructorInvocation call, ExprMirror parent, MirrorMaker subexprMaker) {
            super(mirrors, call, parent, subexprMaker);
        }

        @Override
        public Iterable<JMethodSig> getAccessibleCandidates(JTypeMirror newType) {
            if (myNode.isThis()) {
                return getEnclosingType().getConstructors();
            }
            return IteratorUtil.mapIterator(
                newType.getConstructors(),
                iter -> IteratorUtil.filter(iter, ctor -> JavaResolvers.isAccessibleIn(getEnclosingType().getSymbol().getNestRoot(), ctor.getSymbol(), true))
            );
        }

        @Override
        public @NonNull JClassType getNewType() {
            // note that actually, for a qualified super ctor call,
            // the new type should be reparameterized using the LHS.
            // In valid code though, both are equivalent, todo unless the superclass is raw
            // eg new Outer<String>().super()
            JClassType encl = getEnclosingType();
            return myNode.isThis() ? encl : encl.getSuperClass();
        }

        @Override
        public @Nullable JTypeMirror unresolvedType() {
            if (myNode.isThis()) {
                return myNode.getEnclosingType().getTypeMirror();
            } else {
                return myNode.getEnclosingType().getTypeMirror().getSuperClass();
            }
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

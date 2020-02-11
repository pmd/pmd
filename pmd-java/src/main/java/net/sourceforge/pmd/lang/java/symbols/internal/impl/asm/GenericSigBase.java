/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;


import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.LexicalScope;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.util.CollectionUtil;

abstract class GenericSigBase<T extends JTypeParameterOwnerSymbol & AsmStub> {
    /*
       Signatures must be parsed lazily, because at the point we see them
       in the file, the enclosing class might not yet have been encountered
       (and since its type parameters are in scope in the signature we must
        wait for it).
     */


    protected final T ctx;
    protected List<JTypeVar> typeParameters;
    private final ParseLock lock;

    protected GenericSigBase(T ctx) {
        this.ctx = ctx;
        this.lock = new ParseLock() {
            @Override
            protected boolean doParse() {
                GenericSigBase.this.doParse();
                return true;
            }

            @Override
            protected boolean postCondition() {
                return GenericSigBase.this.postCondition();
            }

            @Override
            protected boolean canReenter() {
                return typeParameters != null;
            }
        };
    }

    LexicalScope getEnclosingTypeParams() {
        JTypeParameterOwnerSymbol enclosing = ctx.getEnclosingTypeParameterOwner();
        return enclosing == null ? LexicalScope.EMPTY : enclosing.getLexicalScope();
    }

    protected void ensureParsed() {
        lock.ensureParsed();
    }

    protected abstract void doParse();


    protected abstract boolean postCondition();

    public void setTypeParams(List<JTypeVar> tvars) {
        assert this.typeParameters == null : "Type params were already parsed for " + this;
        this.typeParameters = tvars;
    }

    public List<JTypeVar> getTypeParams() {
        ensureParsed();
        return typeParameters;
    }

    public SignatureParser typeLoader() {
        return ctx.sigParser();
    }


    static class LazyClassSignature extends GenericSigBase<ClassStub> {

        private static final String OBJECT_SIG = "Ljava/lang/Object;";
        private static final String OBJECT_BOUND = ":" + OBJECT_SIG;

        private final @Nullable String signature;

        private @Nullable JClassType superType;
        private List<JClassType> superItfs;

        private final List<JClassSymbol> rawItfs;
        private final @Nullable JClassSymbol rawSuper;

        LazyClassSignature(ClassStub ctx,
                           @Nullable String signature, // null if doesn't use generics in header
                           @Nullable String superName, // null if this is the Object class
                           String[] interfaces) {
            super(ctx);
            this.signature = signature;

            this.rawItfs = CollectionUtil.map(interfaces, ctx.getResolver()::resolveFromInternalNameCannotFail);
            this.rawSuper = ctx.getResolver().resolveFromInternalNameCannotFail(superName);
        }

        static LazyClassSignature defaultWhenUnresolved(ClassStub ctx, int observedArity) {
            String sig;
            if (observedArity > 0) {
                StringBuilder sigBuilder = new StringBuilder("<");
                for (int i = 0; i < observedArity; i++) {
                    sigBuilder.append('T').append(i).append(OBJECT_BOUND);
                }
                sigBuilder.append(">").append(OBJECT_SIG);
                sig = sigBuilder.toString();
            } else {
                sig = OBJECT_SIG;
            }

            return new LazyClassSignature(ctx, sig, OBJECT_SIG, null);
        }

        @Override
        protected void doParse() {
            if (signature == null) {
                this.superType = rawSuper == null ? null // the Object class
                                                  : (JClassType) ctx.getTypeSystem().rawType(rawSuper);
                this.superItfs = CollectionUtil.map(rawItfs, klass -> (JClassType) ctx.getTypeSystem().rawType(klass));
                setTypeParams(Collections.emptyList());
            } else {
                ctx.sigParser().parseClassSignature(this, signature);
            }
        }

        @Override
        protected boolean postCondition() {
            return (superItfs != null && superType != null || signature == null) && typeParameters != null;
        }

        void setSuperInterfaces(List<JClassType> supers) {
            Validate.validState(superItfs == null);
            superItfs = supers;
        }

        void setSuperClass(JClassType sup) {
            Validate.validState(this.superType == null);
            this.superType = sup;
        }

        public JClassType getSuperType(Substitution subst) {
            ensureParsed();
            return superType == null ? null : superType.subst(subst);
        }

        public List<JClassType> getSuperItfs(Substitution subst) {
            ensureParsed();
            return (List<JClassType>) (List) TypeOps.subst(superItfs, subst);
        }

        public JClassSymbol getRawSuper() {
            return rawSuper;
        }

        public List<JClassSymbol> getRawItfs() {
            return rawItfs;
        }

        @Override
        public String toString() {
            return signature;
        }
    }

    /**
     * Method or constructor type.
     */
    static class LazyMethodType extends GenericSigBase<ExecutableStub> {

        private final @NonNull String signature;

        private List<JTypeMirror> parameterTypes;
        private List<JTypeMirror> exceptionTypes;
        private JTypeMirror returnType;

        LazyMethodType(ExecutableStub ctx, @Nullable String genericSig, @NonNull String descriptor, @Nullable String[] exceptions) {
            super(ctx);
            this.signature = genericSig != null ? genericSig : descriptor;
        }

        @Override
        protected void doParse() {
            ctx.sigParser().parseMethodType(this, signature);
        }

        @Override
        protected boolean postCondition() {
            return parameterTypes != null && exceptionTypes != null && returnType != null;
        }

        void setParameterTypes(List<JTypeMirror> params) {
            Validate.validState(parameterTypes == null);
            parameterTypes = params;
        }

        void setExceptionTypes(List<JTypeMirror> exs) {
            Validate.validState(exceptionTypes == null);
            exceptionTypes = exs;
        }

        void setReturnType(JTypeMirror returnType) {
            Validate.validState(this.returnType == null);
            this.returnType = returnType;
        }

        public List<JTypeMirror> getParameterTypes() {
            ensureParsed();
            return parameterTypes;
        }

        public List<JTypeMirror> getExceptionTypes() {
            ensureParsed();
            return exceptionTypes;
        }

        public JTypeMirror getReturnType() {
            ensureParsed();
            return returnType;
        }

        @Override
        public String toString() {
            return signature;
        }
    }
}

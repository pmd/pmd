/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.CtorStub;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeAnnotationHelper.TypeAnnotationSet;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeAnnotationHelper.TypeAnnotationSetWithReferences;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.LexicalScope;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

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

    protected GenericSigBase(T ctx, String parseLockName) {
        this.ctx = ctx;
        this.lock = new ParseLock(parseLockName) {
            @Override
            protected boolean doParse() {
                try {
                    GenericSigBase.this.doParse();
                    return true;
                } catch (RuntimeException e) {
                    throw AssertionUtil.contexted(e)
                            .addContextValue("signature", GenericSigBase.this)
                            // Here we don't use the toString of the ctx directly because
                            // it could be using the signature indirectly, which would fail
                            .addContextValue("owner class", ctx.getEnclosingClass())
                            .addContextValue("owner name", ctx.getSimpleName())
                            .addContextValue("owner package", ctx.getPackageName());
                }
            }

            @Override
            protected boolean postCondition() {
                return GenericSigBase.this.postCondition();
            }

            @Override
            protected void finishParse(boolean failed) {
                // Mark the bound as ready to parse, after any type annotations have been collected
                typeParameters.forEach(tp -> ((TParamStub) tp.getSymbol()).setCanComputeBound());
            }
        };
    }

    LexicalScope getEnclosingTypeParams() {
        JTypeParameterOwnerSymbol enclosing = ctx.getEnclosingTypeParameterOwner();
        return enclosing == null ? LexicalScope.EMPTY : enclosing.getLexicalScope();
    }

    protected final void ensureParsed() {
        lock.ensureParsed();
    }

    protected abstract void doParse();

    protected abstract boolean postCondition();

    protected abstract int getTypeParameterCount();

    protected boolean isGeneric() {
        return getTypeParameterCount() > 0;
    }

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

        private static final String OBJECT_INTERNAL_NAME = "java/lang/Object";
        private static final String OBJECT_SIG = "L" + OBJECT_INTERNAL_NAME + ";";
        private static final String OBJECT_BOUND = ":" + OBJECT_SIG;

        private final @Nullable String signature;
        private final int typeParameterCount;

        private @Nullable JClassType superType;
        private List<JClassType> superItfs;

        private final List<JClassSymbol> rawItfs;
        private final @Nullable JClassSymbol rawSuper;

        LazyClassSignature(
                ClassStub ctx,
                @Nullable String signature, // null if doesn't use generics in header
                @Nullable String superInternalName, // null if this is the Object class
                String[] interfaces) {
            super(ctx, "LazyClassSignature:" + ctx.getInternalName() + "[" + signature + "]");
            this.signature = signature;
            this.typeParameterCount = GenericTypeParameterCounter.determineTypeParameterCount(this.signature);

            this.rawItfs = CollectionUtil.map(interfaces, ctx.getResolver()::resolveFromInternalNameCannotFail);
            this.rawSuper = ctx.getResolver().resolveFromInternalNameCannotFail(superInternalName);
        }

        static LazyClassSignature defaultWhenUnresolved(ClassStub ctx, int observedArity) {
            String sig = sigWithNTypeParams(observedArity);

            return new LazyClassSignature(ctx, sig, OBJECT_INTERNAL_NAME, null);
        }

        private static @NonNull String sigWithNTypeParams(int observedArity) {
            assert observedArity >= 0;

            // use constants for common values
            switch (observedArity) {
                case 0:
                    return OBJECT_SIG;
                case 1:
                    return "<T0" + OBJECT_BOUND + ">" + OBJECT_SIG;
                case 2:
                    return "<T0" + OBJECT_BOUND + "T1" + OBJECT_BOUND + ">" + OBJECT_SIG;
                default:
                    return Stream.iterate(0, i -> i + 1)
                            .limit(observedArity)
                            .map(i -> "T" + i + OBJECT_BOUND)
                            .collect(Collectors.joining("", "<", ">" + OBJECT_SIG));
            }
        }

        @Override
        protected void doParse() {
            if (signature == null) {
                this.superType = rawSuper == null
                        ? null // the Object class
                        : (JClassType) ctx.getTypeSystem().rawType(rawSuper);
                this.superItfs = CollectionUtil.map(
                        rawItfs, klass -> (JClassType) ctx.getTypeSystem().rawType(klass));
                setTypeParams(Collections.emptyList());
            } else {
                ctx.sigParser().parseClassSignature(this, signature);
            }
        }

        @Override
        protected int getTypeParameterCount() {
            // note: no ensureParsed() needed, the type parameters are counted eagerly
            return typeParameterCount;
        }

        @Override
        protected boolean postCondition() {
            return superItfs != null && (superType != null || signature == null) && typeParameters != null;
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
            return TypeOps.substClasses(superItfs, subst);
        }

        public @Nullable JClassSymbol getRawSuper() {
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
    static class LazyMethodType extends GenericSigBase<ExecutableStub> implements TypeAnnotationReceiver {

        private final @NonNull String signature;
        private final int typeParameterCount;

        private @Nullable TypeAnnotationSet receiverAnnotations;
        private List<JTypeMirror> parameterTypes;
        private List<JTypeMirror> exceptionTypes;
        private JTypeMirror returnType;
        private @Nullable TypeAnnotationSetWithReferences typeAnnots;
        private @Nullable String[] rawExceptions;

        /** Used for constructors of inner non-static classes. */
        private final boolean skipFirstParam;

        // TODO exceptions. Couple of notes:
        //  - the descriptor never contains thrown exceptions
        //  - the signature might not contain the thrown exception types (if they do not depend on type variables)
        //  - the exceptions array also contains unchecked exceptions
        //
        //  See https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.9.1
        // TODO test cases
        //  <E extends Exception> void foo()    throws E;          // descriptor "()V"                     signature
        // "<TE;>()V^TE;"   exceptions: ???
        //  <E>                   void foo(E e) throws Exception;  // descriptor "(Ljava.lang.Object;)V"   signature
        // "<TE;>(TE;)V"    exceptions: [ "java/lang/Exception" ]
        //                        void foo()    throws Exception;  // descriptor "()V"                     signature
        // null             exceptions: [ "java/lang/Exception" ]
        LazyMethodType(
                ExecutableStub ctx,
                @NonNull String descriptor,
                @Nullable String genericSig,
                @Nullable String[] exceptions,
                boolean skipFirstParam) {
            super(ctx, "LazyMethodType:" + (genericSig != null ? genericSig : descriptor));
            this.signature = genericSig != null ? genericSig : descriptor;
            this.typeParameterCount = GenericTypeParameterCounter.determineTypeParameterCount(genericSig);
            // generic signatures already omit the synthetic param
            this.skipFirstParam = skipFirstParam && genericSig == null;
            this.rawExceptions = exceptions;
        }

        @Override
        protected void doParse() {
            ctx.sigParser().parseMethodType(this, signature);
            if (rawExceptions != null && this.exceptionTypes.isEmpty()) {
                // the descriptor did not contain exceptions. They're in this string array.
                this.exceptionTypes = Arrays.stream(rawExceptions)
                        .map(ctx.getResolver()::resolveFromInternalNameCannotFail)
                        .map(ctx.getTypeSystem()::rawType)
                        .collect(CollectionUtil.toUnmodifiableList());
            }
            if (ctx instanceof CtorStub) {
                // Is a constructor, return type of the descriptor is void.
                // We replace the return type with the owner type. This must
                // be done before type annotations are applied.
                assert this.returnType.isVoid();
                this.returnType = ctx.getTypeSystem().declaration(ctx.getEnclosingClass());
            }
            if (typeAnnots != null) {
                // apply type annotations here
                // this may change type parameters
                typeAnnots.forEach(this::acceptAnnotationAfterParse);
            }
            // null this transient data out
            this.rawExceptions = null;
            this.typeAnnots = null;
        }

        public JTypeMirror applyReceiverAnnotations(JTypeMirror typeMirror) {
            if (receiverAnnotations == null) {
                return typeMirror;
            }
            return receiverAnnotations.decorate(typeMirror);
        }

        @Override
        protected boolean postCondition() {
            return parameterTypes != null && exceptionTypes != null && returnType != null;
        }

        @Override
        protected int getTypeParameterCount() {
            // note: no ensureParsed() needed, the type parameters are counted eagerly
            return typeParameterCount;
        }

        void setParameterTypes(List<JTypeMirror> params) {
            Validate.validState(parameterTypes == null);
            parameterTypes = skipFirstParam ? params.subList(1, params.size()) : params;
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

        @Override
        public void acceptTypeAnnotation(int typeRefInt, @Nullable TypePath path, SymAnnot annot) {
            // Accumulate type annotations for later
            // They shouldn't be applied right now because the descriptor maybe has not been parsed yet.
            if (typeAnnots == null) {
                typeAnnots = new TypeAnnotationSetWithReferences();
            }
            typeAnnots.add(new TypeReference(typeRefInt), path, annot);
        }

        /**
         * See {@link MethodVisitor#visitTypeAnnotation(int, TypePath, String, boolean)} for possible
         * values of typeRef sort (they're each case of the switch).
         * Returns true if type parameters have been mutated.
         */
        boolean acceptAnnotationAfterParse(TypeReference tyRef, @Nullable TypePath path, SymAnnot annot) {
            switch (tyRef.getSort()) {
                case TypeReference.METHOD_RETURN: {
                    assert returnType != null : "Return type is not set";
                    returnType = TypeAnnotationHelper.applySinglePath(returnType, path, annot);
                    return false;
                }
                case TypeReference.METHOD_FORMAL_PARAMETER: {
                    assert parameterTypes != null : "Parameter types are not set";
                    int idx = tyRef.getFormalParameterIndex();
                    JTypeMirror annotatedFormal =
                            TypeAnnotationHelper.applySinglePath(parameterTypes.get(idx), path, annot);
                    parameterTypes = TypeAnnotationHelper.replaceAtIndex(parameterTypes, idx, annotatedFormal);
                    return false;
                }
                case TypeReference.THROWS: {
                    assert exceptionTypes != null : "Exception types are not set";
                    int idx = tyRef.getExceptionIndex();
                    JTypeMirror annotatedFormal =
                            TypeAnnotationHelper.applySinglePath(exceptionTypes.get(idx), path, annot);
                    exceptionTypes = TypeAnnotationHelper.replaceAtIndex(exceptionTypes, idx, annotatedFormal);
                    return false;
                }
                case TypeReference.METHOD_TYPE_PARAMETER: {
                    assert typeParameters != null;
                    assert path == null : "unexpected path " + path;
                    int idx = tyRef.getTypeParameterIndex();
                    // Here we add to the symbol, not the type var.
                    // This ensures that all occurrences of the type var
                    // share these annotations (symbol is unique, contrary to jtypevar)
                    ((TParamStub) typeParameters.get(idx).getSymbol()).addAnnotation(annot);
                    return false;
                }
                case TypeReference.METHOD_TYPE_PARAMETER_BOUND: {
                    assert typeParameters != null;
                    int tparamIdx = tyRef.getTypeParameterIndex();

                    JTypeVar tparam = typeParameters.get(tparamIdx);
                    TParamStub sym = (TParamStub) tparam.getSymbol();
                    assert sym != null;
                    sym.addAnnotationOnBound(tyRef, path, annot);
                    return false;
                }
                case TypeReference.METHOD_RECEIVER: {
                    if (receiverAnnotations == null) {
                        receiverAnnotations = new TypeAnnotationSet();
                    }
                    receiverAnnotations.add(path, annot);
                    return false;
                }
                default:
                    throw new IllegalArgumentException(
                            "Invalid type reference for method or ctor type annotation: " + tyRef.getSort());
            }
        }
    }
}

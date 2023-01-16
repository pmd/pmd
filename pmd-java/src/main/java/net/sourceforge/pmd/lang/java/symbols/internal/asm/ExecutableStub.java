/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.pcollections.HashTreePSet;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.GenericSigBase.LazyMethodType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

abstract class ExecutableStub extends MemberStubBase implements JExecutableSymbol, TypeAnnotationReceiver {

    private final String descriptor;
    protected final LazyMethodType type;
    private List<JFormalParamSymbol> params;
    private PMap<Integer, PSet<SymAnnot>> parameterAnnotations = IntTreePMap.empty();

    protected ExecutableStub(ClassStub owner,
                             String simpleName,
                             int accessFlags,
                             String descriptor,
                             @Nullable String signature,
                             @Nullable String[] exceptions,
                             boolean skipFirstParam) {
        super(owner, simpleName, accessFlags);
        this.descriptor = descriptor;
        this.type = new LazyMethodType(this, descriptor, signature, exceptions, skipFirstParam);
    }

    boolean matches(String name, String descriptor) {
        return this.nameEquals(name) && descriptor.equals(this.descriptor);
    }


    @Override
    public List<JTypeVar> getTypeParameters() {
        return type.getTypeParams();
    }

    @Override
    public List<JFormalParamSymbol> getFormalParameters() {
        if (params == null) {
            List<JTypeMirror> ptypes = type.getParameterTypes();
            List<JFormalParamSymbol> newParams = new ArrayList<>(ptypes.size());
            for (int i = 0; i < ptypes.size(); i++) {
                newParams.add(new FormalParamStub(ptypes.get(i), i));
            }
            params = Collections.unmodifiableList(newParams);
        }
        return params;
    }

    @Override
    public boolean isVarargs() {
        return (getModifiers() & Opcodes.ACC_VARARGS) != 0;
    }

    @Override
    public int getArity() {
        return type.getParameterTypes().size();
    }

    PSet<SymAnnot> getFormalParameterAnnotations(int parameterIndex) {
        return parameterAnnotations.getOrDefault(parameterIndex, HashTreePSet.empty());
    }

    @Override
    public @Nullable JTypeMirror getAnnotatedReceiverType(Substitution subst) {
        if (!this.hasReceiver()) {
            return null;
        }
        JTypeMirror receiver = getTypeSystem().declaration(getEnclosingClass()).subst(subst);
        return type.applyReceiverAnnotations(receiver);
    }

    @Override
    public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
        return TypeOps.subst(type.getParameterTypes(), subst);
    }

    @Override
    public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
        return TypeOps.subst(type.getExceptionTypes(), subst);
    }

    void setDefaultAnnotValue(@Nullable SymbolicValue defaultAnnotValue) {
        // overridden by MethodStub
    }

    @Override
    public void acceptTypeAnnotation(int typeRef, @Nullable TypePath path, SymAnnot annot) {
        type.acceptTypeAnnotation(typeRef, path, annot);
    }

    void addParameterAnnotation(int paramIndex, SymbolicValue.SymAnnot annot) {
        PSet<SymAnnot> newAnnots = parameterAnnotations.getOrDefault(paramIndex, HashTreePSet.empty()).plus(annot);
        parameterAnnotations = parameterAnnotations.plus(paramIndex, newAnnots);
    }

    /**
     * Formal parameter symbols obtained from the class have no info
     * about name or whether it's final. This info is missing from
     * classfiles when they're not compiled with debug symbols.
     */
    class FormalParamStub implements JFormalParamSymbol {

        private final JTypeMirror type;
        private final int index;

        FormalParamStub(JTypeMirror type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public JExecutableSymbol getDeclaringSymbol() {
            return ExecutableStub.this;
        }

        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public JTypeMirror getTypeMirror(Substitution subst) {
            return type.subst(subst);
        }

        @Override
        public String getSimpleName() {
            return "";
        }

        @Override
        public TypeSystem getTypeSystem() {
            return ExecutableStub.this.getTypeSystem();
        }

        @Override
        public PSet<SymAnnot> getDeclaredAnnotations() {
            return ExecutableStub.this.getFormalParameterAnnotations(index);
        }
    }

    /**
     * Formal parameter symbols obtained from the class have no info
     * about name or whether it's final. This is because due to ASM's
     * design, parsing this information would entail parsing a lot of
     * other information we don't care about, and so this would be
     * wasteful. It's unlikely anyone cares about this anyway.
     *
     * <p>If classes are compiled without debug symbols that info
     * is NOT in the classfile anyway.
     */
    static class MethodStub extends ExecutableStub implements JMethodSymbol {

        private @Nullable SymbolicValue defaultAnnotValue;

        protected MethodStub(ClassStub owner,
                             String simpleName,
                             int accessFlags,
                             String descriptor,
                             @Nullable String signature,
                             @Nullable String[] exceptions) {
            super(owner, simpleName, accessFlags, descriptor, signature, exceptions, false);
        }

        @Override
        public boolean isBridge() {
            return (getModifiers() & Opcodes.ACC_BRIDGE) != 0;
        }

        @Override
        public JTypeMirror getReturnType(Substitution subst) {
            return type.getReturnType().subst(subst);
        }


        @Override
        public String toString() {
            return SymbolToStrings.ASM.toString(this);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.METHOD.hash(this);
        }

        @Override
        public boolean equals(Object obj) {
            return SymbolEquality.METHOD.equals(this, obj);
        }

        @Override
        public @Nullable SymbolicValue getDefaultAnnotationValue() {
            return defaultAnnotValue;
        }

        @Override
        void setDefaultAnnotValue(@Nullable SymbolicValue defaultAnnotValue) {
            this.defaultAnnotValue = defaultAnnotValue;
        }
    }

    static class CtorStub extends ExecutableStub implements JConstructorSymbol {

        protected CtorStub(ClassStub owner,
                           int accessFlags,
                           String descriptor,
                           @Nullable String signature,
                           @Nullable String[] exceptions,
                           boolean isInnerNonStaticClass) {
            super(owner, JConstructorSymbol.CTOR_NAME, accessFlags, descriptor, signature, exceptions, isInnerNonStaticClass);
        }

        @Override
        public String toString() {
            return SymbolToStrings.ASM.toString(this);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.CONSTRUCTOR.hash(this);
        }

        @Override
        public boolean equals(Object obj) {
            return SymbolEquality.CONSTRUCTOR.equals(this, obj);
        }

    }
}

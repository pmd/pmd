/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.GenericSigBase.LazyMethodType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

abstract class ExecutableStub extends MemberStubBase implements JExecutableSymbol {

    /**
     * A method may only have 255 parameters by the jvm spec (chap 4).
     * So we can put the flag for a final param inside the index.
     */
    static final int FINAL_PARAM_FLAG = 1 << 9;

    private final String descriptor;
    protected final LazyMethodType type;
    private List<JFormalParamSymbol> params;

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
        return params;
    }

    @Override
    public boolean isVarargs() {
        return (getModifiers() & Opcodes.ACC_VARARGS) != 0;
    }

    @Override
    public int getArity() {
        return getFormalParameters().size();
    }

    @Override
    public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
        return TypeOps.subst(type.getParameterTypes(), subst);
    }

    @Override
    public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
        return TypeOps.subst(type.getExceptionTypes(), subst);
    }

    void setParams(List<JFormalParamSymbol> params) {
        this.params = Collections.unmodifiableList(params); // implicit null check
    }


    /**
     * Formal parameter symbols obtained from the class have no info
     * about name or whether it's final. This is because due to ASM's
     * design, parsing this information would entail parsing a lot of
     * other information we don't care about, and so this would be
     * wasteful. It's unlikely anyone cares about this anyway.
     */
    class FormalParamStub implements JFormalParamSymbol {

        private final int index;
        private final String name;

        FormalParamStub(int index, boolean isFinal, String name) {
            this.name = name;
            this.index = index | (isFinal ? FINAL_PARAM_FLAG : 0);
        }

        private JTypeMirror getType() {
            return ExecutableStub.this.type.getParameterTypes().get(index);
        }

        @Override
        public JExecutableSymbol getDeclaringSymbol() {
            return ExecutableStub.this;
        }

        @Override
        public boolean isFinal() {
            return (index & FINAL_PARAM_FLAG) != 0;
        }

        @Override
        public JTypeMirror getTypeMirror(Substitution subst) {
            return getType().subst(subst);
        }

        @Override
        public String getSimpleName() {
            return name;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return ExecutableStub.this.getTypeSystem();
        }
    }

    static class MethodStub extends ExecutableStub implements JMethodSymbol {

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

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.internal.InternalMethodTypeItf;

final class UnresolvedMethodSig implements JMethodSig, InternalMethodTypeItf {

    private final TypeSystem ts;
    private final JExecutableSymbol sym;

    UnresolvedMethodSig(TypeSystem ts) {
        this.ts = ts;
        sym = new UnresolvedMethodSym(ts);
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JExecutableSymbol getSymbol() {
        return sym;
    }

    @Override
    public JMethodSig getErasure() {
        return this;
    }

    @Override
    public JTypeMirror getDeclaringType() {
        return ts.UNKNOWN;
    }

    @Override
    public JTypeMirror getReturnType() {
        return ts.UNKNOWN;
    }

    @Override
    public JTypeMirror getAnnotatedReceiverType() {
        return ts.UNKNOWN;
    }

    @Override
    public List<JTypeMirror> getFormalParameters() {
        return Collections.emptyList();
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public List<JTypeMirror> getThrownExceptions() {
        return Collections.emptyList();
    }

    @Override
    public JMethodSig withReturnType(JTypeMirror returnType) {
        return this;
    }

    @Override
    public JMethodSig markAsAdapted() {
        return this;
    }

    @Override
    public JMethodSig adaptedMethod() {
        return this;
    }

    @Override
    public JMethodSig withTypeParams(@Nullable List<JTypeVar> tparams) {
        return this;
    }

    @Override
    public JMethodSig subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        return this;
    }

    @Override
    public JMethodSig withOwner(JTypeMirror newOwner) {
        return this;
    }

    @Override
    public JMethodSig originalMethod() {
        return this;
    }

    @Override
    public InternalMethodTypeItf internalApi() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    private static class UnresolvedMethodSym implements JMethodSymbol {

        private final TypeSystem ts;

        UnresolvedMethodSym(TypeSystem ts) {
            this.ts = ts;
        }

        @Override
        public boolean isUnresolved() {
            return true;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return ts;
        }

        @Override
        public boolean isBridge() {
            return false;
        }

        @Override
        public JTypeMirror getReturnType(Substitution subst) {
            return ts.NO_TYPE;
        }

        @Override
        public List<JFormalParamSymbol> getFormalParameters() {
            return Collections.emptyList();
        }

        @Override
        public boolean isVarargs() {
            return false;
        }

        @Override
        public int getArity() {
            return 0;
        }

        @Override
        public @Nullable JTypeMirror getAnnotatedReceiverType(Substitution subst) {
            return ts.UNKNOWN;
        }

        @Override
        public @NonNull JClassSymbol getEnclosingClass() {
            return (JClassSymbol) ts.UNKNOWN.getSymbol();
        }

        @Override
        public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
            return Collections.emptyList();
        }

        @Override
        public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
            return Collections.emptyList();
        }

        @Override
        public List<JTypeVar> getTypeParameters() {
            return Collections.emptyList();
        }

        @Override
        public int getModifiers() {
            return Modifier.PUBLIC;
        }

        @Override
        public String getSimpleName() {
            return "(*unknown method*)";
        }

        @Override
        public String toString() {
            return getSimpleName();
        }
    }
}

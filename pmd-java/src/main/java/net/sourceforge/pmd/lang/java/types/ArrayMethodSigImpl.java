/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.internal.InternalMethodTypeItf;

// for array clone or array constructor
class ArrayMethodSigImpl implements JMethodSig, InternalMethodTypeItf {


    private final JArrayType owner;
    // either method or constructor
    private final JExecutableSymbol symbol;

    ArrayMethodSigImpl(JArrayType owner,
                       @NonNull JExecutableSymbol symbol) {
        this.owner = owner;
        this.symbol = symbol;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return owner.getTypeSystem();
    }

    @Override
    public JExecutableSymbol getSymbol() {
        return symbol;
    }

    @Override
    public JArrayType getDeclaringType() {
        return owner;
    }

    @Override
    public JMethodSig getErasure() {
        JArrayType erasedOwner = owner.getErasure();
        return erasedOwner == owner ? this : new ArrayMethodSigImpl(erasedOwner, symbol); // NOPMD CompareObjectsWithEquals
    }

    @Override
    public JTypeMirror getAnnotatedReceiverType() {
        return owner;
    }

    @Override
    public JTypeMirror getReturnType() {
        return owner; // clone or cons
    }

    @Override
    public List<JTypeMirror> getFormalParameters() {
        if (getSymbol() instanceof JConstructorSymbol) {
            return Collections.singletonList(owner.getTypeSystem().INT);
        }
        return emptyList();
    }


    @Override
    public List<JTypeVar> getTypeParameters() {
        return emptyList();
    }

    @Override
    public List<JTypeMirror> getThrownExceptions() {
        return emptyList();
    }

    @Override
    public InternalMethodTypeItf internalApi() {
        return this;
    }

    @Override
    public JMethodSig withReturnType(JTypeMirror returnType) {
        if (!returnType.equals(owner)) {
            throw new UnsupportedOperationException("Something went wrong");
        }
        return this;
    }

    @Override
    public JMethodSig withTypeParams(@Nullable List<JTypeVar> tparams) {
        if (tparams != null && !tparams.isEmpty()) {
            throw new UnsupportedOperationException("Something went wrong");
        }
        return this;
    }

    @Override
    public JMethodSig subst(Function<? super SubstVar, ? extends JTypeMirror> fun) {
        JArrayType subbed = (JArrayType) TypeOps.subst(owner, fun);
        return new ArrayMethodSigImpl(subbed, getSymbol());
    }

    @Override
    public JMethodSig withOwner(JTypeMirror newOwner) {
        if (newOwner instanceof JArrayType) {
            return new ArrayMethodSigImpl((JArrayType) newOwner, symbol);
        } else {
            throw new IllegalArgumentException(newOwner + " cannot be the owner of " + this);
        }
    }

    @Override
    public JMethodSig markAsAdapted() {
        return this;
    }

    @Override
    public JMethodSig originalMethod() {
        return new ArrayMethodSigImpl(owner, symbol);
    }

    @Override
    public JMethodSig adaptedMethod() {
        return originalMethod();
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JMethodSig)) {
            return false;
        }
        JMethodSig that = (JMethodSig) o;
        return TypeOps.isSameType(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFormalParameters(), getReturnType());
    }
}

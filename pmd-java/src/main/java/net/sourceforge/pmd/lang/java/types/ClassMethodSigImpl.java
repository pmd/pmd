/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.types.Substitution.EMPTY;
import static net.sourceforge.pmd.lang.java.types.Substitution.isEmptySubst;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.internal.InternalMethodTypeItf;

class ClassMethodSigImpl implements JMethodSig, InternalMethodTypeItf {


    private final JClassType owner;
    // either method or constructor
    private final JExecutableSymbol symbol;

    private @Nullable List<JTypeVar> tparams;
    private JTypeMirror resultType;
    private List<JTypeMirror> formals;
    private List<JTypeMirror> thrown;

    // TODO bounds of the type params of a method need to be substituted
    //  with the lexical subst of the owner

    ClassMethodSigImpl(@NonNull JClassType owner, @NonNull JExecutableSymbol symbol) {
        this(owner, symbol, null, null, null, null);
    }


    private ClassMethodSigImpl(@NonNull JClassType owner,
                               @NonNull JExecutableSymbol symbol,
                               @Nullable List<JTypeVar> tparams,
                               @Nullable JTypeMirror resultType,
                               @Nullable List<JTypeMirror> formals,
                               @Nullable List<JTypeMirror> thrown) {
        this.owner = owner;
        this.symbol = symbol;
        this.resultType = resultType;
        this.formals = formals;
        this.thrown = thrown;
        this.tparams = tparams;
    }

    static List<JTypeVar> realTypeParams(JClassType owner, List<JTypeVar> baseTypeParams) {
        assert baseTypeParams != null && owner != null : "Null params in " + owner + ", " + baseTypeParams;
        return TypeOps.substInBoundsOnly(baseTypeParams, owner.getTypeParamSubst());
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
    public JClassType getDeclaringType() {
        return owner;
    }

    @Override
    public JMethodSig getErasure() {
        return new ClassMethodSigImpl(
            owner.getErasure(),
            symbol,
            Collections.emptyList(),
            getReturnType().getErasure(),
            TypeOps.erase(getFormalParameters()),
            TypeOps.erase(getThrownExceptions())
        );
    }

    @Override
    public JTypeMirror getReturnType() {
        if (resultType == null) {
            if (symbol instanceof JMethodSymbol) {
                if (owner.isRaw()) {
                    resultType = ClassTypeImpl.eraseToRaw(((JMethodSymbol) symbol).getReturnType(EMPTY), getTypeParamSubst());
                } else {
                    resultType = ((JMethodSymbol) symbol).getReturnType(getTypeParamSubst());
                }
            } else {
                // constructor
                resultType = owner;
            }
        }
        return resultType;
    }

    @Override
    public List<JTypeMirror> getFormalParameters() {
        if (formals == null) {
            if (owner.isRaw()) {
                formals = map(symbol.getFormalParameterTypes(EMPTY),
                              m -> ClassTypeImpl.eraseToRaw(m, getTypeParamSubst()));
            } else {
                formals = symbol.getFormalParameterTypes(getTypeParamSubst());
            }
        }
        return formals;
    }


    @Override
    public List<JTypeVar> getTypeParameters() {
        if (tparams == null) {
            tparams = realTypeParams(owner, symbol.getTypeParameters());
        }
        return tparams;
    }

    @Override
    public List<JTypeMirror> getThrownExceptions() {
        if (thrown == null) {
            if (owner.isRaw()) {
                thrown = map(symbol.getThrownExceptionTypes(EMPTY),
                             m -> ClassTypeImpl.eraseToRaw(m, getTypeParamSubst()));
            } else {
                thrown = symbol.getThrownExceptionTypes(getTypeParamSubst());
            }
        }
        return thrown;
    }

    @Override
    public boolean isBridge() {
        return symbol instanceof JMethodSymbol && ((JMethodSymbol) symbol).isBridge();
    }

    @Override
    public InternalMethodTypeItf internalApi() {
        return this;
    }

    @Override
    public JMethodSig withReturnType(JTypeMirror returnType) {
        // share formals & thrown to avoid recomputing
        return new ClassMethodSigImpl(owner, symbol, getTypeParameters(), returnType, formals, thrown);
    }

    @Override
    public JMethodSig withTypeParams(List<JTypeVar> tparams) {
        return new ClassMethodSigImpl(owner, symbol, tparams, null, null, null);
    }

    @Override
    public JMethodSig subst(Function<? super SubstVar, ? extends JTypeMirror> fun) {
        if (isEmptySubst(fun)) {
            return this;
        }
        return new ClassMethodSigImpl(
            owner,
            symbol,
            tparams, // don't substitute type parameters
            TypeOps.subst(getReturnType(), fun),
            TypeOps.subst(getFormalParameters(), fun),
            TypeOps.subst(getThrownExceptions(), fun)
        );
    }

    @Override
    public JMethodSig withOwner(JTypeMirror newOwner) {
        // FIXME java.lang.IllegalArgumentException: java.util.function.BinaryOperator<java.util.List<java.lang.Class<?>>> cannot be the owner of java.util.function.BiFunction<java.util.List<java.lang.Class<?>>, java.util.List<java.lang.Class<?>>, java.util.List<java.lang.Class<?>>>.apply(java.util.List<java.lang.Class<?>>, java.util.List<java.lang.Class<?>>) -> java.util.List<java.lang.Class<?>>
        //        private static List<Class<?>> longestParameterList(List<List<Class<?>>> lists) {
        //            final List<Class<?>> empty = List.of();
        //            return lists.stream().reduce((p, q) -> p.size() >= q.size() ? p : q).orElse(empty);
        //        }
        if (newOwner instanceof JClassType && Objects.equals(newOwner.getSymbol(), this.owner.getSymbol())) {
            return new ClassMethodSigImpl((JClassType) newOwner, symbol, tparams, resultType, formals, thrown);
        } else {
            throw new IllegalArgumentException(newOwner + " cannot be the owner of " + this);
        }
    }

    @Override
    public JMethodSig originalMethod() {
        return new ClassMethodSigImpl(owner, symbol);
    }


    private Substitution getTypeParamSubst() {
        return owner.getTypeParamSubst();
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

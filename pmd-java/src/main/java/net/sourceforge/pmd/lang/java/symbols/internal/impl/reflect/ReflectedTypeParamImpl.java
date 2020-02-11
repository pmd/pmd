/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;

class ReflectedTypeParamImpl extends AbstractReflectedSymbol implements JTypeParameterSymbol {

    private final JTypeParameterOwnerSymbol ownerSymbol;
    private final String name;
    private final TypeVariable<?> reflected;
    private final JTypeVar tvar;


    ReflectedTypeParamImpl(AbstractTypeParamOwnerSymbol<?> ownerSymbol, TypeVariable<?> tvar) {
        super(ownerSymbol.factory);
        this.ownerSymbol = ownerSymbol;
        this.name = tvar.getName();
        this.reflected = tvar;
        this.tvar = factory.getTypeSystem().newTypeVar(this);
    }

    @Override
    public JTypeVar getTypeMirror() {
        return tvar;
    }

    @Override
    public JTypeMirror computeUpperBound() {
        Type[] bounds = reflected.getBounds();

        TypeSystem ts = tvar.getTypeSystem();
        if (bounds.length == 0) {
            return ts.OBJECT;
        } else {
            return ts.intersect(TypesFromReflection.fromReflect(ts, getDeclaringSymbol().getLexicalScope(), Substitution.EMPTY, bounds));
        }
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        Type[] bounds = reflected.getBounds();
        if (bounds.length == 0) {
            return Object.class;
        } else {
            return bounds[0] instanceof Class ? (Class<?>) bounds[0] : null;
        }
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return ownerSymbol;
    }

    @NonNull
    @Override
    public String getSimpleName() {
        return name;
    }

}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Generic implementation for array symbols, which does not rely on
 * reflection.
 */
class ArraySymbolImpl implements JClassSymbol {

    private final SymbolFactory factory;
    private final JTypeDeclSymbol component;

    ArraySymbolImpl(SymbolFactory factory, JTypeDeclSymbol component) {
        this.component = Objects.requireNonNull(component, "Array symbol requires component");
        this.factory = Objects.requireNonNull(factory, "Array symbol requires symbol factory");
        if (component instanceof JClassSymbol && ((JClassSymbol) component).isAnonymousClass()) {
            throw new IllegalArgumentException("Anonymous classes cannot be array components: " + component);
        }
    }

    @Override
    public TypeSystem getTypeSystem() {
        return factory.getTypeSystem();
    }

    @Override
    public @NonNull String getBinaryName() {
        if (component instanceof JClassSymbol) {
            return ((JClassSymbol) component).getBinaryName() + "[]";
        }
        return component.getSimpleName() + "[]";
    }

    @Override
    public String getCanonicalName() {
        if (component instanceof JClassSymbol) {
            String compName = ((JClassSymbol) component).getCanonicalName();
            return compName == null ? null : compName + "[]";
        }
        return component.getSimpleName() + "[]";
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        JTypeDeclSymbol elt = this.getArrayComponent();
        int depth = 0;
        while (elt instanceof JClassSymbol && ((JClassSymbol) elt).isArray()) {
            elt = ((JClassSymbol) elt).getArrayComponent();
            depth++;
        }

        Class<?> eltType = elt.getJvmRepr();
        if (eltType == null) {
            return null;
        }

        return Array.newInstance(eltType, (int[]) Array.newInstance(int.class, depth)).getClass();
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return null;
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return Collections.singletonList(ImplicitMemberSymbols.arrayClone(this));
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return Collections.singletonList(ImplicitMemberSymbols.arrayLengthField(this));
    }

    @Override
    public @Nullable JClassSymbol getSuperclass() {
        return getTypeSystem().OBJECT.getSymbol();
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return listOf(getTypeSystem().CLONEABLE.getSymbol(), getTypeSystem().SERIALIZABLE.getSymbol());
    }

    @Override
    public List<JClassType> getSuperInterfaceTypes(Substitution substitution) {
        return listOf(getTypeSystem().CLONEABLE, getTypeSystem().SERIALIZABLE);
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        return getTypeSystem().OBJECT;
    }

    @Override
    public @NonNull JTypeDeclSymbol getArrayComponent() {
        return component;
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitArray(this, component, param);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.hash(this);
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return Collections.emptyList();
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        return Collections.singletonList(ImplicitMemberSymbols.arrayConstructor(this));
    }

    @Override
    @NonNull
    public String getPackageName() {
        return getArrayComponent().getPackageName();
    }

    @Override
    @NonNull
    public String getSimpleName() {
        return getArrayComponent().getSimpleName() + "[]";
    }

    @Override
    public int getModifiers() {
        int comp = getArrayComponent().getModifiers();
        return Modifier.FINAL | Modifier.ABSTRACT | (comp & ~Modifier.STATIC);
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public JClassSymbol getEnclosingClass() {
        return null;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isLocalClass() {
        return false;
    }

    @Override
    public boolean isAnonymousClass() {
        return false;
    }

    @Override
    public String toString() {
        return SymbolToStrings.SHARED.toString(this);
    }

}

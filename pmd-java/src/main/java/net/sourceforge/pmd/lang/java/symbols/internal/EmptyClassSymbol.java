/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

public abstract class EmptyClassSymbol implements JClassSymbol {
    private final Supplier<TypeSystem> typeSystemSupplier;

    public EmptyClassSymbol(Supplier<TypeSystem> typeSystemSupplier) {
        this.typeSystemSupplier = typeSystemSupplier;
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        return null;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return null;
    }

    @Override
    public @Nullable String getCanonicalName() {
        return null;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return Collections.emptyList();
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return Collections.emptyList();
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        return Collections.emptyList();
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return Collections.emptyList();
    }

    @Override
    public List<JClassType> getSuperInterfaceTypes(Substitution substitution) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        return null;
    }

    @Override
    public @Nullable JClassSymbol getSuperclass() {
        return null;
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public @Nullable JTypeDeclSymbol getArrayComponent() {
        return null;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
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
    public boolean isAnnotation() {
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
    public TypeSystem getTypeSystem() {
        return typeSystemSupplier.get();
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return Collections.emptyList();
    }
}

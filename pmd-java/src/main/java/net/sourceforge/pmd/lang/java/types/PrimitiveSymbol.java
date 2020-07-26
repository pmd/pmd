/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;


import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

final class PrimitiveSymbol implements JClassSymbol {
    private final TypeSystem ts;
    private final PrimitiveTypeKind kind;

    PrimitiveSymbol(TypeSystem ts, PrimitiveTypeKind kind) {
        this.ts = ts;
        this.kind = kind;
    }

    @Override
    public @NonNull String getBinaryName() {
        return kind.getSimpleName();
    }

    @Override
    public @Nullable String getCanonicalName() {
        return kind.getSimpleName();
    }

    @Override
    public @NonNull String getSimpleName() {
        return kind.getSimpleName();
    }

    @Override
    public @NonNull String getPackageName() {
        return PRIMITIVE_PACKAGE;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
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
        return true;
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
    public @Nullable Class<?> getJvmRepr() {
        return kind.jvmRepr();
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.FINAL;
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        return null;
    }


    @Override
    public int hashCode() {
        return SymbolEquality.CLASS.hash(this);
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.CLASS.equals(this, obj);
    }

    @Override
    public String toString() {
        return SymbolToStrings.SHARED.toString(this);
    }
}

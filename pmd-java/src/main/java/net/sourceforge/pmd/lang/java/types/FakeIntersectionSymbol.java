/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

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
import net.sourceforge.pmd.util.CollectionUtil;

class FakeIntersectionSymbol implements JClassSymbol {

    private final String name;
    private final JClassType superClass;
    private final List<JClassType> superItfs;

    FakeIntersectionSymbol(String name, @NonNull JClassType superClass, List<JClassType> superItfs) {
        this.name = name;
        this.superClass = Objects.requireNonNull(superClass, "null superclass");
        this.superItfs = superItfs;
    }

    @Override
    public boolean isInterface() {
        return superClass.isTop();
    }

    @Override
    public @NonNull String getBinaryName() {
        return name;
    }

    @Override
    public @Nullable String getCanonicalName() {
        return null;
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
    public @NonNull List<JFieldSymbol> getEnumConstants() {
        return superClass.getSymbol().getEnumConstants();
    }

    @Override
    public List<JClassType> getSuperInterfaceTypes(Substitution substitution) {
        return TypeOps.substClasses(superItfs, substitution);
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        return superClass.subst(substitution);
    }

    @Override
    public @Nullable JClassSymbol getSuperclass() {
        return superClass.getSymbol();
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return CollectionUtil.map(superItfs, JClassType::getSymbol);
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
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isLocalClass() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isAnonymousClass() {
        return false;
    }

    @Override
    public @NonNull String getSimpleName() {
        return "";
    }

    @Override
    public TypeSystem getTypeSystem() {
        return superClass.getTypeSystem();
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
    public @Nullable JClassSymbol getEnclosingClass() {
        return null;
    }

    @Override
    public @NonNull String getPackageName() {
        return "";
    }
}

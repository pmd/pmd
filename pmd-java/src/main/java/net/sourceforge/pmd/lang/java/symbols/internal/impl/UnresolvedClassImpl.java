/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

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
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;

/**
 * Unresolved <i>external reference</i> to a class.
 *
 * @see JClassSymbol#isUnresolved()
 */
class UnresolvedClassImpl implements JClassSymbol {

    private final @Nullable JClassSymbol enclosing;
    private final String canonicalName;

    UnresolvedClassImpl(String canonicalName) {
        this(null, canonicalName);
    }

    UnresolvedClassImpl(@Nullable JClassSymbol enclosing, String canonicalName) {
        this.enclosing = enclosing;
        this.canonicalName = canonicalName;
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }


    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public @NonNull String getBinaryName() {
        return canonicalName;
    }

    @NonNull
    @Override
    public String getSimpleName() {
        int idx = canonicalName.lastIndexOf('.');
        if (idx < 0) {
            return canonicalName;
        } else {
            return canonicalName.substring(idx + 1);
        }
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public @NonNull String getPackageName() {
        int idx = canonicalName.lastIndexOf('.');
        if (idx < 0) {
            return canonicalName;
        } else {
            return canonicalName.substring(0, idx);
        }
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return null;
    }


    @Nullable
    @Override
    public JClassSymbol getSuperclass() {
        return ReflectSymInternals.OBJECT_SYM;
    }


    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return Collections.emptyList();
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
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isAnonymousClass() {
        return false;
    }

    @Override
    public boolean isLocalClass() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public JClassSymbol getEnclosingClass() {
        return enclosing;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }


    @Nullable
    @Override
    public JTypeDeclSymbol getArrayComponent() {
        return null;
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
    public List<JTypeParameterSymbol> getTypeParameters() {
        return Collections.emptyList();
    }


    @Override
    public String toString() {
        return SymbolToStrings.SHARED.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.hash(this);
    }

}

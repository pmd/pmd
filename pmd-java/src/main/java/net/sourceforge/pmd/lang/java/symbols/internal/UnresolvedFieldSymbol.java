/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

final class UnresolvedFieldSymbol implements JFieldSymbol {

    private final JClassSymbol enclosing;
    private final TypeSystem ts;
    private final String simpleName;

    UnresolvedFieldSymbol(JClassSymbol enclosing,
                          TypeSystem ts,
                          String simpleName) {
        this.enclosing = enclosing;
        this.ts = ts;
        this.simpleName = simpleName;
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public boolean isEnumConstant() {
        return false;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC; // always accessible
    }

    @Override
    public @NonNull JClassSymbol getEnclosingClass() {
        return enclosing;
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        return ts.UNRESOLVED_TYPE;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }
}

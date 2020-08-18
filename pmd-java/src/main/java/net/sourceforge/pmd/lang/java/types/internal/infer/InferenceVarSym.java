/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class InferenceVarSym implements JTypeDeclSymbol {

    private final TypeSystem ts;
    private final JInferenceVar var;

    InferenceVarSym(TypeSystem ts, JInferenceVar var) {
        this.ts = ts;
        this.var = var;
    }

    @Override
    public @NonNull String getSimpleName() {
        return var.getName();
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitTypeDecl(this, param);
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        return null;
    }

    @Override
    public @NonNull String getPackageName() {
        return "";
    }

    @Override
    public String toString() {
        return "InferenceVar(" + getSimpleName() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InferenceVarSym that = (InferenceVarSym) o;
        return var.equals(that.var);
    }

    @Override
    public int hashCode() {
        return var.hashCode();
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class TParamStub implements JTypeParameterSymbol {

    private final String name;
    private final JTypeParameterOwnerSymbol owner;
    private final JTypeVar typeVar;
    private final String boundSignature;
    private final SignatureParser sigParser;

    TParamStub(String name, GenericSigBase<?> sig, String bound) {
        this.name = name;
        this.owner = sig.ctx;
        this.sigParser = sig.typeLoader();
        this.boundSignature = bound;

        TypeSystem ts = sig.ctx.getTypeSystem();
        this.typeVar = ts.newTypeVar(this);
    }

    @Override
    public @NonNull String getSimpleName() {
        return name;
    }

    @Override
    public JTypeMirror computeUpperBound() {
        return sigParser.parseTypeVarBound(owner.getLexicalScope(), boundSignature);
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return owner;
    }

    @Override
    public JTypeVar getTypeMirror() {
        return typeVar;
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return null;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return owner.getTypeSystem();
    }

    @Override
    public String toString() {
        return SymbolToStrings.ASM.toString(this);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.TYPE_PARAM.hash(this);
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.TYPE_PARAM.equals(this, obj);
    }

}

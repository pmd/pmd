/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class TParamStub implements JTypeParameterSymbol {

    private final String name;
    private final JTypeParameterOwnerSymbol owner;
    private final JTypeVar typeVar;
    private final String boundSignature;
    private final SignatureParser sigParser;
    private PSet<SymAnnot> annotations = HashTreePSet.empty();

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
        // Note: type annotations on the bound are added when applying
        // type annots collected on the enclosing symbol. See usages of
        // this method.
        return sigParser.parseTypeVarBound(owner.getLexicalScope(), boundSignature);
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return owner;
    }

    @Override
    public PSet<SymAnnot> getDeclaredAnnotations() {
        return annotations;
    }

    void addAnnotation(SymAnnot annot) {
        annotations = annotations.plus(annot);
    }

    @Override
    public JTypeVar getTypeMirror() {
        return typeVar;
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

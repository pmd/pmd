/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeAnnotationHelper.TypeAnnotationSetWithReferences;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
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
    private TypeAnnotationSetWithReferences typeAnnotationsOnBound;
    private boolean canComputeBound;

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
        if (!canComputeBound) {
            throw new IllegalStateException(
                    "Can't compute upper bound of " + name + " in " + owner.getEnclosingTypeParameterOwner());
        }
        JTypeMirror bound = sigParser.parseTypeVarBound(owner.getLexicalScope(), boundSignature);
        if (typeAnnotationsOnBound == null) {
            return bound;
        }
        // apply all type annotations.
        return typeAnnotationsOnBound.reduce(bound, (tyRef, path, annot, acc) -> {
            int boundIdx = tyRef.getTypeParameterBoundIndex();

            return applyTypeAnnotationToBound(path, annot, boundIdx, acc);
        });
    }

    private static JTypeMirror applyTypeAnnotationToBound(@Nullable TypePath path, SymAnnot annot, int boundIdx,
            JTypeMirror ub) {
        if (ub instanceof JIntersectionType) {
            JIntersectionType intersection = (JIntersectionType) ub;

            // Object is pruned from the component list
            boundIdx = intersection.getPrimaryBound().isTop() ? boundIdx - 1 : boundIdx;

            List<JTypeMirror> components = new ArrayList<>(intersection.getComponents());
            JTypeMirror bound = components.get(boundIdx);
            JTypeMirror newBound = TypeAnnotationHelper.applySinglePath(bound, path, annot);
            components.set(boundIdx, newBound);
            return intersection.getTypeSystem().glb(components);
        } else {
            return TypeAnnotationHelper.applySinglePath(ub, path, annot);
        }
    }

    /**
     * The bound cannot be computed before we have collected type annotations for
     * the bound. These may come from the parsing of the type annotations for the
     * enclosing declaration (method or class) so come relatively late.
     */
    void setCanComputeBound() {
        this.canComputeBound = true;
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

    void addAnnotationOnBound(TypeReference tyRef, @Nullable TypePath path, SymAnnot annot) {
        assert tyRef.getSort() == TypeReference.CLASS_TYPE_PARAMETER_BOUND
                || tyRef.getSort() == TypeReference.METHOD_TYPE_PARAMETER_BOUND;

        if (typeAnnotationsOnBound == null) {
            typeAnnotationsOnBound = new TypeAnnotationSetWithReferences();
        }
        typeAnnotationsOnBound.add(tyRef, path, annot);
    }
}

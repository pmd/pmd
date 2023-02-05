/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * An array type (1 dimension). Multi-level arrays have an array type
 * as component themselves.
 */
public final class JArrayType implements JTypeMirror {

    private final JTypeMirror component;
    private final TypeSystem ts;
    private final PSet<SymAnnot> typeAnnots;
    private JClassSymbol symbol;

    JArrayType(TypeSystem ts, JTypeMirror component) {
        this(ts, component, null, HashTreePSet.empty());
    }

    JArrayType(TypeSystem ts, JTypeMirror component, JClassSymbol arraySymbol, PSet<SymAnnot> typeAnnots) {
        assert component != null : "Expected non-null component";
        assert typeAnnots != null : "Expected non-null annotations";
        this.component = component;
        this.ts = ts;
        this.symbol = arraySymbol;
        this.typeAnnots = typeAnnots;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public @NonNull JClassSymbol getSymbol() {
        if (symbol == null) {
            JTypeDeclSymbol comp = getComponentType().getSymbol();
            if (comp == null) {
                comp = getComponentType().getErasure().getSymbol();
            }
            symbol = new ArraySymbolImpl(ts, comp); // will nullcheck
        }
        return symbol;
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return typeAnnots;
    }

    @Override
    public JArrayType withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        if (newTypeAnnots.isEmpty() && this.typeAnnots.isEmpty()) {
            return this;
        }
        return new JArrayType(ts, component, symbol, newTypeAnnots);
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public JArrayType getErasure() {
        JTypeMirror erasedComp = component.getErasure();
        return erasedComp == component ? this  // NOPMD CompareObjectsWithEquals
                                       : new JArrayType(ts, erasedComp, symbol, typeAnnots);
    }


    /**
     * Gets the component type of this array. This is the same type as
     * the array, stripped of a single array dimensions, e.g. the component
     * type of {@code int[][][]} is {@code int[][]}.
     *
     * @return The component type of this array type
     *
     * @see #getElementType()
     */
    public JTypeMirror getComponentType() {
        return component;
    }

    /**
     * Gets the element type of this array. This is the same type as
     * the array, stripped of all array dimensions, e.g. the element
     * type of {@code int[][][]} is {@code int}.
     *
     * @return The element type of this array type
     *
     * @see #getComponentType()
     */
    public JTypeMirror getElementType() {
        JTypeMirror c = this;
        while (c instanceof JArrayType) {
            c = ((JArrayType) c).getComponentType();
        }

        return c;
    }


    @Override
    public Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return Stream.concat(
            streamDeclaredMethods(prefilter),
            // inherited object methods
            ts.OBJECT.streamMethods(prefilter)
        );
    }

    @Override
    public Stream<JMethodSig> streamDeclaredMethods(Predicate<? super JMethodSymbol> prefilter) {
        return getSymbol().getDeclaredMethods().stream()
                          .filter(prefilter)
                          .map(it -> new ArrayMethodSigImpl(this, it));
    }

    @Override
    public List<JMethodSig> getConstructors() {
        return CollectionUtil.map(getSymbol().getConstructors(), it -> new ArrayMethodSigImpl(this, it));
    }

    @Override
    public boolean isRaw() {
        return getElementType().isRaw();
    }

    @Override
    public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitArray(this, p);
    }

    @Override
    public JArrayType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newComp = getComponentType().subst(subst);
        return newComp == component ? this // NOPMD UseEqualsToCompareObjectReferences
                                    : getTypeSystem().arrayType(newComp).withAnnotations(getTypeAnnotations());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JArrayType)) {
            return false;
        }
        JArrayType that = (JArrayType) o;
        return TypeOps.isSameType(this, that);
    }

    @Override
    public int hashCode() {
        return component.hashCode() * 3;
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

}

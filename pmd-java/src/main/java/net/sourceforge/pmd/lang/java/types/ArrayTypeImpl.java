/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.util.CollectionUtil;


final class ArrayTypeImpl implements JArrayType {

    private final JTypeMirror component;
    private final TypeSystem ts;
    private JClassSymbol symbol;

    private JVariableSig lengthField;

    ArrayTypeImpl(TypeSystem ts, JTypeMirror component) {
        assert component != null : "Expected non-null component";
        this.component = component;
        this.ts = ts;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    @NonNull
    public JClassSymbol getSymbol() {
        if (symbol == null) {
            JTypeDeclSymbol comp = getComponentType().getSymbol();
            if (comp == null) {
                // fake a symbol for the component
                comp = ts.symbols().fakeSymbol("(" + getComponentType().toString() + ")");
            }
            symbol = ts.symbols().makeArraySymbol(comp);
        }
        return symbol;
    }

    @Override
    public JArrayType getErasure() {
        JTypeMirror erasedComp = component.getErasure();
        return erasedComp == component ? this : new ArrayTypeImpl(ts, erasedComp); // NOPMD CompareObjectsWithEquals
    }

    @Override
    public JTypeMirror getComponentType() {
        return component;
    }

    @Override
    public Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return Stream.concat(
            getSymbol().getDeclaredMethods().stream()
                       .filter(prefilter)
                       .map(it -> new ArrayMethodSigImpl(this, it)),

            // inherited object methods
            ts.OBJECT.streamMethods(prefilter)
        );
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
    public @Nullable JVariableSig getField(String name) {
        if ("length".equals(name)) {
            if (lengthField == null) {
                lengthField = JVariableSig.forField(this, getSymbol().getDeclaredField("length"));
            }
            return lengthField;
        }
        return null;
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
        return Objects.hash(component);
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

}

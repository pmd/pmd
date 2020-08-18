/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

class IntersectionTypeImpl implements JIntersectionType {


    private final TypeSystem ts;
    private final JTypeMirror superClass;
    private final List<JTypeMirror> components;
    private JClassType induced;

    /**
     * @param superClass may be Object if every bound is an interface
     * @param allBounds  including the superclass, unless Object
     */
    IntersectionTypeImpl(TypeSystem ts,
                         JTypeMirror superClass,
                         List<JTypeMirror> allBounds) {
        this.superClass = superClass;
        this.components = Collections.unmodifiableList(allBounds);
        this.ts = ts;

        assert allBounds.size() > 1 : "Intersection of a single bound??"; // should be caught by GLB
        assert superClass instanceof JArrayType : "Intersection with an array is not well-formed"; // should be caught by GLB
        assert Lub.isExclusiveIntersectionBound(superClass) : "Wrong primary intersection bound " + superClass + " in " + this;

        checkWellFormed(allBounds);

    }

    @Override
    public JClassType getInducedClassType() {
        JTypeMirror primary = getPrimaryBound();
        if (primary instanceof JTypeVar) {
            // TODO, should generate an interface which has all the members of Ti
            throw new NotImplementedException("Intersection with type variable is not implemented yet");
        }

        if (induced == null) {
            JClassSymbol sym = ts.symbols().fakeIntersectionSymbol("", (JClassType) primary, getInterfaces());
            this.induced = (JClassType) ts.declaration(sym);
        }
        return induced;
    }

    @Override
    public @Nullable JTypeDeclSymbol getSymbol() {
        return null; // the induced intersection may be an interface
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public List<JTypeMirror> getComponents() {
        return components;
    }

    @Override
    public JTypeMirror getErasure() {
        return superClass.getErasure();
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JIntersectionType)) {
            return false;
        }
        JIntersectionType that = (JIntersectionType) o;
        return TypeOps.isSameType(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }

    @Override
    public @NonNull JTypeMirror getPrimaryBound() {
        return superClass;
    }

    @Override
    public @NonNull List<JClassType> getInterfaces() {
        List<JTypeMirror> list = superClass == ts.OBJECT ? components
                                                         : components.subList(1, components.size());
        return (List<JClassType>) (List) list; // the list is unmodifiable
    }

    private static void checkWellFormed(List<JTypeMirror> flattened) {
        for (int i = 0; i < flattened.size(); i++) {
            JTypeMirror ci = flattened.get(i);
            Objects.requireNonNull(ci, "Null intersection component");
            if (Lub.isExclusiveIntersectionBound(ci)) {
                if (i != 0) {
                    throw malformedIntersection(flattened);
                }
            } else if (ci instanceof JClassType) {
                // must be an interface, as per isExclusiveBlabla
                assert ci.isInterface();
            } else {
                throw malformedIntersection(flattened);
            }
        }
    }

    private static RuntimeException malformedIntersection(List<JTypeMirror> flattened) {
        return new IllegalArgumentException(
            "Malformed intersection: "
                + flattened.stream().map(JTypeMirror::toString).collect(Collectors.joining(" & "))
        );
    }
}

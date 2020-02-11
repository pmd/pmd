/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

class IntersectionTypeImpl implements JIntersectionType {


    private final TypeSystem ts;
    private final List<JTypeMirror> components;
    private JClassType induced;


    IntersectionTypeImpl(TypeSystem ts, List<JTypeMirror> components) {
        this.ts = ts;
        this.components = Collections.unmodifiableList(components);

        if (components.size() < 2) {
            throw new IllegalArgumentException("Intersection type should have more than one bound");
        }
    }

    @Override
    public JClassType getInducedClassType() {
        if (induced == null) {
            JTypeMirror glb = ts.glb(components);
            if (glb instanceof MinimalIntersection) {
                induced = ((MinimalIntersection) glb).induced;
            } else if (glb instanceof JClassType) {
                induced = (JClassType) glb;
            } else {
                induced = ts.OBJECT; // TODO typeVars, arrays?
            }
        }
        return induced;
    }

    @Override
    public boolean isInterface() {
        return getInducedClassType().isInterface();
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
        return components.get(0).getErasure();
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


    static final class MinimalIntersection extends IntersectionTypeImpl {

        private final JClassType induced;

        MinimalIntersection(TypeSystem ts,
                            JClassType superClass,
                            List<JTypeMirror> components) {
            super(ts, components);

            List<JClassType> superItfs = new ArrayList<>();

            for (JTypeMirror comp : components) {
                if (comp == superClass) {
                    continue;
                }
                if (comp instanceof JClassType) {
                    assert comp.isInterface()
                        || comp.getSymbol() != null && comp.getSymbol().isUnresolved()
                        : "Not an interface type " + comp + " in intersection " + components;

                    superItfs.add((JClassType) comp);
                } else if (comp instanceof JTypeVar) {
                    // TODO, should generate an interface which has all the members of Ti
                }
            }
            JClassSymbol sym = ts.symbols().fakeIntersectionSymbol("", superClass, Collections.unmodifiableList(superItfs));
            this.induced = (JClassType) ts.declaration(sym);
        }

        @Override
        public JClassType getInducedClassType() {
            return induced;
        }
    }
}

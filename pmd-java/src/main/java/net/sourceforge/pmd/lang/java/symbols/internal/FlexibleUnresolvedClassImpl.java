/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Unresolved <i>external reference</i> to a class.
 *
 * @see JClassSymbol#isUnresolved()
 */
final class FlexibleUnresolvedClassImpl extends UnresolvedClassImpl {

    private static final int UNKNOWN_ARITY = 0;

    private int arity = UNKNOWN_ARITY;
    private List<JTypeVar> tparams = Collections.emptyList();
    private List<UnresolvedClassImpl> childClasses = Collections.emptyList();

    FlexibleUnresolvedClassImpl(TypeSystem ts,
                                @Nullable JClassSymbol enclosing,
                                String canonicalName) {
        super(ts, enclosing, canonicalName);
    }

    /**
     * Set the number of type parameters of this type. If the arity was
     * already set to a value different from {@value #UNKNOWN_ARITY},
     * this does nothing: the unresolved type appears several times with
     * inconsistent arities, which must be reported later.
     *
     * @param newArity New number of type parameters
     */
    @Override
    void setTypeParameterCount(int newArity) {
        if (arity == UNKNOWN_ARITY) {
            this.arity = newArity;
            ArrayList<JTypeVar> newParams = new ArrayList<>(newArity);
            for (int i = 0; i < newArity; i++) {
                newParams.add(new FakeTypeParam("T" + i, getTypeSystem(), this).getTypeMirror());
            }
            this.tparams = Collections.unmodifiableList(newParams);
        }
    }

    @Override
    UnresolvedClassImpl getOrCreateUnresolvedChildClass(String simpleName) {
        if (childClasses.isEmpty()) {
            childClasses = new ArrayList<>(); // make it mutable
        }
        for (UnresolvedClassImpl childClass : childClasses) {
            if (childClass.nameEquals(simpleName)) {
                return childClass;
            }
        }
        FlexibleUnresolvedClassImpl newChild =
            new FlexibleUnresolvedClassImpl(getTypeSystem(), this, getCanonicalName() + '.' + simpleName);
        childClasses.add(newChild);
        return newChild;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return Collections.unmodifiableList(childClasses);
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return tparams;
    }

    private static class FakeTypeParam implements JTypeParameterSymbol {

        private final String name;
        private final JTypeParameterOwnerSymbol owner;
        private final JTypeVar tvar;

        private FakeTypeParam(String name, TypeSystem ts, JTypeParameterOwnerSymbol owner) {
            this.name = name;
            this.owner = owner;
            this.tvar = ts.newTypeVar(this);
        }

        @Override
        public TypeSystem getTypeSystem() {
            return tvar.getTypeSystem();
        }

        @Override
        public JTypeVar getTypeMirror() {
            return tvar;
        }

        @Override
        public JTypeMirror computeUpperBound() {
            return getTypeSystem().OBJECT;
        }

        @Override
        public @NonNull String getSimpleName() {
            return name;
        }

        @Override
        public JTypeParameterOwnerSymbol getDeclaringSymbol() {
            return owner;
        }

    }
}

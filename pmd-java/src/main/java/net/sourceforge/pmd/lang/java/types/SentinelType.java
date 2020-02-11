/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A "type" that exists outside of the main type hierarchy. This is only
 * used to have some sentinel values, to eg represent failure or errors.
 */
class SentinelType implements JTypeMirror {

    private final TypeSystem ts;
    private final String name;

    SentinelType(TypeSystem ts, String name) {
        this.ts = ts;
        this.name = name;
    }

    @Override
    public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        return this;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public Set<JTypeMirror> getSuperTypeSet() {
        return Collections.emptySet();
    }

    @Override
    public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitSentinel(this, p);
    }

    @Override
    public String toString() {
        return name;
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;


import static net.sourceforge.pmd.util.CollectionUtil.associateByTo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An index of type variables by name.
 */
public final class LexicalScope extends MapFunction<String, @Nullable JTypeVar> {

    /** The empty scope contains no vars. */
    public static final LexicalScope EMPTY = new LexicalScope(Collections.emptyMap());

    private LexicalScope(Map<String, ? extends JTypeVar> map) {
        super(Collections.unmodifiableMap(map));
    }

    /**
     * Returns the type var with the given name, or null.
     */
    @Override
    public @Nullable JTypeVar apply(@NonNull String var) {
        return getMap().get(var);
    }

    /**
     * Return a new scope which contains the given tvars. They shadow
     * tvars that were in this scope.
     */
    public LexicalScope andThen(List<? extends JTypeVar> vars) {
        if (this == EMPTY && vars.isEmpty()) {
            return EMPTY;
        }
        return new LexicalScope(associateByTo(new HashMap<>(getMap()), vars, JTypeVar::getName));
    }

}

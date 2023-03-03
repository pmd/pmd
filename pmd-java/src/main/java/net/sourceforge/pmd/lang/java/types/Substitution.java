/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;


import static net.sourceforge.pmd.util.CollectionUtil.associateWith;
import static net.sourceforge.pmd.util.CollectionUtil.zip;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A function from {@link SubstVar}s to types. Applying it to a type
 * replaces the occurrences of some variables with other types. This
 * can be done with {@link TypeOps#subst(JTypeMirror, Function)}.
 */
public final class Substitution extends MapFunction<@NonNull SubstVar, @NonNull JTypeMirror> {


    /** The empty substitution maps every type variable to itself. */
    public static final Substitution EMPTY = new Substitution(Collections.emptyMap());

    Substitution(Map<@NonNull SubstVar, @NonNull JTypeMirror> map) {
        super(map);
    }

    public static boolean isEmptySubst(Function<?, ?> m) {
        return m == EMPTY || m instanceof MapFunction && ((MapFunction<?, ?>) m).isEmpty();
    }

    /** Returns the type with which the given variable should be replaced. */
    @Override
    public @NonNull JTypeMirror apply(@NonNull SubstVar var) {
        return getMap().getOrDefault(var, var);
    }


    /**
     * Returns a composed substitution that first applies this substitution
     * to its input, and then applies the {@code after} substitution to the
     * result.
     *
     * <p>Given two substitutions S1, S2 and a type t:
     * <br>
     * {@code subst(subst(t, S1), S2) == subst(t, S1.andThen(S2)) }
     *
     * <p>For example:
     * <pre>{@code
     *  S1 = [ T -> A<U> ]
     *  S2 = [ U -> B<V> ]
     *
     *  subst(List<T>, S1)    = List<A<U>>
     *  subst(List<A<U>>, S2) = List<A<B<V>>>
     *
     *  S1.andThen(S2) = [ T -> A<B<V>>, U -> B<V> ]
     * }</pre>
     *
     * @param other the function to apply after this function is applied
     *
     * @return a composed substitution
     *
     * @throws NullPointerException if other is null
     */
    public Substitution andThen(Substitution other) {
        AssertionUtil.requireParamNotNull("subst", other);
        if (other.isEmpty()) {
            return this;
        }

        Map<SubstVar, JTypeMirror> newSubst = new HashMap<>(other.getMap());

        for (Entry<SubstVar, JTypeMirror> entry : getMap().entrySet()) {
            JTypeMirror substed = entry.getValue().subst(other);
            newSubst.put(entry.getKey(), substed);
        }

        return new Substitution(newSubst);
    }


    /**
     * Maps the given variable to the given type. This does not apply
     * this substitution to the type mirror.
     */
    public Substitution plus(SubstVar from, JTypeMirror to) {
        return new Substitution(CollectionUtil.plus(getMap(), from, to));
    }

    /**
     * Builds a substitution where the mapping from vars to types is
     * defined by the correspondence between the two lists.
     * <p>
     * If there are no vars to be mapped, then no substitution is returned
     * even though some types might have been supplied.
     *
     * @throws IllegalArgumentException If the two lists are of different lengths
     * @throws NullPointerException     If any of the two lists is null
     */
    public static Substitution mapping(List<? extends SubstVar> from, List<? extends JTypeMirror> to) {
        AssertionUtil.requireParamNotNull("from", from);
        AssertionUtil.requireParamNotNull("to", to);

        if (from.isEmpty()) {
            return EMPTY;
        }
        // zip throws IllegalArgumentException if the lists are of different lengths
        return new Substitution(zip(from, to));
    }


    /**
     * Returns a substitution that replaces the given type variables
     * with their erasure.
     *
     * @param tparams Type variables to erase
     */
    public static Substitution erasing(List<? extends JTypeVar> tparams) {
        if (tparams.isEmpty()) {
            return EMPTY;
        }
        return new Substitution(associateWith(tparams, JTypeMirror::getErasure));
    }


}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;


import static net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.definitely;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collector;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Tracks a set of overloads, automatically pruning override-equivalent
 * methods if possible.
 */
public abstract class OverloadSet<T> {

    private final List<T> overloads = new ArrayList<>();

    OverloadSet() {
        // package-private
    }

    void add(T sig) {
        ListIterator<T> iterator = overloads.listIterator();
        while (iterator.hasNext()) {
            T existing = iterator.next();

            switch (shouldTakePrecedence(existing, sig)) {
            case YES:
                // new sig is less specific than an existing one, don't add it
                return;
            case NO:
                // new sig is more specific than an existing one
                iterator.remove();
                break;
            case UNKNOWN:
                // neither sig is more specific
                break;
            default:
                throw new AssertionError();
            }
        }
        overloads.add(sig);
    }

    protected abstract OptionalBool shouldTakePrecedence(T m1, T m2);

    List<T> getOverloadsMutable() {
        return overloads;
    }

    boolean nonEmpty() {
        return !overloads.isEmpty();
    }

    /**
     * Returns a collector that can apply to a stream of method signatures,
     * and that collects them into a set of method, where none override one another.
     * Do not use this in a parallel stream. Do not use this to collect constructors.
     * Do not use this if your stream contains methods that have different names.
     *
     * @param commonSubtype Site where the signatures are observed. The owner of every method
     *                      in the stream must be a supertype of this type
     *
     * @return A collector
     */
    public static Collector<JMethodSig, ?, List<JMethodSig>> collectMostSpecific(JTypeMirror commonSubtype) {
        return Collector.of(
            () -> new ContextIndependentSet(commonSubtype),
            OverloadSet::add,
            (left, right) -> {
                throw new NotImplementedException("Cannot use this in a parallel stream");
            },
            o -> Collections.unmodifiableList(o.getOverloadsMutable())
        );
    }

    static final class ContextIndependentSet extends OverloadSet<JMethodSig> {

        private final JTypeMirror viewingSite;
        private String name;

        ContextIndependentSet(JTypeMirror viewingSite) {
            this.viewingSite = viewingSite;
        }


        @Override
        protected OptionalBool shouldTakePrecedence(JMethodSig m1, JMethodSig m2) {
            return areOverrideEquivalent(m1, m2)
                   ? shouldAlwaysTakePrecedence(m1, m2, viewingSite)
                   : OptionalBool.UNKNOWN;
        }

        @Override
        void add(JMethodSig sig) {
            if (name == null) {
                name = sig.getName();
            }
            assert sig.getName().equals(name) : "Not the right name!";
            assert !sig.isConstructor() : "Constructors they cannot override each other";
            super.add(sig);
        }
    }


    /**
     * Given that m1 and m2 are override-equivalent, should m1 be chosen
     * over m2 (YES/NO), for ANY call expression, or could both be applicable
     * given suitable expressions. This handles a few cases about shadowing/overriding/hiding
     * that are not covered strictly by the definition of "specificity".
     *
     * <p>If m1 and m2 are equal, returns the first one by convention.
     */
    static OptionalBool shouldAlwaysTakePrecedence(@NonNull JMethodSig m1, @NonNull JMethodSig m2, @NonNull JTypeMirror commonSubtype) {
        // select
        // 1. the non-bridge
        // 2. the one that overrides the other
        // 3. the non-abstract method

        // Symbols don't reflect bridge methods anymore
        // if (m1.isBridge() != m2.isBridge()) {
        //      return definitely(!m1.isBridge());
        // } else
        if (TypeOps.overrides(m1, m2, commonSubtype)) {
            return YES;
        } else if (TypeOps.overrides(m2, m1, commonSubtype)) {
            return NO;
        } else if (m1.isAbstract() ^ m2.isAbstract()) {
            return definitely(!m1.isAbstract());
        } else if (m1.isAbstract() && m2.isAbstract()) { // last ditch effort
            // both are unrelated abstract, inherited into 'site'
            // their signature would be merged into the site
            // if exactly one is declared in a class, prefer it
            // if both are declared in a class, ambiguity error (recall, neither overrides the other)
            // if both are declared in an interface, select any of them
            boolean m1InClass = m1.getSymbol().getEnclosingClass().isClass();
            boolean m2Class = m2.getSymbol().getEnclosingClass().isClass();

            return m1InClass && m2Class ? UNKNOWN : definitely(m1InClass);
        }

        if (Modifier.isPrivate(m1.getModifiers() | m2.getModifiers())
            && commonSubtype instanceof JClassType) {
            // One of them is private, which means, they can't be overridden,
            // so they failed the above test
            // Maybe it's shadowing then
            return shadows(m1, m2, (JClassType) commonSubtype);
        }

        return UNKNOWN;
    }

    /**
     * Returns whether m1 shadows m2 in the body of the given site, ie
     * m1 is declared in a class C1 that encloses the site, and m2 is declared
     * in a type that strictly encloses C1.
     *
     * <p>Assumes m1 and m2 are override-equivalent, and declared in different
     * classes.
     */
    // test only
    static OptionalBool shadows(JMethodSig m1, JMethodSig m2, JClassType site) {
        final JClassSymbol c1 = m1.getSymbol().getEnclosingClass();
        final JClassSymbol c2 = m2.getSymbol().getEnclosingClass();

        // We go outward from the `site`. The height measure is the distance
        // from the site (ie, the reverted depth of each class)

        int height = 0;
        int c1Height = -1;
        int c2Height = -1;
        JClassSymbol c = site.getSymbol();

        while (c != null) {
            if (c.equals(c1)) {
                c1Height = height;
            }
            if (c.equals(c2)) {
                c2Height = height;
            }
            c = c.getEnclosingClass();
            height++;
        }

        if (c1Height < 0 || c2Height < 0 || c1Height == c2Height) {
            return UNKNOWN;
        }
        return definitely(c1Height < c2Height);
    }
}

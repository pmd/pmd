/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;


import static net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent;
import static net.sourceforge.pmd.lang.java.types.internal.infer.OverloadComparator.shouldTakePrecedence;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * Tracks a set of overloads, automatically pruning override-equivalent
 * methods if possible.
 */
final class OverloadSet {

    private final List<JMethodSig> overloads = new ArrayList<>();


    void add(JMethodSig sig, JClassType site) {
        ListIterator<JMethodSig> iterator = overloads.listIterator();
        while (iterator.hasNext()) {
            JMethodSig existing = iterator.next();

            if (areOverrideEquivalent(existing, sig)) {
                switch (shouldTakePrecedence(existing, sig, site)) {
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
        }
        overloads.add(sig);
    }

    public List<JMethodSig> getOverloads() {
        return overloads;
    }

    public @Nullable JMethodSig getAsSingle() {
        return overloads.size() == 1 ? overloads.get(0) : null;
    }
}

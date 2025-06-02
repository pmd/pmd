/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Clément Fournier
 */
enum MethodResolutionPhase {
    /** No boxing is allowed, arity must match. */
    STRICT,

    /** Boxing required, arity must match. */
    LOOSE,

    /**
     * Boxing required, arity is extended for varargs. If a non-varargs method
     * failed the LOOSE phase, it has no chance of succeeding in VARARGS phase.
     */
    VARARGS,

    INVOC_STRICT, INVOC_LOOSE, INVOC_VARARGS;

    /**
     * Phases used to determine applicability.
     */
    static final Set<MethodResolutionPhase> APPLICABILITY_TESTS = EnumSet.of(STRICT, LOOSE, VARARGS);

    MethodResolutionPhase asInvoc() {
        switch (this) {
            case STRICT :
            return INVOC_STRICT;
            case LOOSE :
            return INVOC_LOOSE;
            case VARARGS :
            return INVOC_VARARGS;
            default :
            return this;
        }
    }

    boolean requiresVarargs() {
        return this == INVOC_VARARGS || this == VARARGS;
    }

    boolean canBox() {
        return this != STRICT;
    }

    /**
     * Last step, performed on the most specific applicable method. This adds
     * constraints on the arguments that are not pertinent to applicability to infer
     * all the tvars.
     */
    boolean isInvocation() {
        return this == INVOC_STRICT || this == INVOC_LOOSE || this == INVOC_VARARGS;
    }
}

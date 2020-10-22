/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * @author Clément Fournier
 */
enum MethodResolutionPhase {
    /** No boxing is allowed, arity must match. */
    STRICT,

    /** Boxing required, arity must match. */
    LOOSE,

    /**
     * Boxing required, arity is extended for varargs.
     * If a non-varargs method failed the LOOSE phase,
     * it has no chance of succeeding in VARARGS phase.
     */
    VARARGS {
        @Override
        public JTypeMirror ithFormal(List<JTypeMirror> formals, int i) {
            assert i >= 0;
            if (i >= formals.size() - 1) {
                JTypeMirror lastFormal = formals.get(formals.size() - 1);
                return ((JArrayType) lastFormal).getComponentType();
            }
            return formals.get(i);
        }
    },


    INVOC_STRICT,
    INVOC_LOOSE,
    INVOC_VARARGS {
        @Override
        public JTypeMirror ithFormal(List<JTypeMirror> formals, int i) {
            return VARARGS.ithFormal(formals, i);
        }
    };

    /**
     * Phases used to determine applicability.
     */
    static final Set<MethodResolutionPhase> APPLICABILITY_TESTS = EnumSet.of(STRICT, LOOSE, VARARGS);

    /**
     * For non-varargs phases, returns the type of the ith parameter.
     * For varargs phases, returns the ith variable arity parameter type
     * (https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12.2.4).
     *
     * <p>The index i starts at 0, while in the JLS it starts at 1.
     */
    public JTypeMirror ithFormal(List<JTypeMirror> formals, int i) {
        assert i >= 0 && i < formals.size();
        return formals.get(i);
    }

    MethodResolutionPhase asInvoc() {
        switch (this) {
        case STRICT:
            return INVOC_STRICT;
        case LOOSE:
            return INVOC_LOOSE;
        case VARARGS:
            return INVOC_VARARGS;
        default:
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
     * Last step, performed on the most specific applicable method.
     * This adds constraints on the arguments that are not
     * pertinent to applicability to infer all the tvars.
     */
    boolean isInvocation() {
        return this == INVOC_STRICT || this == INVOC_LOOSE || this == INVOC_VARARGS;
    }
}

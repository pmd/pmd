/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/** Represents a boolean that may not be present. Use as a non-null type. */
public enum OptionalBool {
    NO, UNKNOWN, YES;

    /**
     * Returns the logical complement.
     * <pre>{@code
     * yes -> no
     * unk -> unk
     * no -> yes
     * }</pre>
     */
    public OptionalBool complement() {
        switch (this) {
        case YES:
            return NO;
        case NO:
            return YES;
        default:
            return this;
        }
    }

    public static OptionalBool max(OptionalBool a, OptionalBool b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static OptionalBool min(OptionalBool a, OptionalBool b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    /**
     * If both values are the same, return it. Otherwise return UNKNOWN.
     * <pre>{@code
     * yes, yes -> yes
     * no, no -> no
     * everything else -> unk
     * }</pre>
     */
    public static OptionalBool join(OptionalBool a, OptionalBool b) {
        return a != b ? UNKNOWN : a;
    }

    /** Returns true this is not {@link #UNKNOWN}. */
    public boolean isKnown() {
        return this != UNKNOWN;
    }

    /** Returns true if this is {@link #YES}. */
    public boolean isTrue() {
        return this == YES;
    }

    /** Returns either YES or NO depending on the given boolean. */
    public static OptionalBool definitely(boolean a) {
        return a ? YES : NO;
    }

    public static OptionalBool unless(boolean a) {
        return a ? NO : YES;
    }
}

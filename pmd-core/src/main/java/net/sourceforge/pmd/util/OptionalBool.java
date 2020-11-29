/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/** Represents a boolean that may not be present. Use as a non-null type. */
public enum OptionalBool {
    NO, UNKNOWN, YES;

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

    public static OptionalBool mix(OptionalBool a, OptionalBool b) {
        return a != b ? UNKNOWN : a;
    }

    public boolean isKnown() {
        return this != UNKNOWN;
    }

    public boolean isTrue() {
        return this == YES;
    }

    public static OptionalBool definitely(boolean a) {
        return a ? YES : NO;
    }
}

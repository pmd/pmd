/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/** Represents a boolean that may not be present. Use as a non-null type. */
public enum OptionalBool {
    YES, NO, UNKNOWN;

    public OptionalBool complement() {
        switch (this) {
        case YES: return NO;
        case NO: return YES;
        default: return this;
        }
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

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

/**
 * Fallback strategy for unresolved stuff.
 */
public enum Fallback {
    AMBIGUOUS("ambiguous"),
    FIELD_ACCESS("a field access"),
    PACKAGE_NAME("a package name"),
    TYPE("an unresolved type");

    private final String displayName;

    Fallback(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

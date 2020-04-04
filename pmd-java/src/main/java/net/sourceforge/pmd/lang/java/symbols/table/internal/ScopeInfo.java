/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;

/**
 * A {@linkplain ShadowChainIterator#getScopeTag() scope tag} for java
 * shadow groups. This gives information about why a declaration is in
 * scope.
 */
public enum ScopeInfo {

    /** Declared in an enclosing class, and not inherited. */
    ENCLOSING_TYPE,
    /** Member of an enclosing type, that is not inherited. */
    ENCLOSING_TYPE_MEMBER,
    /** Inherited by some enclosing class. */
    INHERITED,
    /** Type parameter of some enclosing class. */
    TYPE_PARAM,
    /** Local var, including lambda parameters and lambda parameters. */
    LOCAL,

    IMPORT_ON_DEMAND,
    SAME_PACKAGE,
    JAVA_LANG,
    SINGLE_IMPORT,

    /** Sibling types in the same file, that are not nested into one another. */
    SAME_FILE,

    /** Method or constructor formal parameter (lambdas are treated as locals). */
    FORMAL_PARAM

}

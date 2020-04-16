/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;

/**
 * A {@linkplain ShadowChainIterator#getScopeTag() scope tag} for java
 * shadow chains. This gives information about why a declaration is in
 * scope.
 */
public enum ScopeInfo {

    /** An enclosing class. */
    ENCLOSING_TYPE,
    /** Member of an enclosing class, that is not inherited. */
    ENCLOSING_TYPE_MEMBER,
    /** Inherited by some enclosing class. */
    INHERITED,
    /** A type parameter of some enclosing class. */
    TYPE_PARAM,
    /** Local var, including lambda parameters and catch parameters. */
    LOCAL,

    // import-likes
    IMPORT_ON_DEMAND,
    SAME_PACKAGE,
    JAVA_LANG,
    SINGLE_IMPORT,

    /** Sibling types in the same file, that are not nested into one another. */
    SAME_FILE,
    /** Method or constructor formal parameter (lambdas are treated as locals). */
    FORMAL_PARAM

}

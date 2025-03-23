/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

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
    /** This merges {@link #ENCLOSING_TYPE_MEMBER} and {@link #INHERITED}. */
    METHOD_MEMBER,
    /** A type parameter of some enclosing class. */
    TYPE_PARAM,
    /** Local var, including lambda parameters and catch parameters. */
    LOCAL,

    // import-likes
    MODULE_IMPORT, // since 7.5.0; Java 23 preview
    IMPORT_ON_DEMAND,
    SAME_PACKAGE,
    JAVA_LANG, // default imports for any compilation unit
    SINGLE_IMPORT,
    SIMPLE_COMPILATION_UNIT, // since 7.5.0; default imports for java.io.IO.* and module java.base

    /** Sibling types in the same file, that are not nested into one another. */
    SAME_FILE,
    /** Method or constructor formal parameter (lambdas are treated as locals). */
    FORMAL_PARAM

}

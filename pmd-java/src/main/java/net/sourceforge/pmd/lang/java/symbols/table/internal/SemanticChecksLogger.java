/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.text.MessageFormat;

import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * TODO pull that up to PMD core, use for language level checks
 */
public interface SemanticChecksLogger {

    /**
     * Warning, classpath is misconfigured (or not configured).
     */
    String CANNOT_FIND_CLASSPATH_SYMBOL = "Symbol is not on the classpath: {0}";

    /**
     * Examples:
     * <ul>
     *     <li>we find T.Inner, where T is a type variable: Inner doesn't exist
     *     <li>we find A.B, where A is a resolved symbol, and B is not an inner class of B.
     *     This means that either the code doesn't compile, or classpath is outdated.
     * </ul>
     */
    String CANNOT_SELECT_TYPE_MEMBER = "{0} is not a type member of {1} {2}";

    /**
     * Warning, meaning we cannot disambiguate some ambiguous name and are
     * making some assumptions to proceed.
     */
    String CANNOT_RESOLVE_AMBIGUOUS_NAME = "Cannot resolve ambiguous name {0}, treating it as a {1}";

    String CANNOT_RESOLVE_MEMBER = "Cannot resolve a member named {0} in {1} , treating it as a {1}";


    /**
     * Log a warning at the given location.
     *
     * @param location Node owning the warning
     * @param message  Message, possibly formatted (see {@link MessageFormat})
     * @param args     Arguments for the template
     */
    void warning(JavaNode location, String message, Object... args);


    void error(JavaNode location, String message, Object... args);
}

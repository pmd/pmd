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

    // TODO how strict do we need to be here?
    //   many rules don't absolutely need correctness to work
    //   maybe we need to identify separate "levels" of the tree
    //   eg level 0: lexable (CPD)
    //      level 2: parsable (many syntax-only rules, eg UnnecessaryParentheses)
    //      level 3: type-resolved (more complicated rules)

    /**
     * Warning, classpath is misconfigured (or not configured).
     */
    String CANNOT_FIND_CLASSPATH_SYMBOL = "Symbol is not on the classpath: {0}";

    /**
     * We found T.Inner, where T is a type variable: Inner cannot exist,
     * this is broken code and not just misconfiguration.
     */
    String CANNOT_SELECT_MEMBER_FROM_TVAR = "{0} cannot be a member of the type variable {2}";

    /**
     * Warning, meaning we cannot disambiguate some ambiguous name and are
     * making some assumptions to proceed.
     */
    String CANNOT_RESOLVE_AMBIGUOUS_NAME = "Cannot resolve ambiguous name {0}, treating it as a {1}";

    String CANNOT_RESOLVE_MEMBER = "Cannot resolve ''{0}'' in {1}, treating it as {1}";


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

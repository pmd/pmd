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

    String CANNOT_FIND_CLASSPATH_SYMBOL = "Symbol is not on the classpath: {0}";


    /**
     * Log a warning at the given location.
     *
     * @param location Node owning the warning
     * @param message  Message, possibly formatted (see {@link MessageFormat})
     * @param args     Arguments for the template
     */
    void warning(JavaNode location, String message, Object... args);

}

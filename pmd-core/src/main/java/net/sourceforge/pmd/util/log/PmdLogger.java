/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

/**
 * Collects files to analyse before a PMD run. This API allows adding
 *
 * @author Clément Fournier
 */
public interface PmdLogger {

    void trace(String message, Object... formatArgs);

    void debug(String message, Object... formatArgs);

    void warning(String message, Object... formatArgs);

    void error(String message, Object... formatArgs);

    void error(String message, Throwable error);

    int numErrors();

}

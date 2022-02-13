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

    void info(String message, Object... formatArgs);

    void trace(String message, Object... formatArgs);

    void debug(String message, Object... formatArgs);

    void warning(String message, Object... formatArgs);

    void warningEx(String message, Throwable error);

    void warningEx(String message, Object[] formatArgs, Throwable error);

    void error(String message, Object... formatArgs);

    void errorEx(String message, Throwable error);

    void errorEx(String message, Object[] formatArgs, Throwable error);

    int numErrors();

}

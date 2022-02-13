/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author Clément Fournier
 */
public class SimplePmdLogger implements PmdLogger {

    private final Logger backend;
    private int numErrors;

    public SimplePmdLogger(Logger backend) {
        this.backend = backend;
    }

    @Override
    public void trace(String message, Object... formatArgs) {
        if (backend.isLoggable(Level.FINER)) {
            backend.finer(MessageFormat.format(message, formatArgs));
        }
    }

    @Override
    public void debug(String message, Object... formatArgs) {
        if (backend.isLoggable(Level.FINE)) {
            backend.fine(MessageFormat.format(message, formatArgs));
        }
    }

    @Override
    public void warning(String message, Object... formatArgs) {
        if (backend.isLoggable(Level.WARNING)) {
            backend.warning(MessageFormat.format(message, formatArgs));
        }
    }

    @Override
    public void warningEx(String message, Throwable error) {
        warningEx(message, new Object[0], error);
    }

    @Override
    public void warningEx(String message, Object[] formatArgs, Throwable error) {
        if (backend.isLoggable(Level.WARNING)) {
            message = MessageFormat.format(message, formatArgs);
            backend.severe(message + ": " + error.getMessage());
            if (backend.isLoggable(Level.FINE)) {
                backend.fine(ExceptionUtils.getStackTrace(error));
            }
        }
    }

    @Override
    public void error(String message, Object... formatArgs) {
        this.numErrors++;
        if (backend.isLoggable(Level.SEVERE)) {
            backend.severe(MessageFormat.format(message, formatArgs));
        }
    }

    @Override
    public void errorEx(String message, Throwable error) {
        errorEx(message, new Object[0], error);
    }

    @Override
    public void errorEx(String message, Object[] formatArgs, Throwable error) {
        this.numErrors++;
        if (backend.isLoggable(Level.SEVERE)) {
            message = MessageFormat.format(message, formatArgs);
            backend.severe(message + ": " + error.getMessage());
            if (backend.isLoggable(Level.FINE)) {
                backend.fine(ExceptionUtils.getStackTrace(error));
            }
        }
    }

    @Override
    public int numErrors() {
        return numErrors;
    }
}

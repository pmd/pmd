/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * A {@link Logger} (java.util) based logger impl.
 *
 * @author Clément Fournier
 */
public class SimpleMessageReporter extends MessageReporterBase implements PmdReporter {

    private final Logger backend;

    public SimpleMessageReporter(Logger backend) {
        this.backend = backend;
    }

    @Override
    protected boolean isLoggableImpl(Level level) {
        switch (level) {
        case ERROR:
            return backend.isErrorEnabled();
        case WARN:
            return backend.isWarnEnabled();
        case INFO:
            return backend.isInfoEnabled();
        case DEBUG:
            return backend.isDebugEnabled();
        case TRACE:
            return backend.isTraceEnabled();
        default:
            return false;
        }
    }

    @Override
    protected void logImpl(Level level, String message) {
        switch (level) {
        case ERROR:
            backend.error(message);
            break;
        case WARN:
            backend.warn(message);
            break;
        case INFO:
            backend.info(message);
            break;
        case DEBUG:
            backend.debug(message);
            break;
        case TRACE:
            backend.trace(message);
            break;
        default:
            throw new AssertionError("Invalid log level: " + level);
        }
    }
}

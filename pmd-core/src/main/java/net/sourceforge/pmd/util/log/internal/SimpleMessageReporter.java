/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * A {@link Logger} (java.util) based logger impl.
 *
 * @author Clément Fournier
 */
@InternalApi
public class SimpleMessageReporter extends MessageReporterBase implements MessageReporter {

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
    protected void logImpl(Level level, String message, Object[] formatArgs) {
        switch (level) {
        case ERROR:
            backend.error(message, formatArgs);
            break;
        case WARN:
            backend.warn(message, formatArgs);
            break;
        case INFO:
            backend.info(message, formatArgs);
            break;
        case DEBUG:
            backend.debug(message, formatArgs);
            break;
        case TRACE:
            backend.trace(message, formatArgs);
            break;
        default:
            throw new AssertionError("Invalid log level: " + level);
        }
    }
}

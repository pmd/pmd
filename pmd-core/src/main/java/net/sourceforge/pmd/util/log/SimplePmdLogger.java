/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * A {@link Logger} (java.util) based logger impl.
 *
 * @author Clément Fournier
 */
public class SimplePmdLogger extends PmdLoggerBase implements PmdLogger {

    private final Logger backend;

    public SimplePmdLogger(Logger backend) {
        this.backend = backend;
    }

    @Override
    protected boolean isLoggableImpl(Level level) {
        return backend.isLoggable(level.toJutilLevel());
    }

    @Override
    protected void logImpl(Level level, String message, Object[] formatArgs) {
        backend.log(level.toJutilLevel(), MessageFormat.format(message, formatArgs));
    }
}

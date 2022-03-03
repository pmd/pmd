/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * A logger that ignores all messages.
 *
 * @author Clément Fournier
 */
@InternalApi
public final class NoopPmdLogger extends PmdLoggerBase implements PmdLogger {

    public static final NoopPmdLogger INSTANCE = new NoopPmdLogger();

    private NoopPmdLogger() {

    }

    @Override
    protected boolean isLoggableImpl(Level level) {
        return false;
    }

    @Override
    protected void logImpl(Level level, String message, Object[] formatArgs) {
        // noop
    }
}

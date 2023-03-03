/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import org.slf4j.event.Level;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * A logger that ignores all messages.
 *
 * @author Clément Fournier
 */
@InternalApi
public class NoopReporter extends MessageReporterBase implements MessageReporter {

    // note: not singleton because PmdLogger accumulates error count.
    // note: not final because used as mock in tests.

    @Override
    protected boolean isLoggableImpl(Level level) {
        return false;
    }

    @Override
    protected void logImpl(Level level, String message) {
        // noop
    }
}

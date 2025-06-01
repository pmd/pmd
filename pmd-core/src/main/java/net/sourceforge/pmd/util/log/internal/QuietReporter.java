/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import net.sourceforge.pmd.util.log.PmdReporter;
import org.slf4j.event.Level;

/**
 * A logger that ignores all messages.
 *
 * @author Clément Fournier
 */
public class QuietReporter extends MessageReporterBase implements PmdReporter {

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

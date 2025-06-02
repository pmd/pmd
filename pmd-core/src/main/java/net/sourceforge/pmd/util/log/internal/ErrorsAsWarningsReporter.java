/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import org.slf4j.event.Level;

import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Turns errors into warnings reported on another logger.
 *
 * @author Clément Fournier
 */
public final class ErrorsAsWarningsReporter extends MessageReporterBase {

    private final PmdReporter backend;

    public ErrorsAsWarningsReporter(PmdReporter backend) {
        this.backend = backend;
    }

    @Override
    protected boolean isLoggableImpl(Level level) {
        if (level == Level.ERROR) {
            level = Level.WARN;
        }
        return super.isLoggableImpl(level);
    }

    @Override
    protected void logImpl(Level level, String message) {
        if (level == Level.ERROR) {
            level = Level.WARN;
        }
        backend.log(level, message);
    }
}

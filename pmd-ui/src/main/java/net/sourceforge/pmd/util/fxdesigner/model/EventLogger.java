/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.Objects;

import org.reactfx.EventStream;
import org.reactfx.value.Var;

/**
 * Logs events.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class EventLogger {

    private final Var<LogEntry> latestEvent = Var.newSimpleVar(null);

    public void logEvent(LogEntry event) {
        latestEvent.setValue(event);
    }

    /**
     * Returns a stream that emits an event each time an exception is logged by the parser.
     *
     */

    public EventStream<LogEntry> getLog() {
        return latestEvent.values().filter(Objects::nonNull);
    }
}

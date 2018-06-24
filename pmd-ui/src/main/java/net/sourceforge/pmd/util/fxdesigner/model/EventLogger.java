/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.Objects;

import org.reactfx.EventSource;
import org.reactfx.EventStream;

/**
 * Logs events.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class EventLogger {

    private final EventSource<LogEntry> latestEvent = new EventSource<>();

    public void logEvent(LogEntry event) {
        latestEvent.push(event);
    }

    /**
     * Returns a stream that emits an event each time an exception is logged by some
     * part of the application.
     */
    public EventStream<LogEntry> getLog() {
        return latestEvent.filter(Objects::nonNull);
    }
}

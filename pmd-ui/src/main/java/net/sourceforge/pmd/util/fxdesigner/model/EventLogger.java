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

    private Var<LogEntry> latestEvent = Var.newSimpleVar(null);

    public void logEvent(LogEntry event) {
        latestEvent.setValue(event);
    }

    public EventStream<LogEntry> getLog() {
        return latestEvent.values().filter(Objects::nonNull);
    }
}

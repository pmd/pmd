/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Logs events.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class EventLogger {

    private ObservableList<LogEntry> log = FXCollections.observableArrayList();


    public void logEvent(LogEntry event) {
        log.add(event);
    }


    /**
     * Gets an observable view of the log.
     *
     * @return The log
     */
    public ObservableList<LogEntry> getLog() {
        return log;
    }
}

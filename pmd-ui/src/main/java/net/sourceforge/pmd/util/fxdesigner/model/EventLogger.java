/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import static net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category.PARSE_EXCEPTION;
import static net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category.PARSE_OK;
import static net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category.XPATH_EVALUATION_EXCEPTION;
import static net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category.XPATH_OK;

import java.time.Duration;
import java.util.EnumSet;

import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.collection.LiveArrayList;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;


/**
 * Logs events. Stores the whole log in case no view was open.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class EventLogger {

    /**
     * Exceptions from XPath evaluation or parsing are never emitted
     * within less than that time interval to keep them from flooding the tableview.
     */
    private static final Duration PARSE_EXCEPTION_DELAY = Duration.ofMillis(3000);
    private final EventSource<LogEntry> latestEvent = new EventSource<>();
    private final LiveList<LogEntry> fullLog = new LiveArrayList<>();


    public EventLogger() {

        EventStream<LogEntry> onlyParseException =
            latestEvent.filter(x -> x.getCategory() == PARSE_EXCEPTION || x.getCategory() == PARSE_OK)
                       .successionEnds(PARSE_EXCEPTION_DELAY)
                       // don't output anything when the last state recorded was OK
                       .filter(x -> x.getCategory() != PARSE_OK);

        EventStream<LogEntry> onlyXPathException =
            latestEvent.filter(x -> x.getCategory() == XPATH_EVALUATION_EXCEPTION || x.getCategory() == XPATH_OK)
                       .successionEnds(PARSE_EXCEPTION_DELAY)
                       // don't output anything when the last state recorded was OK
                       .filter(x -> x.getCategory() != XPATH_OK);

        EnumSet<Category> otherExceptionSet = EnumSet.complementOf(EnumSet.of(PARSE_EXCEPTION, XPATH_EVALUATION_EXCEPTION, PARSE_OK, XPATH_OK));

        EventStream<LogEntry> otherExceptions = latestEvent.filter(x -> otherExceptionSet.contains(x.getCategory()));

        EventStreams.merge(onlyParseException, otherExceptions, onlyXPathException)
                    .subscribe(fullLog::add);
    }


    /** Number of log entries that were not yet examined by the user. */
    public Val<Integer> numNewLogEntriesProperty() {
        return DesignerUtil.countNotMatching(fullLog.map(LogEntry::wasExaminedProperty));
    }


    /** Total number of log entries. */
    public Val<Integer> numLogEntriesProperty() {
        return fullLog.sizeProperty();
    }


    public void logEvent(LogEntry event) {
        if (event != null) {
            latestEvent.push(event);
        }
    }

    /**
     * Returns the full log.
     */
    public LiveList<LogEntry> getLog() {
        return fullLog;
    }
}

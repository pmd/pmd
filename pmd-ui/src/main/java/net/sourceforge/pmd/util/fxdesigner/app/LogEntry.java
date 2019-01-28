/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import static net.sourceforge.pmd.util.fxdesigner.app.LogEntry.Category.CategoryType.FLAG;

import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reactfx.value.Var;

import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource.NodeSelectionEvent;


/**
 * Log entry of an {@link EventLogger}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class LogEntry implements Comparable<LogEntry> {


    private final String shortMessage;
    private final Category category;
    private final String detailsText;
    private final Date timestamp;
    private final Var<Boolean> wasExamined = Var.newSimpleVar(false);


    private LogEntry(String detailsText, String shortMessage, Category cat) {
        this.detailsText = detailsText;
        this.shortMessage = shortMessage;
        this.category = cat;
        this.timestamp = new Date();
    }


    public boolean isWasExamined() {
        return wasExamined.getValue();
    }


    public void setExamined(boolean wasExamined) {
        this.wasExamined.setValue(wasExamined);
    }

    public Var<Boolean> wasExaminedProperty() {
        return wasExamined;
    }


    public String getMessage() {
        return shortMessage;
    }


    public Category getCategory() {
        return category;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    @Override
    public int compareTo(LogEntry o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }


    public String getDetails() {
        return detailsText;
    }


    public boolean isInternal() {
        return category == Category.INTERNAL;
    }


    public static LogEntry createUserExceptionEntry(Throwable thrown, Category cat) {
        return new LogEntry(ExceptionUtils.getStackTrace(thrown), thrown.getMessage(), cat);
    }


    /**
     * Just for the flag categories {@link Category#PARSE_OK} and {@link Category#XPATH_OK},
     * which are not rendered in the log.
     */
    public static LogEntry createUserFlagEntry(Category flagCategory) {
        return new LogEntry("", "", flagCategory);
    }


    public static LogEntry createInternalExceptionEntry(Throwable thrown) {
        return createUserExceptionEntry(thrown, Category.INTERNAL);
    }


    public static LogEntry createInternalDebugEntry(String shortMessage, String details) {
        return new LogEntry(details, shortMessage, Category.INTERNAL);
    }


    public static LogEntryWithData<NodeSelectionEvent> createNodeSelectionEventTraceEntry(NodeSelectionEvent event, String details) {
        return new LogEntryWithData<>(details, event.toString(), Category.SELECTION_EVENT_TRACING, event);
    }


    public enum Category {
        // all of those are "user" categories, which are relevant to a regular user of the app

        PARSE_EXCEPTION("Parse exception"),
        TYPERESOLUTION_EXCEPTION("Type resolution exception"),
        QNAME_RESOLUTION_EXCEPTION("Qualified name resolution exception"),
        SYMBOL_FACADE_EXCEPTION("Symbol façade exception"),
        XPATH_EVALUATION_EXCEPTION("XPath evaluation exception"),

        // These are "flag" categories that signal that previous exceptions
        // thrown during code or XPath edition may be discarded as uninteresting
        PARSE_OK("Parsing success", CategoryType.INTERNAL),
        XPATH_OK("XPath evaluation success", CategoryType.INTERNAL),

        /**
         * Used for events that occurred internally to the app and are only relevant to a developer of the app.
         */
        INTERNAL("Internal event", CategoryType.INTERNAL),
        SELECTION_EVENT_TRACING("Selection event tracing", CategoryType.INTERNAL);

        public final String name;
        private final CategoryType type;


        Category(String name) {
            this(name, CategoryType.USER_EXCEPTION);
        }


        Category(String name, CategoryType type) {
            this.name = name;
            this.type = type;
        }


        @Override
        public String toString() {
            return name;
        }


        public boolean isFlag() {
            return type == FLAG;
        }


        public boolean isInternal() {
            return type == CategoryType.INTERNAL;
        }


        public boolean isUserException() {
            return type == CategoryType.USER_EXCEPTION;
        }


        enum CategoryType {
            USER_EXCEPTION,
            FLAG,
            INTERNAL
        }
    }

    public static class LogEntryWithData<T> extends LogEntry {

        private final T userData;


        private LogEntryWithData(String detailsText, String shortMessage, Category cat, T userData) {
            super(detailsText, shortMessage, cat);
            this.userData = userData;
        }


        public T getUserData() {
            return userData;
        }


        static LogEntryWithData<NodeSelectionEvent> reduceEventTrace(LogEntryWithData<NodeSelectionEvent> prev, LogEntryWithData<NodeSelectionEvent> next) {
            return createNodeSelectionEventTraceEntry(prev.getUserData(), prev.getDetails() + "\n" + next.getDetails());
        }
    }

}

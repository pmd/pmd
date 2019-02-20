/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reactfx.value.Var;


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


    public static <T> LogEntryWithData<T> createDataEntry(T data, Category category, String details) {
        return new LogEntryWithData<>(details, Objects.toString(data), category, data);
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
        // When in developer mode they're pushed to the event log too
        PARSE_OK("Parsing success", CategoryType.INTERNAL),
        XPATH_OK("XPath evaluation success", CategoryType.INTERNAL),

        // These are used for events that occurred internally to the app and are
        // only relevant to a developer of the app.
        INTERNAL("Internal event", CategoryType.INTERNAL),
        SELECTION_EVENT_TRACING("Selection event tracing", CategoryType.TRACE);

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


        /** Internal categories are only logged if the app is in developer mode. */
        public boolean isInternal() {
            return type != CategoryType.USER_EXCEPTION;
        }


        public boolean isUserException() {
            return type == CategoryType.USER_EXCEPTION;
        }


        public boolean isTrace() {
            return type == CategoryType.TRACE;
        }

        enum CategoryType {
            USER_EXCEPTION,
            INTERNAL,
            /** Trace events are aggregated. */
            TRACE
        }
    }

    static class LogEntryWithData<T> extends LogEntry {

        private final T userData;


        private LogEntryWithData(String detailsText, String shortMessage, Category cat, T userData) {
            super(detailsText, shortMessage, cat);
            this.userData = userData;
        }


        public T getUserData() {
            return userData;
        }


        static <T> LogEntryWithData<T> reduceEventTrace(LogEntryWithData<T> prev, LogEntryWithData<T> next) {
            return createDataEntry(prev.getUserData(), prev.getCategory(), prev.getDetails() + "\n" + next.getDetails());
        }
    }

}

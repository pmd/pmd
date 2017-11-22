/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Log entry of an {@link EventLogger}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class LogEntry {


    private final Throwable throwable;
    private final Category category;
    private final Date timestamp;


    public LogEntry(Throwable thrown, Category cat) {
        this.throwable = thrown;
        this.category = cat;
        timestamp = new Date();
    }


    public String getMessage() {
        return throwable.getMessage();
    }


    public Category getCategory() {
        return category;
    }


    public String getStackTrace() {
        return ExceptionUtils.getStackTrace(throwable);
    }


    public Date getTimestamp() {
        return timestamp;
    }


    public enum Category {
        PARSE_EXCEPTION("Parse exception"),
        TYPERESOLUTION_EXCEPTION("Type resolution exception"),
        SYMBOL_FACADE_EXCEPTION("Symbol façade exception"),
        XPATH_EVALUATION_EXCEPTION("XPath evaluation exception"),
        OTHER("Other");

        public final String name;


        Category(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }


}

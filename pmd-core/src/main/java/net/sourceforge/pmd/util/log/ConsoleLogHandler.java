/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * Log to the console using a basic formatter.
 * 
 * @author Wouter Zelle
 */
public class ConsoleLogHandler extends Handler {

    private static final Formatter FORMATTER = new PmdLogFormatter();

    public void publish(LogRecord logRecord) {
        System.out.println(FORMATTER.format(logRecord));
        if (logRecord.getThrown() != null) {
            // Use the same channel, to make sure that the stacktrace comes
            // after the message on the console (using printStackTrace
            // directly messes things up)
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            logRecord.getThrown().printStackTrace(printWriter);
            System.out.println(stringWriter.toString());
        }
    }
    
    public void close() throws SecurityException {
    }

    public void flush() {
    }
}
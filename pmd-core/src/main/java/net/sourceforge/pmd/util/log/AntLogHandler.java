/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.tools.ant.Project;

/**
 * AntLogHandler sends log messages to an Ant Task, so the regular Ant logging
 * is used.
 * 
 * @author Wouter Zelle
 */
public class AntLogHandler extends Handler {
    private Project project;

    private static final Formatter FORMATTER = new PmdLogFormatter();

    public AntLogHandler(Project project) {
        this.project = project;
    }

    public void publish(LogRecord logRecord) {
        //Map the log levels from java.util.logging to Ant
        int antLevel;
        Level level = logRecord.getLevel();
        if (level == Level.FINEST) {
            antLevel = Project.MSG_DEBUG;   //Shown when -debug is supplied to Ant
        } else if (level == Level.FINE || level == Level.FINER || level == Level.CONFIG) {
            antLevel = Project.MSG_VERBOSE; //Shown when -verbose is supplied to Ant
        } else if (level == Level.INFO) {
            antLevel = Project.MSG_INFO;    //Always shown
        } else if (level == Level.WARNING) {
            antLevel = Project.MSG_WARN;    //Always shown
        } else if (level == Level.SEVERE) {
            antLevel = Project.MSG_ERR;     //Always shown
        } else {
            throw new IllegalStateException("Unknown logging level");   //shouldn't get ALL or NONE
        }
        
        project.log(FORMATTER.format(logRecord), antLevel);
        if (logRecord.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            logRecord.getThrown().printStackTrace(printWriter);
            project.log(stringWriter.toString(), antLevel);
        }
    }

    public void close() throws SecurityException {
    }

    public void flush() {
    }
}
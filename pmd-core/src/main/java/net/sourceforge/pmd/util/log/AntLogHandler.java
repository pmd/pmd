/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.tools.ant.BuildListener;
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
    private static final Level[] LOG_LEVELS = {
        Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINEST,
    };

    public AntLogHandler(Project project) {
        this.project = project;
    }
    
    public Level getAntLogLevel() {
        Class<?> clazz;
        int maxLevel = Project.MSG_ERR;
        for (final BuildListener l : project.getBuildListeners()) {
            clazz = l.getClass();
            Field declaredField = null;
            try {
                while (BuildListener.class.isAssignableFrom(clazz)) {
                    try {
                        declaredField = clazz.getDeclaredField("msgOutputLevel");
                    } catch (final NoSuchFieldException ignored) {
                        try {
                            declaredField = clazz.getDeclaredField("logLevel");
                        } catch (final NoSuchFieldException expected) {
                            // ignore it
                        }
                    }
                    if (declaredField != null) {
                        break;
                    }
                    
                    clazz = clazz.getSuperclass();
                }
                
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    final int level = declaredField.getInt(l);
                    if (maxLevel < level) {
                        maxLevel = level;
                    }
                }
            } catch (final IllegalArgumentException | IllegalAccessException ignored) {
                // Just ignore it
            }
        }
        
        return LOG_LEVELS[maxLevel];
    }

    @Override
    public void publish(LogRecord logRecord) {
        // Map the log levels from java.util.logging to Ant
        int antLevel;
        Level level = logRecord.getLevel();
        if (level == Level.FINEST) {
            antLevel = Project.MSG_DEBUG; // Shown when -debug is supplied to
            // Ant
        } else if (level == Level.FINE || level == Level.FINER || level == Level.CONFIG) {
            antLevel = Project.MSG_VERBOSE; // Shown when -verbose is supplied
            // to Ant
        } else if (level == Level.INFO) {
            antLevel = Project.MSG_INFO; // Always shown
        } else if (level == Level.WARNING) {
            antLevel = Project.MSG_WARN; // Always shown
        } else if (level == Level.SEVERE) {
            antLevel = Project.MSG_ERR; // Always shown
        } else {
            throw new IllegalStateException("Unknown logging level"); // shouldn't
            // get ALL
            // or NONE
        }

        project.log(FORMATTER.format(logRecord), antLevel);
        if (logRecord.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            logRecord.getThrown().printStackTrace(printWriter);
            project.log(stringWriter.toString(), antLevel);
        }
    }

    @Override
    public void close() throws SecurityException {
        // nothing to do
    }

    @Override
    public void flush() {
        // nothing to do
    }
}

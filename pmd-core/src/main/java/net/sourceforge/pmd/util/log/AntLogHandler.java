/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.XmlLogger;
import org.apache.tools.ant.taskdefs.RecorderEntry;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * AntLogHandler sends log messages to an Ant Task, so the regular Ant logging
 * is used.
 *
 * @author Wouter Zelle
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public class AntLogHandler extends Handler {
    private Project project;

    private static final Level DEFAULT_LEVEL = Level.WARNING;

    private static final Formatter FORMATTER = new PmdLogFormatter();

    // Maps from ant's Project.MSG_* to java.util.logging.Level
    private static final Level[] LOG_LEVELS = {
        Level.SEVERE,   // Project.MSG_ERR=0
        Level.WARNING,  // Project.MSG_WARN=1
        Level.INFO,     // Project.MSG_INFO=2
        Level.CONFIG,   // Project.MSG_VERBOSE=3
        Level.FINEST,   // Project.MSG_DEBUG=4
    };

    public AntLogHandler(Project project) {
        this.project = project;
    }

    public Level getAntLogLevel() {
        for (final BuildListener l : project.getBuildListeners()) {
            Field declaredField = null;
            try {
                if (l instanceof DefaultLogger) {
                    declaredField = DefaultLogger.class.getDeclaredField("msgOutputLevel");
                } else if (l instanceof XmlLogger) {
                    declaredField = XmlLogger.class.getDeclaredField("msgOutputLevel");
                } else if (l instanceof RecorderEntry) {
                    declaredField = RecorderEntry.class.getDeclaredField("loglevel");
                } else if (l.getClass().getName().equals("org.gradle.api.internal.project.ant.AntLoggingAdapter")) {
                    return determineGradleLogLevel(l);
                } else {
                    try {
                        declaredField = l.getClass().getDeclaredField("logLevel");
                        if (declaredField.getType() != Integer.class && declaredField.getType() != int.class) {
                            declaredField = null;
                            project.log("Unsupported build listener: " + l.getClass(), Project.MSG_DEBUG);
                        }
                    } catch (final NoSuchFieldException e) {
                        project.log("Unsupported build listener: " + l.getClass(), Project.MSG_DEBUG);
                    }
                }

                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    return LOG_LEVELS[declaredField.getInt(l)];
                }

            } catch (final ReflectiveOperationException ignored) {
                // Just ignore it
            }
        }

        project.log("Could not determine ant log level, no supported build listeners found. "
                + "Log level is set to " + DEFAULT_LEVEL, Project.MSG_WARN);

        return DEFAULT_LEVEL;
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

    private Level determineGradleLogLevel(BuildListener l) {
        try {
            project.log("Detected gradle AntLoggingAdapter", Project.MSG_DEBUG);
            Field loggerField = l.getClass().getDeclaredField("logger");
            loggerField.setAccessible(true);
            // org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger
            Object logger = loggerField.get(l);

            Class<?> gradleLogLevel = l.getClass().getClassLoader().loadClass("org.gradle.api.logging.LogLevel");

            Method isLevelAtMostMethod = logger.getClass().getDeclaredMethod("isLevelAtMost", gradleLogLevel);
            isLevelAtMostMethod.setAccessible(true);

            Object[] logLevels = gradleLogLevel.getEnumConstants();
            // the log levels in gradle are declared in the order DEBUG, INFO, LIFECYCLE, WARN, QUIET, ERROR
            Level[] mapping = new Level[] {
                Level.FINEST,   // DEBUG
                Level.CONFIG,   // INFO
                Level.INFO,     // LIFECYCLE
                Level.WARNING,  // WARN
                Level.SEVERE,   // QUIET
                Level.SEVERE,   // ERROR
            };

            for (int i = 0; i < Math.min(logLevels.length, mapping.length); i++) {
                boolean enabled = (boolean) isLevelAtMostMethod.invoke(logger, logLevels[i]);
                if (enabled) {
                    project.log("Current log level: " + logLevels[i] + " -> " + mapping[i], Project.MSG_DEBUG);
                    return mapping[i];
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // ignored
        }
        project.log("Could not determine log level, falling back to default: " + DEFAULT_LEVEL, Project.MSG_WARN);
        return DEFAULT_LEVEL;
    }
}

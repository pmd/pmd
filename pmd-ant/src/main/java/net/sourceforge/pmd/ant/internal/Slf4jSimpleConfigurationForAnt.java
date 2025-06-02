/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant.internal;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.XmlLogger;
import org.apache.tools.ant.taskdefs.RecorderEntry;
import org.slf4j.event.Level;

import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

public final class Slf4jSimpleConfigurationForAnt {
    private Slf4jSimpleConfigurationForAnt() { }

    private static final Level DEFAULT_LEVEL = Level.INFO;

    // Maps from ant's Project.MSG_* to org.slf4j.event.Level
    private static final Level[] LOG_LEVELS = {
        Level.ERROR,   // Project.MSG_ERR=0
        Level.WARN,    // Project.MSG_WARN=1
        Level.INFO,    // Project.MSG_INFO=2
        Level.DEBUG,   // Project.MSG_VERBOSE=3
        Level.TRACE,   // Project.MSG_DEBUG=4
    };

    @SuppressWarnings("PMD.CloseResource")
    public static Level reconfigureLoggingForAnt(Project antProject) {
        if (!Slf4jSimpleConfiguration.isSimpleLogger()) {
            // do nothing, not even set system properties, if not Simple Logger is in use
            return DEFAULT_LEVEL;
        }

        PrintStream original = System.err;
        try {
            System.setErr(new SimpleLoggerToAntBridge(antProject, original));

            // configuring the format so that the log level appears at the beginning of the printed line
            System.setProperty("org.slf4j.simpleLogger.showDateTime", "false");
            System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
            System.setProperty("org.slf4j.simpleLogger.showThreadId", "false");
            System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "false");

            // using cacheOutputStream so that we can restore System.err after SimpleLogger has been initialized
            System.setProperty("org.slf4j.simpleLogger.cacheOutputStream", "true");
            System.setProperty("org.slf4j.simpleLogger.logFile", "System.err");

            Level level = getAntLogLevel(antProject);
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(level);

            return level;
        } finally {
            System.setErr(original);
        }
    }

    private static final class SimpleLoggerToAntBridge extends PrintStream {
        private static final Map<String, Integer> ANT_LOG_LEVELS;

        static {
            ANT_LOG_LEVELS = new HashMap<>();
            ANT_LOG_LEVELS.put(Level.ERROR.name(), Project.MSG_ERR);
            ANT_LOG_LEVELS.put(Level.WARN.name(), Project.MSG_WARN);
            ANT_LOG_LEVELS.put(Level.INFO.name(), Project.MSG_INFO);
            ANT_LOG_LEVELS.put(Level.DEBUG.name(), Project.MSG_VERBOSE);
            ANT_LOG_LEVELS.put(Level.TRACE.name(), Project.MSG_DEBUG);
        }

        private final StringBuilder buffer = new StringBuilder(100);
        private final Project antProject;

        SimpleLoggerToAntBridge(Project antProject, PrintStream original) {
            super(original);
            this.antProject = antProject;
        }

        @Override
        public void println(String x) {
            buffer.append(x).append(System.lineSeparator());
        }

        @Override
        public void flush() {
            String logLevel = determineLogLevel();
            int antLogLevel = ANT_LOG_LEVELS.getOrDefault(logLevel, Project.MSG_INFO);
            antProject.log(buffer.toString(), antLogLevel);
            buffer.setLength(0);
        }

        private String determineLogLevel() {
            int firstSpace = buffer.indexOf(" ");
            if (firstSpace != -1) {
                String level = buffer.substring(0, firstSpace);
                buffer.delete(0, firstSpace + 1);
                return level;
            }
            return DEFAULT_LEVEL.name();
        }
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static Level getAntLogLevel(Project project) {
        for (final BuildListener l : project.getBuildListeners()) {
            Field declaredField = null;
            try {
                if (l instanceof DefaultLogger) {
                    declaredField = DefaultLogger.class.getDeclaredField("msgOutputLevel");
                } else if (l instanceof XmlLogger) {
                    declaredField = XmlLogger.class.getDeclaredField("msgOutputLevel");
                } else if (l instanceof RecorderEntry) {
                    declaredField = RecorderEntry.class.getDeclaredField("loglevel");
                } else if ("org.gradle.api.internal.project.ant.AntLoggingAdapter".equals(l.getClass().getName())) {
                    return determineGradleLogLevel(project, l);
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

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static Level determineGradleLogLevel(Project project, BuildListener l) {
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
                Level.TRACE,   // DEBUG
                Level.DEBUG,   // INFO
                Level.INFO,     // LIFECYCLE
                Level.WARN,  // WARN
                Level.ERROR,   // QUIET
                Level.ERROR,   // ERROR
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

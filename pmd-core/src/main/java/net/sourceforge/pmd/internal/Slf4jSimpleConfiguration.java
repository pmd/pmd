/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.event.Level;

public final class Slf4jSimpleConfiguration {
    private static final String SIMPLE_LOGGER_FACTORY_CLASS = "org.slf4j.impl.SimpleLoggerFactory";
    private static final String SIMPLE_LOGGER_CLASS = "org.slf4j.impl.SimpleLogger";
    private static final String SIMPLE_LOGGER_CONFIGURATION = "org.slf4j.impl.SimpleLoggerConfiguration";
    private static final String PMD_ROOT_LOGGER = "net.sourceforge.pmd";

    private Slf4jSimpleConfiguration() { }

    public static void reconfigureDefaultLogLevel(Level level) {
        if (!isSimpleLogger()) {
            // do nothing, not even set system properties, if not Simple Logger is in use
            return;
        }

        if (level != null) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", level.toString());
        }

        // Call SimpleLogger.init() by reflection.
        // Alternatively: move the CLI related classes into an own module, add
        // slf4j-simple as a compile dependency and create a PmdSlf4jSimpleFriend class in
        // the package org.slf4j.simple to gain access to this package-private init method.
        //
        // SimpleLogger.init() will reevaluate the configuration from the system properties or
        // simplelogger.properties file.
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        ClassLoader classLoader = loggerFactory.getClass().getClassLoader();
        try {
            Class<?> simpleLoggerClass = classLoader.loadClass(SIMPLE_LOGGER_CLASS);
            Method initMethod = simpleLoggerClass.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(null);

            int newDefaultLogLevel = getDefaultLogLevelInt(simpleLoggerClass);

            Field currentLogLevelField = simpleLoggerClass.getDeclaredField("currentLogLevel");
            currentLogLevelField.setAccessible(true);

            Method levelStringMethod = simpleLoggerClass.getDeclaredMethod("recursivelyComputeLevelString");
            levelStringMethod.setAccessible(true);

            Method stringToLevelMethod = classLoader.loadClass(SIMPLE_LOGGER_CONFIGURATION)
                    .getDeclaredMethod("stringToLevel", String.class);
            stringToLevelMethod.setAccessible(true);

            // Change the logging level of loggers that were already created.
            // For this we fetch the map of name to logger that is stored in the logger factory,
            // then set the log level field of each logger via reflection.
            // The new log level is determined similar to the constructor of SimpleLogger, that
            // means, configuration params are being considered.
            Class<?> loggerFactoryClass = classLoader.loadClass(SIMPLE_LOGGER_FACTORY_CLASS);
            Field loggerMapField = loggerFactoryClass.getDeclaredField("loggerMap");
            loggerMapField.setAccessible(true);
            // we checked previously, that loggerFactory instanceof SimpleLoggerFactory
            // see #isSimpleLogger()
            @SuppressWarnings("unchecked")
            Map<String, Logger> loggerMap = (Map<String, Logger>) loggerMapField.get(loggerFactory);
            for (Logger logger : loggerMap.values()) {
                if (logger.getName().startsWith(PMD_ROOT_LOGGER)
                    && simpleLoggerClass.isAssignableFrom(logger.getClass())) {
                    String newConfiguredLevel = (String) levelStringMethod.invoke(logger);
                    int newLogLevel = newDefaultLogLevel;
                    if (newConfiguredLevel != null) {
                        newLogLevel = (int) stringToLevelMethod.invoke(null, newConfiguredLevel);
                    }
                    currentLogLevelField.set(logger, newLogLevel);
                }
            }
        } catch (ReflectiveOperationException | ClassCastException ex) {
            System.err.println("Error while initializing logging: " + ex);
        }
    }

    private static int getDefaultLogLevelInt(Class<?> simpleLoggerClass) throws ReflectiveOperationException {
        Field configParamsField = simpleLoggerClass.getDeclaredField("CONFIG_PARAMS");
        configParamsField.setAccessible(true);
        Object configParams = configParamsField.get(null);
        Field defaultLogLevelField = configParams.getClass().getDeclaredField("defaultLogLevel");
        defaultLogLevelField.setAccessible(true);
        return (int) defaultLogLevelField.get(configParams);
    }

    public static Level getDefaultLogLevel() {
        Logger rootLogger = LoggerFactory.getLogger(PMD_ROOT_LOGGER);

        // check the lowest log level first
        if (rootLogger.isTraceEnabled()) {
            return Level.TRACE;
        }
        if (rootLogger.isDebugEnabled()) {
            return Level.DEBUG;
        }
        if (rootLogger.isInfoEnabled()) {
            return Level.INFO;
        }
        if (rootLogger.isWarnEnabled()) {
            return Level.WARN;
        }
        if (rootLogger.isErrorEnabled()) {
            return Level.ERROR;
        }

        return Level.INFO;
    }

    public static void disableLogging(Class<?> clazz) {
        if (!isSimpleLogger()) {
            // do nothing, not even set system properties, if not Simple Logger is in use
            return;
        }

        System.setProperty("org.slf4j.simpleLogger.log." + clazz.getName(), "off");
    }

    public static boolean isSimpleLogger() {
        try {
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            Class<?> loggerFactoryClass = loggerFactory.getClass().getClassLoader().loadClass(SIMPLE_LOGGER_FACTORY_CLASS);
            return loggerFactoryClass.isAssignableFrom(loggerFactory.getClass());
        } catch (ClassNotFoundException e) {
            // not slf4j simple logger
            return false;
        }
    }

    public static void installJulBridge() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger(); // removes any existing ConsoleLogger
            SLF4JBridgeHandler.install();
        }
    }
}

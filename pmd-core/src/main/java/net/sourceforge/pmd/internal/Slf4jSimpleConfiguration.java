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
import org.slf4j.PmdLoggerFactoryFriend;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.event.Level;

public final class Slf4jSimpleConfiguration {
    private static final String SIMPLE_LOGGER_FACTORY_CLASS = "org.slf4j.impl.SimpleLoggerFactory";
    private static final String SIMPLE_LOGGER_CLASS = "org.slf4j.impl.SimpleLogger";
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
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        Class<? extends ILoggerFactory> loggerFactoryClass = loggerFactory.getClass();
        try {
            Class<?> simpleLoggerClass = loggerFactoryClass.getClassLoader().loadClass(SIMPLE_LOGGER_CLASS);
            Method initMethod = simpleLoggerClass.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(null);

            int newLogLevel = getDefaultLogLevelInt(simpleLoggerClass);

            Field currentLogLevelField = simpleLoggerClass.getDeclaredField("currentLogLevel");
            currentLogLevelField.setAccessible(true);

            // Change the logging level of loggers that were already created.
            // For this we fetch the map of name to logger that is stored in the logger factory,
            // then set the log level field of each logger via reflection.
            Field loggerMapField = loggerFactoryClass.getDeclaredField("loggerMap");
            loggerMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Logger> loggerMap = (Map<String, Logger>) loggerMapField.get(loggerFactory);
            for (Logger logger : loggerMap.values()) {
                if (logger.getName().startsWith(PMD_ROOT_LOGGER)
                    && SIMPLE_LOGGER_CLASS.equals(logger.getClass().getName())) {
                    currentLogLevelField.set(logger, newLogLevel);
                }
            }
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while initializing logging: " + ex);
        }

        PmdLoggerFactoryFriend.reset();
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
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        return SIMPLE_LOGGER_FACTORY_CLASS.equals(loggerFactory.getClass().getName());
    }

    public static void installJulBridge() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger(); // removes any existing ConsoleLogger
            SLF4JBridgeHandler.install();
        }
    }
}

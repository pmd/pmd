/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.lang.reflect.Method;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.PmdLoggerFactoryFriend;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.event.Level;

public final class Slf4jSimpleConfiguration {
    private static final String SIMPLE_LOGGER_FACTORY_CLASS = "org.slf4j.impl.SimpleLoggerFactory";
    private static final String SIMPLE_LOGGER_CLASS = "org.slf4j.impl.SimpleLogger";

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

            // Call SimpleLoggerFactory.reset() by reflection.
            Method resetMethod = loggerFactoryClass.getDeclaredMethod("reset");
            resetMethod.setAccessible(true);
            resetMethod.invoke(loggerFactory);
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while initializing logging: " + ex);
        }

        PmdLoggerFactoryFriend.reset();
    }

    public static Level getDefaultLogLevel() {
        Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

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

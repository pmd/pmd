/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.lang.reflect.Method;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.event.Level;

public final class Slf4jSimpleConfiguration {
    private Slf4jSimpleConfiguration() { }

    public static void reconfigureDefaultLogLevel(Level level) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", level.toString());

        // Call SimpleLogger.init() by reflection.
        // Alternatively: move the CLI related classes into an own module, add
        // slf4j-simple as a compile dependency and create a PmdSlf4jSimpleFriend class in
        // the package org.slf4j.simple to gain access to this package-private init method.
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        Class<? extends ILoggerFactory> loggerFactoryClass = loggerFactory.getClass();
        try {
            Class<?> simpleLoggerClass = loggerFactoryClass.getClassLoader().loadClass("org.slf4j.simple.SimpleLogger");
            Method initMethod = simpleLoggerClass.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(null);
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while initializing logging: " + ex);
        }

        LoggerFactoryFriend.reset();
    }

    public static void disableLogging(Class<?> clazz) {
        System.setProperty("org.slf4j.simpleLogger.log." + clazz.getName(), "off");
    }

    public static void installJulBridge() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.install();
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * @author Clément Fournier
 */
public final class PmdRootLogger {

    private static final String PMD_PACKAGE = "net.sourceforge.pmd";
    // not final, in order to re-initialize logging
    public static Logger log = LoggerFactory.getLogger(PMD_PACKAGE);

    private PmdRootLogger() {
        // utility class
    }

    public static <C extends AbstractConfiguration, R> R executeInLoggingContext(C conf, Function<C, R> runnable) {
        Level curLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        boolean resetLogLevel = false;
        try {
            // only reconfigure logging, if debug flag was used on command line
            // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
            if (conf.isDebug()) {
                Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
                // need to reload the logger with the new configuration
                log = LoggerFactory.getLogger(PMD_PACKAGE);
                resetLogLevel = true;
            }

            MessageReporter pmdReporter = setupMessageReporter();
            conf.setReporter(pmdReporter);
            return runnable.apply(conf);
        } finally {
            if (resetLogLevel) {
                // reset to the previous value
                Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(curLogLevel);
                log = LoggerFactory.getLogger(PMD_PACKAGE);
            }
        }
    }

    private static @NonNull MessageReporter setupMessageReporter() {

        // create a top-level reporter
        // TODO CLI errors should also be reported through this
        // TODO this should not use the logger as backend, otherwise without
        //  slf4j implementation binding, errors are entirely ignored.
        MessageReporter pmdReporter = new SimpleMessageReporter(log);
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        log.info("Log level is at {}", defaultLogLevel);
        return pmdReporter;
    }
}

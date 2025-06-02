/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * Interacts with slf4j-simple to reconfigure logging levels based on
 * the debug flag.
 *
 * @author Clément Fournier
 */
public final class PmdRootLogger {

    private static final String PMD_CLI_LOGGER = "net.sourceforge.pmd.cli";
    // not final, in order to re-initialize logging
    // This logger is used as backend for the MessageReporter currently.
    private static Logger log = LoggerFactory.getLogger(PMD_CLI_LOGGER);

    private PmdRootLogger() {
        // utility class
    }

    public static <C extends AbstractConfiguration, R> R executeInLoggingContext(C conf, boolean isDebug, Function<C, R> runnable) {
        Level curLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        boolean resetLogLevel = false;
        try {
            // only reconfigure logging, if debug flag was used on command line
            // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
            if (isDebug) {
                Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
                // need to reload the logger with the new configuration
                log = LoggerFactory.getLogger(PMD_CLI_LOGGER);
                resetLogLevel = true;

                // logging, mostly for testing purposes
                Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
                log.debug("Log level is at {}", defaultLogLevel);
            }

            PmdReporter pmdReporter = setupMessageReporter();
            conf.setReporter(pmdReporter);
            return runnable.apply(conf);
        } finally {
            if (resetLogLevel) {
                // reset to the previous value
                Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(curLogLevel);
                log = LoggerFactory.getLogger(PMD_CLI_LOGGER);
            }
        }
    }

    private static @NonNull PmdReporter setupMessageReporter() {
        // Note: This implementation uses slf4j as the backend. If PMD is integrated into an application
        // a slf4j implementation binding must be provided to see any loggings (even errors).
        // In pmd-cli, we use slf4j-simple.

        // create a top-level reporter
        PmdReporter pmdReporter = new SimpleMessageReporter(log);
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        return pmdReporter;
    }
}

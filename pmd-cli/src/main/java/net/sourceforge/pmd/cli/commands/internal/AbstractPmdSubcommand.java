/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.util.concurrent.Callable;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

public abstract class AbstractPmdSubcommand implements Callable<Integer> {

    @Spec
    protected CommandSpec spec; // injected by PicoCli, needed for validations

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show this help message and exit.")
    protected boolean helpRequested;

    @Option(names = { "--debug", "--verbose", "-D", "-v" }, description = "Debug mode.")
    protected boolean debug;

    @Override
    public final Integer call() throws Exception {
        setupCliLogger();
        validate();
        return execute().getExitCode();
    }

    /**
     * Extension point to validate provided configuration.
     * 
     * Implementations must throw {@code ParameterException} upon a violation.
     * 
     * @throws ParameterException
     */
    protected void validate() throws ParameterException {
        // no-op, children may override
    }

    protected abstract CliExitCode execute();

    private void setupCliLogger() {
        // only reconfigure logging, if debug flag was used on command line
        // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
        if (debug) {
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
        }

        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();

        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        LoggerFactory.getLogger(AbstractPmdSubcommand.class).info("Log level is at {}", defaultLogLevel);
    }
}

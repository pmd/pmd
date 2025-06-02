/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.util.concurrent.Callable;

import net.sourceforge.pmd.cli.internal.CliExitCode;

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

}

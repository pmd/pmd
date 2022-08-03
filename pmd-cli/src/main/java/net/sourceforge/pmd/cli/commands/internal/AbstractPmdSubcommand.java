package net.sourceforge.pmd.cli.commands.internal;

import java.util.concurrent.Callable;

import net.sourceforge.pmd.cli.internal.ExecutionResult;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

public abstract class AbstractPmdSubcommand implements Callable<Integer> {

    @Spec
    protected CommandSpec spec; // injected by PicoCli, needed for validations

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show this help message and exit.")
    protected boolean helpRequested;

    @Override
    public final Integer call() throws Exception {
        return execute().getExitCode();
    }

    protected abstract ExecutionResult execute();
}

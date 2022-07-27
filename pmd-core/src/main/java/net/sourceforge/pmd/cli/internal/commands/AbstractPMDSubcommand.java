package net.sourceforge.pmd.cli.internal.commands;

import java.util.concurrent.Callable;

import net.sourceforge.pmd.cli.internal.ExecutionResult;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

public abstract class AbstractPMDSubcommand implements Callable<Integer> {

    @Spec
    protected CommandSpec spec; // injected by PicoCli, needed for validations

    @Override
    public final Integer call() throws Exception {
        return execute().getExitCode();
    }

    protected abstract ExecutionResult execute();
}

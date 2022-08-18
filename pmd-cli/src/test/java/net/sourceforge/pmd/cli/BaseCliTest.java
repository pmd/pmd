package net.sourceforge.pmd.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.cli.internal.ExecutionResult;

import com.github.stefanbirkner.systemlambda.SystemLambda;

abstract class BaseCliTest {

    protected String runCliSuccessfully(String... args) throws Exception {
        return SystemLambda.tapSystemErrAndOut(() -> {
            runCli(ExecutionResult.OK, args);
        });
    }

    protected void runCli(ExecutionResult expectedExitCode, String... args) throws Exception {
        final List<String> argList = new ArrayList<>();
        argList.addAll(cliStandardArgs());
        argList.addAll(Arrays.asList(args));
        
        final int actualExitCode = SystemLambda.catchSystemExit(() -> {
            PmdCli.main(argList.toArray(new String[0]));
        });
        assertEquals(expectedExitCode.getExitCode(), actualExitCode, "Exit code");
    }

    abstract protected List<String> cliStandardArgs();
}
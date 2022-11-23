/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import net.sourceforge.pmd.cli.internal.ExecutionResult;

import com.github.stefanbirkner.systemlambda.SystemLambda;

abstract class BaseCliTest {

    @BeforeAll
    static void disablePicocliAnsi() {
        System.setProperty("picocli.ansi", "false");
    }
    
    @AfterAll
    static void resetPicocliAnsi() {
        System.clearProperty("picocli.ansi");
    }
    
    protected String runCliSuccessfully(String... args) throws Exception {
        return runCli(ExecutionResult.OK, args);
    }

    protected String runCli(ExecutionResult expectedExitCode, String... args) throws Exception {
        final List<String> argList = new ArrayList<>();
        argList.addAll(cliStandardArgs());
        argList.addAll(Arrays.asList(args));
        
        return SystemLambda.tapSystemErrAndOut(() -> {
            final int actualExitCode = SystemLambda.catchSystemExit(() -> {
                PmdCli.main(argList.toArray(new String[0]));
            });
            assertEquals(expectedExitCode.getExitCode(), actualExitCode, "Exit code");
        });
    }

    protected abstract List<String> cliStandardArgs();
}

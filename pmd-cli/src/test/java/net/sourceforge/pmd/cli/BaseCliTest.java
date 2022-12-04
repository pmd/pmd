/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.opentest4j.AssertionFailedError;

import net.sourceforge.pmd.cli.internal.CliExitCode;

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

    protected CliExecutionResult runCliSuccessfully(String... args) throws Exception {
        return runCli(CliExitCode.OK, args);
    }

    protected CliExecutionResult runCli(CliExitCode expectedExitCode, String... args) throws Exception {
        final List<String> argList = new ArrayList<>();
        argList.addAll(cliStandardArgs());
        argList.addAll(Arrays.asList(args));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        final PrintStream formerOut = System.out;
        final PrintStream formerErr = System.err;

        CliExitCode exitCode;
        try {
            System.out.println("running: pmd " + String.join(" ", argList));
            System.setOut(new PrintStream(out));
            System.setErr(new PrintStream(err));
            int actualExitCode = SystemLambda.catchSystemExit(
                // restoring system properties: --debug might change logging properties
                () -> SystemLambda.restoreSystemProperties(
                    () -> PmdCli.main(argList.toArray(new String[0]))
                )
            );
            exitCode = CliExitCode.fromInt(actualExitCode);

        } finally {
            System.setOut(formerOut);
            System.setErr(formerErr);
        }

        return new CliExecutionResult(
            out, err, exitCode
        ).verify(e -> assertEquals(expectedExitCode, e.exitCode));
    }

    protected abstract List<String> cliStandardArgs();


    public static Matcher<String> containsPattern(final String regex) {
        return new BaseMatcher<String>() {
            final Pattern pattern = Pattern.compile(regex);

            @Override
            public void describeTo(Description description) {
                description.appendText("a string containing the pattern '" + this.pattern + "'");
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof String && pattern.matcher((String) o).find();
            }
        };
    }

    public static Matcher<String> containsStringNTimes(final int times, final String substring) {
        return new BaseMatcher<String>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("a string containing " + times + " times the substring '" + substring + "'");
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof String
                    && StringUtils.countMatches((String) o, substring) == times;
            }
        };
    }


    static class CliExecutionResult {

        private final ByteArrayOutputStream out;
        private final ByteArrayOutputStream err;
        private final CliExitCode exitCode;

        CliExecutionResult(ByteArrayOutputStream out,
                           ByteArrayOutputStream err,
                           CliExitCode exitCode) {
            this.out = out;
            this.err = err;
            this.exitCode = exitCode;
        }

        public String getOut() {
            return out.toString();
        }

        public String getErr() {
            return err.toString();
        }


        public void checkOk() {
            assertEquals(CliExitCode.OK, exitCode);
        }

        public void checkFailed() {
            assertEquals(CliExitCode.ERROR, exitCode);
        }

        public void checkNoErrorOutput() {
            checkStdErr(equalTo(""));
        }

        public void checkStdOut(Matcher<? super String> matcher) {
            assertThat(getOut(), matcher);
        }

        public void checkStdErr(Matcher<? super String> matcher) {
            assertThat(getErr(), matcher);
        }

        /**
         * Use this method to wrap assertions.
         */
        public CliExecutionResult verify(ThrowingConsumer<CliExecutionResult> actions) {
            try {
                actions.accept(this);
            } catch (Throwable e) {
                System.out.println("TEST FAILED");
                System.out.println("> Return code: " + exitCode);
                System.out.println("> Standard output -------------------------");
                System.err.println(out.toString());
                System.err.flush();
                System.out.println("> Standard error --------------------------");
                System.err.println(err.toString());
                System.err.flush();
                System.out.println("> -----------------------------------------");

                if (e instanceof Exception) {
                    throw new AssertionFailedError("Expected no exception to be thrown", e);
                }
                throw (Error) e;
            }
            return this;
        }
    }

}

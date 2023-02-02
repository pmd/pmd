/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static net.sourceforge.pmd.cli.internal.CliExitCode.OK;
import static net.sourceforge.pmd.cli.internal.CliExitCode.VIOLATIONS_FOUND;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import org.junit.jupiter.api.Test;


class ForceLanguageCliTest extends BaseCliTest {

    private static final String BASE_DIR = "src/test/resources/net/sourceforge/pmd/cli/forceLanguage/";
    private static final String RULE_MESSAGE = "Violation from ReportAllRootNodes";

    @Override
    protected List<String> cliStandardArgs() {
        return listOf(
            "check",
            "--no-cache",
            "-f", "text",
            "-R", PmdCliTest.RULESET_WITH_VIOLATION
        );
    }

    @Test
    void analyzeSingleXmlWithoutForceLanguage() throws Exception {
        runCli(OK, "-d", BASE_DIR + "src/file1.ext")
            .verify(r -> r.checkStdOut(containsStringNTimes(0, RULE_MESSAGE)));
    }

    @Test
    void analyzeSingleXmlWithForceLanguage() throws Exception {
        runCli(VIOLATIONS_FOUND, "-d", BASE_DIR + "src/file1.ext", "--force-language", "dummy")
            .verify(r -> r.checkStdOut(containsStringNTimes(1, RULE_MESSAGE)));
    }

    @Test
    void analyzeDirectoryWithForceLanguage() throws Exception {
        runCli(VIOLATIONS_FOUND, "-d", BASE_DIR + "src/", "--force-language", "dummy")
            .verify(r -> r.checkStdOut(containsStringNTimes(3, RULE_MESSAGE)));
    }

}

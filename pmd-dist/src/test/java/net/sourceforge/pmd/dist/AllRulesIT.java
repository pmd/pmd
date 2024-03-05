/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AllRulesIT extends AbstractBinaryDistributionTest {


    static Iterable<String> languagesToTest() {
        // note: scala and wsdl have no rules
        return Arrays.asList("java", "apex", "html", "javascript", "jsp", "modelica",
                "plsql", "pom", "visualforce", "velocity", "xml", "xsl");
    }

    @ParameterizedTest
    @MethodSource("languagesToTest")
    void runRuleTests(String language) throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source/" + language + "/").getAbsolutePath();

        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir,
                "src/test/resources/rulesets/all-" + language + ".xml");
        assertDefaultExecutionResult(result);
    }

    private static void assertDefaultExecutionResult(ExecutionResult result) {
        result.assertExitCode(4)
              .assertStdOut(containsString(""));

        result.assertNoError("Exception applying rule");
        result.assertNoError("Ruleset not found");
        result.assertNoError("Use of deprecated attribute");
        result.assertNoError("instead of the deprecated"); // rule deprecations
        result.assertNoErrorInReport("Error while processing");
        result.assertNoErrorInReport("Error while parsing");

        // See bug #2092: [apex] ApexLexer logs visible when Apex is the selected language upon starting the designer
        result.assertNoError("Deduped array ApexLexer");
    }
}

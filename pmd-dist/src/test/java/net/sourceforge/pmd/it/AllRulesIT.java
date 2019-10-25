/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AllRulesIT extends AbstractBinaryDistributionTest {

    @Parameter
    public String language;

    @Parameters
    public static Iterable<String> languagesToTest() {
        // note: scala and wsdl have no rules
        return Arrays.asList("java", "apex", "javascript", "jsp", "plsql", "pom", "visualforce", "velocitytemplate", "xml", "xsl");
    }

    @Test
    public void runRuleTests() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source/" + language + "/").getAbsolutePath();

        ExecutionResult result = PMDExecutor.runPMDRules(tempDir, srcDir, "src/test/resources/rulesets/all-"
                + language + ".xml");
        assertDefaultExecutionResult(result);
    }

    private static void assertDefaultExecutionResult(ExecutionResult result) {
        result.assertExecutionResult(4, "");

        result.assertNoError("Exception applying rule");
        result.assertNoError("Ruleset not found");
        result.assertNoError("Use of deprecated attribute");
        result.assertNoErrorInReport("Error while processing");
        result.assertNoErrorInReport("Error while parsing");
    }
}

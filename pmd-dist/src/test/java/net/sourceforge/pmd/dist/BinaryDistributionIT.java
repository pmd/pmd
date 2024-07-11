/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDVersion;

class BinaryDistributionIT extends AbstractBinaryDistributionTest {

    private static final List<String> SUPPORTED_LANGUAGES_CPD = listOf(
        "apex", "coco", "cpp", "cs", "dart", "ecmascript",
        "fortran", "gherkin", "go", "groovy", "html", "java", "jsp",
        "julia",
        "kotlin", "lua", "matlab", "modelica", "objectivec", "perl",
        "php", "plsql", "pom", "python", "ruby", "scala", "swift",
        "tsql", "typescript", "velocity", "visualforce", "wsdl", "xml", "xsl"
    );

    private static final List<String> SUPPORTED_LANGUAGES_PMD = listOf(
        "apex-52", "apex-53", "apex-54", "apex-55",
        "apex-56", "apex-57", "apex-58", "apex-59",
        "apex-60",
        "ecmascript-3", "ecmascript-5",
        "ecmascript-6", "ecmascript-7", "ecmascript-8",
        "ecmascript-9", "ecmascript-ES2015",
        "ecmascript-ES2016", "ecmascript-ES2017",
        "ecmascript-ES2018", "ecmascript-ES6", "html-4",
        "html-5", "java-1.10", "java-1.3", "java-1.4", "java-1.5",
        "java-1.6", "java-1.7", "java-1.8", "java-1.9", "java-10",
        "java-11", "java-12", "java-13", "java-14", "java-15",
        "java-16", "java-17", "java-18", "java-19",
        "java-20",
        "java-21",
        "java-22", "java-22-preview",
        "java-23", "java-23-preview",
        "java-5", "java-6", "java-7",
        "java-8", "java-9", "jsp-2", "jsp-3", "kotlin-1.6",
        "kotlin-1.7", "kotlin-1.8", "modelica-3.4", "modelica-3.5",
        "plsql-11g", "plsql-12.1", "plsql-12.2",
        "plsql-12c_Release_1", "plsql-12c_Release_2",
        "plsql-18c", "plsql-19c", "plsql-21c", "pom-4.0.0",
        "scala-2.10", "scala-2.11", "scala-2.12", "scala-2.13",
        "swift-4.2", "swift-5.0", "swift-5.1", "swift-5.2",
        "swift-5.3", "swift-5.4", "swift-5.5", "swift-5.6",
        "swift-5.7", "swift-5.8", "swift-5.9",
        "velocity-2.0", "velocity-2.1", "velocity-2.2", "velocity-2.3",
        "visualforce-52", "visualforce-53", "visualforce-54", "visualforce-55", "visualforce-56",
        "visualforce-57", "visualforce-58", "visualforce-59",
        "visualforce-60",
        "wsdl-1.1", "wsdl-2.0",
        "xml-1.0", "xml-1.1",
        "xsl-1.0", "xsl-2.0", "xsl-3.0"
    );

    private final String srcDir = new File(".", "src/test/resources/sample-source/java/").getAbsolutePath();

    private static Pattern toListPattern(List<String> items) {
        String pattern = items.stream().map(Pattern::quote)
                              .collect(Collectors.joining(",", ".*Validvalues:", ".*"));
        return Pattern.compile(pattern, Pattern.DOTALL);
    }

    @Test
    void testFileExistence() {
        assertTrue(getBinaryDistribution().exists());
    }

    private Set<String> getExpectedFileNames() {
        Set<String> result = new HashSet<>();
        String basedir = "pmd-bin-" + PMDVersion.VERSION + "/";
        result.add(basedir);
        result.add(basedir + "LICENSE");
        result.add(basedir + "bin/pmd");
        result.add(basedir + "bin/pmd.bat");
        result.add(basedir + "conf/simplelogger.properties");
        result.add(basedir + "lib/pmd-core-" + PMDVersion.VERSION + ".jar");
        result.add(basedir + "lib/pmd-java-" + PMDVersion.VERSION + ".jar");
        result.add(basedir + "sbom/pmd-" + PMDVersion.VERSION + "-cyclonedx.xml");
        result.add(basedir + "sbom/pmd-" + PMDVersion.VERSION + "-cyclonedx.json");
        return result;
    }

    @Test
    void testZipFileContent() throws IOException {
        Set<String> expectedFileNames = getExpectedFileNames();

        ZipFile zip = new ZipFile(getBinaryDistribution());

        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            expectedFileNames.remove(entry.getName());
        }

        zip.close();

        if (!expectedFileNames.isEmpty()) {
            fail("Missing files in archive: " + expectedFileNames);
        }
    }

    @Test
    void testPmdJavaQuickstart() throws Exception {
        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir, "rulesets/java/quickstart.xml");
        result.assertExitCode(4)
              .assertStdOut(containsString(""));
    }

    @Test
    void testPmdXmlFormat() throws Exception {
        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir, "src/test/resources/rulesets/sample-ruleset.xml", "xml");
        result.assertExitCode(4).assertReport(containsString("JumbledIncrementer.java\">"));
        result.assertExitCode(4).assertReport(containsString("<violation beginline=\"8\" endline=\"10\" begincolumn=\"13\" endcolumn=\"14\" rule=\"JumbledIncrementer\""));
    }

    @Test
    void testPmdSample() throws Exception {
        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir, "src/test/resources/rulesets/sample-ruleset.xml");
        result.assertExitCode(4).assertReport(containsString("JumbledIncrementer.java:8:"));
    }

    @Test
    void testPmdSampleWithZippedSources() throws Exception {
        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir + "/sample-source-java.zip",
                "src/test/resources/rulesets/sample-ruleset.xml");
        result.assertExitCode(4).assertReport(containsString("JumbledIncrementer.java:8:"));
    }

    @Test
    void testPmdSampleWithJarredSources() throws Exception {
        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir + "/sample-source-java.jar",
                "src/test/resources/rulesets/sample-ruleset.xml");
        result.assertExitCode(4).assertReport(containsString("JumbledIncrementer.java:8:"));
    }

    @Test
    void testPmdHelp() throws Exception {
        ExecutionResult result = PMDExecutor.runPMD(null, tempDir, "-h");
        result.assertExitCode(0);
        String output = result.getOutput().replaceAll("\\s+|\r|\n", "");
        assertThat(output, matchesRegex(toListPattern(SUPPORTED_LANGUAGES_PMD)));
    }

    @Test
    void testPmdNoArgs() throws Exception {
        ExecutionResult result = PMDExecutor.runPMD(null, tempDir); // without any argument, display usage help and error

        result.assertExitCode(2).assertStdErr(containsString("Usage: pmd check "));
    }

    @Test
    void logging() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source/java/").getAbsolutePath();

        ExecutionResult result;

        result = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", srcDir, "-R", "src/test/resources/rulesets/sample-ruleset.xml");
        result.assertExitCode(4);
        result.assertNoErrorInReport("[DEBUG] Log level is at TRACE");


        // now with debug
        result = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", srcDir, "-R", "src/test/resources/rulesets/sample-ruleset.xml", "--debug");
        result.assertExitCode(4);
        result.assertErrorOutputContains("[DEBUG] Log level is at TRACE");
    }

    @Test
    void runPMDWithError() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source/unparsable/").getAbsolutePath();

        ExecutionResult result = PMDExecutor.runPMDRules(createTemporaryReportFile(), tempDir, srcDir, "src/test/resources/rulesets/sample-ruleset.xml");

        result.assertExitCode(5).assertStdErr(containsString("Run in verbose mode to see a stack-trace."));
    }

    @Test
    void runCPD() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source-cpd/").getAbsolutePath();

        ExecutionResult result;

        result = CpdExecutor.runCpd(tempDir); // without any argument, display usage help and error

        result.assertExitCode(2).assertStdErr(containsString("Usage: pmd cpd "));

        result = CpdExecutor.runCpd(tempDir, "-h");
        result.assertExitCode(0);
        String output = result.getOutput().replaceAll("\\s+|\r|\n", "");
        assertThat(output, matchesRegex(toListPattern(SUPPORTED_LANGUAGES_CPD)));

        result = CpdExecutor.runCpd(tempDir, "--minimum-tokens", "10", "--format", "text", "--dir", srcDir);
        result.assertExitCode(4)
              .assertStdOut(containsString("Found a 10 line (55 tokens) duplication in the following files:"));
        result.assertExitCode(4)
              .assertStdOut(containsString("Class1.java"));
        result.assertExitCode(4)
              .assertStdOut(containsString("Class2.java"));

        result = CpdExecutor.runCpd(tempDir, "--minimum-tokens", "10", "--format", "xml", "--dir", srcDir);
        result.assertExitCode(4)
              .assertStdOut(containsString("<duplication lines=\"10\" tokens=\"55\">"));
        result.assertExitCode(4)
              .assertStdOut(containsString("Class1.java\"/>"));
        result.assertExitCode(4)
              .assertStdOut(containsString("Class2.java\"/>"));

        result = CpdExecutor.runCpd(tempDir, "--minimum-tokens", "1000", "--format", "text", "--dir", srcDir);
        result.assertExitCode(0);
    }

    @Test
    void runAstDump() throws Exception {
        File jumbledIncrementerSrc = new File(srcDir, "JumbledIncrementer.java");
        List<String> args = listOf("--format", "xml", "--language", "java", "--file", jumbledIncrementerSrc.toString());
        ExecutionResult result = PMDExecutor.runCommand(tempDir, "ast-dump", args);
        result.assertExitCode(0);
    }

}

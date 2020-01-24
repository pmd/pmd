/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;

import net.sourceforge.pmd.PMDVersion;

public class BinaryDistributionIT extends AbstractBinaryDistributionTest {

    @Test
    public void testFileExistence() {
        assertTrue(getBinaryDistribution().exists());
    }

    private Set<String> getExpectedFileNames() {
        Set<String> result = new HashSet<>();
        String basedir = "pmd-bin-" + PMDVersion.VERSION + "/";
        result.add(basedir);
        result.add(basedir + "LICENSE");
        result.add(basedir + "bin/run.sh");
        result.add(basedir + "bin/pmd.bat");
        result.add(basedir + "bin/cpd.bat");
        result.add(basedir + "lib/pmd-core-" + PMDVersion.VERSION + ".jar");
        result.add(basedir + "lib/pmd-java-" + PMDVersion.VERSION + ".jar");
        return result;
    }

    @Test
    public void testZipFileContent() throws IOException {
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
    public void runPMD() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source/java/").getAbsolutePath();

        ExecutionResult result;

        result = PMDExecutor.runPMD(tempDir, "-h");
        result.assertExecutionResult(0, "apex, ecmascript, java, jsp, modelica, plsql, pom, scala, vf, vm, wsdl, xml, xsl");

        result = PMDExecutor.runPMDRules(tempDir, srcDir, "src/test/resources/rulesets/sample-ruleset.xml");
        result.assertExecutionResult(4, "", "JumbledIncrementer.java:8:");

        result = PMDExecutor.runPMDRules(tempDir, srcDir, "rulesets/java/quickstart.xml");
        result.assertExecutionResult(4, "");
    }

    @Test
    public void runCPD() throws Exception {
        String srcDir = new File(".", "src/test/resources/sample-source-cpd/").getAbsolutePath();

        ExecutionResult result;

        result = CpdExecutor.runCpd(tempDir, "-h");
        result.assertExecutionResult(0, "Supported languages: [apex, cpp, cs, dart, ecmascript, fortran, go, groovy, java, jsp, kotlin, lua, matlab, modelica, objectivec, perl, php, plsql, python, ruby, scala, swift, vf]");

        result = CpdExecutor.runCpd(tempDir, "--minimum-tokens", "10", "--format", "text", "--files", srcDir);
        result.assertExecutionResult(4, "Found a 10 line (55 tokens) duplication in the following files:");
        result.assertExecutionResult(4, "Class1.java");
        result.assertExecutionResult(4, "Class2.java");

        result = CpdExecutor.runCpd(tempDir, "--minimum-tokens", "1000", "--format", "text", "--files", srcDir);
        result.assertExecutionResult(0, "");
    }
}

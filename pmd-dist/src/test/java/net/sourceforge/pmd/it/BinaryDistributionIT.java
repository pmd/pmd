/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class BinaryDistributionIT {

    private File getBinaryDistribution() {
        return new File(".", "target/pmd-bin-" + PMD.VERSION + ".zip");
    }

    @Test
    public void testFileExistence() {
        assertTrue(getBinaryDistribution().exists());
    }

    private Set<String> getExpectedFileNames() {
        Set<String> result = new HashSet<>();
        String basedir = "pmd-bin-" + PMD.VERSION + "/";
        result.add(basedir);
        result.add(basedir + "bin/run.sh");
        result.add(basedir + "bin/pmd.bat");
        result.add(basedir + "bin/cpd.bat");
        result.add(basedir + "lib/pmd-core-" + PMD.VERSION + ".jar");
        result.add(basedir + "lib/pmd-java-" + PMD.VERSION + ".jar");
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

        assertTrue(expectedFileNames.isEmpty());
    }

    @Test
    public void runPMD() throws Exception {
        Path tempDir = Files.createTempDirectory("pmd-it-test-");
        String srcDir = new File(".", "src/test/resources/sample-source/").getAbsolutePath();

        try {
            ZipFileExtractor.extractZipFile(getBinaryDistribution().toPath(), tempDir);
            PMDExecutionResult result;

            result = PMDExecutor.runPMD(tempDir, "-h");
            result.assertPMDExecutionResult(1, "java, jsp, wsdl, xsl, xml, vm, ecmascript, plsql");

            result = PMDExecutor.runPMDRules(tempDir, srcDir, "java-basic");
            result.assertPMDExecutionResult(4, "JumbledIncrementer.java:8:");

            result = PMDExecutor.runPMDRules(tempDir, srcDir, "java-design");
            result.assertPMDExecutionResult(0, "");
        } finally {
            FileUtils.forceDelete(tempDir.toFile());
        }
    }
}

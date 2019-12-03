/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class GreedyStrategyTest {
    @Test
    public void textRetentionTest() throws Exception {
        SCMConfiguration configuration = new SCMConfiguration();
        Path inputFile = Files.createTempFile("pmd-test-", ".tmp");
        Path outputFile = Files.createTempFile("pmd-test", ".tmp");
        Files.copy(getClass().getResourceAsStream("cutter-test-input.txt"), inputFile, StandardCopyOption.REPLACE_EXISTING);
        String cmdline;
        if (SystemUtils.IS_OS_WINDOWS) {
            cmdline = "type " + outputFile.toString();
        } else {
            cmdline = "/bin/cat " + outputFile.toString();
        }
        String[] args = {
            "--language", "java", "--input-file", inputFile.toString(), "--output-file", outputFile.toString(),
            "--invariant", "message", "--printed-message", "testRemoval", "--command-line", cmdline,
            "--strategy", "greedy",
        };
        configuration.parse(args);
        Assert.assertNull(configuration.getErrorString());
        SourceCodeMinimizer minimizer = new SourceCodeMinimizer(configuration);
        minimizer.runMinimization();
        Helper.assertResultedSourceEquals(StandardCharsets.UTF_8, getClass().getResource("cutter-test-retained-testRemoval.txt"), outputFile);
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class AnalysisCacheIT extends AbstractBinaryDistributionTest {

    private final String srcDir = new File(".", "src/test/resources/sample-source/java/").getAbsolutePath();

    @Test
    void testPmdCachedResultMatches() throws Exception {
        final Path cacheFile = createTemporaryReportFile();
        
        ExecutionResult result = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", srcDir, "-R", "src/test/resources/rulesets/sample-ruleset.xml",
                "-f", "text", "--cache", cacheFile.toAbsolutePath().toString(), "--no-progress");
        
        // Ensure we have violations and a non-empty cache file
        assertTrue(cacheFile.toFile().length() > 0, "cache file is empty after run");
        result.assertExitCode(4).assertReport(containsString(srcDir + File.separator + "JumbledIncrementer.java:8:\tJumbledIncrementer:\t"));
        
        // rerun from cache
        ExecutionResult resultFromCache = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", srcDir, "-R", "src/test/resources/rulesets/sample-ruleset.xml",
                "-f", "text", "--cache", cacheFile.toAbsolutePath().toString(), "--no-progress", "-v");

        // expect identical
        result.assertIdenticalResults(resultFromCache);
        resultFromCache.assertErrorOutputContains("Incremental Analysis cache HIT");
    }
    
    @Test
    void testPmdCachedResultsAreRelativized() throws Exception {
        final Path cacheFile = createTemporaryReportFile();
        
        ExecutionResult result = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", srcDir, "-R", "src/test/resources/rulesets/sample-ruleset.xml",
                "-f", "text", "--cache", cacheFile.toAbsolutePath().toString(), "--no-progress");
        
        // Ensure we have violations and a non-empty cache file
        assertTrue(cacheFile.toFile().length() > 0, "cache file is empty after run");
        result.assertExitCode(4)
              .assertReport(containsString(srcDir + File.separator + "JumbledIncrementer.java:8:\tJumbledIncrementer:\t"));
        
        // rerun from cache with relativized paths
        ExecutionResult resultFromCache = PMDExecutor.runPMD(createTemporaryReportFile(), tempDir, "-d", Paths.get(".").toAbsolutePath().relativize(Paths.get(srcDir)).toString(), "-R", "src/test/resources/rulesets/sample-ruleset.xml",
                "-f", "text", "--cache", cacheFile.toAbsolutePath().toString(), "--no-progress", "-v");
        
        resultFromCache.assertErrorOutputContains("Incremental Analysis cache HIT");

        // An error with the relative path should exist, but no with the absolute one
        result.assertExitCode(4)
              .assertReport(containsString("src/test/resources/sample-source/java/JumbledIncrementer.java:8:\tJumbledIncrementer:\t".replace('/', File.separatorChar)));
        resultFromCache.assertNoErrorInReport(srcDir + File.separator + "JumbledIncrementer.java:8:\tJumbledIncrementer:\t");
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.test.util.JavaUtilLoggingRule;

/**
 *
 */
public class ApexMultifileAnalysisTest {

    @Rule
    public final JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(ApexMultifileAnalysis.LOG);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNoSfdxProjectJsonProducesFailedAnalysis() {
        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertTrue(analysisInstance.isFailed());
        assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        loggingRule.assertContainsIgnoringCase("Missing project file");
    }

    @Test
    public void testMalformedSfdxProjectJsonProducesFailedAnalysis() throws IOException {
        copyResource("malformedSfdxFile.json", "sfdx-project.json");

        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertTrue(analysisInstance.isFailed());
        assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        loggingRule.assertContainsIgnoringCase("error: 'path' is required for all 'packageDirectories' elements");
    }

    @Test
    public void testWellFormedSfdxProjectJsonProducesFunctionalAnalysis() throws IOException {
        copyResource("correctSfdxFile.json", "sfdx-project.json");

        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertFalse(analysisInstance.isFailed());
        loggingRule.assertEmpty();
    }

    private @NonNull ApexMultifileAnalysis getAnalysisForTempFolder() {
        return ApexMultifileAnalysis.getAnalysisInstance(tempFolder.getRoot().getAbsolutePath());
    }

    private void copyResource(String resourcePath, String relativePathInTempDir) throws IOException {
        File file = tempFolder.newFile(relativePathInTempDir);
        String fileContents = IOUtils.toString(getClass().getResourceAsStream(resourcePath), StandardCharsets.UTF_8);
        Files.write(file.toPath(), Arrays.asList(fileContents.split("\\R").clone()));
    }

}

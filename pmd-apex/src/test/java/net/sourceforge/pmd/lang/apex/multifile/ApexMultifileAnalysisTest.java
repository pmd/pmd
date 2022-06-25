/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.util.IOUtil;

public class ApexMultifileAnalysisTest {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNoSfdxProjectJsonProducesFailedAnalysis() {
        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertTrue(analysisInstance.isFailed());
        assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        assertThat(systemErrRule.getLog(), containsStringIgnoringCase("Missing project file"));
    }

    @Test
    public void testMalformedSfdxProjectJsonProducesFailedAnalysis() throws IOException {
        copyResource("malformedSfdxFile.json", "sfdx-project.json");

        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertTrue(analysisInstance.isFailed());
        assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        assertThat(systemErrRule.getLog(),
                containsStringIgnoringCase("error: 'path' is required for all 'packageDirectories' elements"));
    }

    @Test
    public void testWellFormedSfdxProjectJsonProducesFunctionalAnalysis() throws IOException {
        copyResource("correctSfdxFile.json", "sfdx-project.json");

        ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

        assertFalse(analysisInstance.isFailed());
        assertTrue(systemErrRule.getLog().isEmpty());
    }

    private @NonNull ApexMultifileAnalysis getAnalysisForTempFolder() {
        return ApexMultifileAnalysis.getAnalysisInstance(tempFolder.getRoot().getAbsolutePath());
    }

    private void copyResource(String resourcePath, String relativePathInTempDir) throws IOException {
        File file = tempFolder.newFile(relativePathInTempDir);
        String fileContents = IOUtil.readToString(getClass().getResourceAsStream(resourcePath), StandardCharsets.UTF_8);
        Files.write(file.toPath(), Arrays.asList(fileContents.split("\\R").clone()));
    }

}

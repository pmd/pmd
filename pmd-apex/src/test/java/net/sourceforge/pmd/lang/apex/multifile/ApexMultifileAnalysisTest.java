/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class ApexMultifileAnalysisTest {

    @TempDir
    private Path tempFolder;

    @Test
    void testNoSfdxProjectJsonProducesFailedAnalysis() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

            assertTrue(analysisInstance.isFailed());
            assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        });
        assertThat(log, containsStringIgnoringCase("Missing project file"));
    }

    @Test
    void testMalformedSfdxProjectJsonProducesFailedAnalysis() throws Exception {
        copyResource("malformedSfdxFile.json", "sfdx-project.json");

        String log = SystemLambda.tapSystemErr(() -> {
            ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

            assertTrue(analysisInstance.isFailed());
            assertTrue(analysisInstance.getFileIssues("any file").isEmpty());
        });
        assertThat(log,
                containsStringIgnoringCase("Error: line 3 at 4: 'path' is required"));
    }

    @Test
    void testWellFormedSfdxProjectJsonProducesFunctionalAnalysis() throws Exception {
        copyResource("correctSfdxFile.json", "sfdx-project.json");

        String log = SystemLambda.tapSystemErr(() -> {
            ApexMultifileAnalysis analysisInstance = getAnalysisForTempFolder();

            assertFalse(analysisInstance.isFailed());
        });

        // TODO: log is not empty due to ANTLR versions, 4.9.1 vs 4.8, expect to resolve with apex-dev-tools switch
        log = log.replace("ANTLR Tool version 4.8 used for code generation does not match the current runtime version 4.9.1", "");
        log = log.replace("ANTLR Runtime version 4.8 used for parser compilation does not match the current runtime version 4.9.1", "");
        log = log.trim();
        assertTrue(log.isEmpty());
    }

    private @NonNull ApexMultifileAnalysis getAnalysisForTempFolder() {
        ApexLanguageProperties props = new ApexLanguageProperties();
        props.setProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY, tempFolder.toAbsolutePath().toString());
        return new ApexMultifileAnalysis(props);
    }

    private void copyResource(String resourcePath, String relativePathInTempDir) throws IOException {
        Path file = tempFolder.resolve(relativePathInTempDir);
        String fileContents = IOUtil.readToString(getClass().getResourceAsStream(resourcePath), StandardCharsets.UTF_8);
        Files.write(file, Arrays.asList(fileContents.split("\\R").clone()));
    }

}

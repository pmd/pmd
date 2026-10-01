/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;

import com.nawforce.pkgforce.path.PathLike;
import com.nawforce.runtime.platform.Environment;
import io.github.apexdevtools.api.Issue;
import scala.Option;

class ApexSemanticIssuesTest {

    private static final Path PROJECT = Paths.get(
            "src/test/resources/net/sourceforge/pmd/lang/apex/rule/errorprone/ApexSemanticErrors/project1")
            .toAbsolutePath();

    @TempDir
    private Path tempDir;

    @Test
    void exposesSemanticIssuesFromApexLs() {
        Option<PathLike> cachePath = Option.apply(new com.nawforce.runtime.platform.Path(tempDir));
        Environment.setCacheDirOverride(Option.apply(cachePath));

        ApexLanguageProperties properties = new ApexLanguageProperties();
        properties.setProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY, Optional.of(PROJECT.toString()));
        ApexMultifileAnalysis analysis = new ApexMultifileAnalysis(properties);

        Path apexFile = PROJECT.resolve("force-app/main/default/classes/FieldTypeMismatch.cls");
        List<Issue> issues = analysis.getFileIssues(apexFile.toString());
        Path validApexFile = PROJECT.resolve("force-app/main/default/classes/ValidField.cls");
        List<Issue> validFileIssues = analysis.getFileIssues(validApexFile.toString());
        assertFalse(analysis.isFailed());
        assertTrue(validFileIssues.stream().anyMatch(issue -> !Boolean.TRUE.equals(issue.isError())));

        List<Issue> errors = issues.stream()
                .filter(issue -> Boolean.TRUE.equals(issue.isError()))
                .collect(java.util.stream.Collectors.toList());
        assertEquals(1, errors.size());
        Issue error = errors.get(0);
        assertEquals("Error", error.rule().name());
        assertEquals(4, error.fileLocation().startLineNumber());
        assertEquals(8, error.fileLocation().startCharOffset());
        assertEquals(41, error.fileLocation().endCharOffset());
        assertEquals("Incompatible types in assignment, from 'System.String' to 'System.Decimal'",
                error.message());
    }
}

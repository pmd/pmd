/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

/**
 * Tests that {@link ApexUnitTestClassShouldHaveRunRelevantTestsAnnotationRule} takes the class's
 * {@code apiVersion} (read from its companion {@code -meta.xml} file) into account. This requires
 * real files on disk, so it can't be expressed with the usual in-memory XML-based rule tests.
 */
class ApexUnitTestClassShouldHaveRunRelevantTestsAnnotationApiVersionTest extends PmdRuleTst {

    private static final String TEST_RESOURCES_BASE =
            "src/test/resources/net/sourceforge/pmd/lang/apex/rule/bestpractices/RunRelevantTestsApiVersion/";

    @TempDir
    private Path tempDir;

    /**
     * The class has no 'critical'/'testFor' modifier, but its meta.xml declares an API version
     * older than 66.0, where these modifiers aren't available. It should not be flagged.
     */
    @Test
    void lowApiVersionIsNotFlagged() throws IOException {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "lowApiVersion"));
        assertEquals(0, report.getViolations().size(),
                "Expected no violation when the class's API version is below 66.0");
    }

    /**
     * Same class, but its meta.xml declares an API version of 66.0 or above, where the
     * 'critical'/'testFor' modifiers are available. It should be flagged.
     */
    @Test
    void highApiVersionIsFlagged() throws IOException {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "highApiVersion"));
        assertEquals(1, report.getViolations().size(),
                "Expected a violation when the class's API version is 66.0 or above");
    }

    private Report runRule(Path testProjectDir) throws IOException {
        return ApexMultifileTestSupport.runRule(tempDir, testProjectDir, "bestpractices",
                "ApexUnitTestClassShouldHaveRunRelevantTestsAnnotation");
    }
}

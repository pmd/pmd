package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class NoopAnalysisCacheDiffblueTest {
    /**
     * Method under test: {@link NoopAnalysisCache#isUpToDate(TextDocument)}
     */
    @Test
    void testIsUpToDate() {
        // Arrange, Act and Assert
        assertFalse((new NoopAnalysisCache()).isUpToDate(mock(TextDocument.class)));
    }

    /**
     * Method under test:
     * {@link NoopAnalysisCache#getCachedViolations(TextDocument)}
     */
    @Test
    void testGetCachedViolations() {
        // Arrange, Act and Assert
        assertTrue((new NoopAnalysisCache()).getCachedViolations(mock(TextDocument.class)).isEmpty());
    }

    /**
     * Method under test: {@link NoopAnalysisCache#startFileAnalysis(TextDocument)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testStartFileAnalysis() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        NoopAnalysisCache noopAnalysisCache = null;
        TextDocument filename = null;

        // Act
        FileAnalysisListener actualStartFileAnalysisResult = noopAnalysisCache.startFileAnalysis(filename);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link NoopAnalysisCache#analysisFailed(TextDocument)}
     *   <li>
     * {@link NoopAnalysisCache#checkValidity(RuleSets, ClassLoader, Collection)}
     *   <li>{@link NoopAnalysisCache#persist()}
     * </ul>
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   There are no fields that could be asserted on.

        // Arrange
        // TODO: Populate arranged inputs
        NoopAnalysisCache noopAnalysisCache = null;
        TextDocument sourceFile = null;

        // Act
        noopAnalysisCache.analysisFailed(sourceFile);
        RuleSets ruleSets = null;
        ClassLoader auxclassPathClassLoader = null;
        Collection<? extends TextFile> files = null;
        noopAnalysisCache.checkValidity(ruleSets, auxclassPathClassLoader, files);
        noopAnalysisCache.persist();

        // Assert
        // TODO: Add assertions on result
    }
}

package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.reporting.RuleViolation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AnalysisResultDiffblueTest {
    /**
     * Method under test: {@link AnalysisResult#addViolations(List)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddViolations() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        AnalysisResult analysisResult = null;
        List<RuleViolation> violations = null;

        // Act
        analysisResult.addViolations(violations);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AnalysisResult#addViolation(RuleViolation)}
     */
    @Test
    void testAddViolation() {
        // Arrange
        AnalysisResult analysisResult = new AnalysisResult(1L);

        // Act
        analysisResult.addViolation(null);

        // Assert
        List<RuleViolation> violations = analysisResult.getViolations();
        assertEquals(1, violations.size());
        assertNull(violations.get(0));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link AnalysisResult#getFileChecksum()}
     *   <li>{@link AnalysisResult#getViolations()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        AnalysisResult analysisResult = new AnalysisResult(1L);

        // Act
        long actualFileChecksum = analysisResult.getFileChecksum();

        // Assert
        assertEquals(1L, actualFileChecksum);
        assertTrue(analysisResult.getViolations().isEmpty());
    }

    /**
     * Method under test: {@link AnalysisResult#AnalysisResult(long)}
     */
    @Test
    void testNewAnalysisResult() {
        // Arrange and Act
        AnalysisResult actualAnalysisResult = new AnalysisResult(1L);

        // Assert
        assertEquals(1L, actualAnalysisResult.getFileChecksum());
        assertTrue(actualAnalysisResult.getViolations().isEmpty());
    }

    /**
     * Method under test: {@link AnalysisResult#AnalysisResult(long, List)}
     */
    @Test
    void testNewAnalysisResult2() {
        // Arrange
        ArrayList<RuleViolation> violations = new ArrayList<>();

        // Act
        AnalysisResult actualAnalysisResult = new AnalysisResult(1L, violations);

        // Assert
        assertEquals(1L, actualAnalysisResult.getFileChecksum());
        List<RuleViolation> violations2 = actualAnalysisResult.getViolations();
        assertTrue(violations2.isEmpty());
        assertSame(violations, violations2);
    }

    /**
     * Method under test: {@link AnalysisResult#AnalysisResult(long, List)}
     */
    @Test
    void testNewAnalysisResult3() {
        // Arrange
        ArrayList<RuleViolation> violations = new ArrayList<>();
        violations.add(null);

        // Act
        AnalysisResult actualAnalysisResult = new AnalysisResult(1L, violations);

        // Assert
        List<RuleViolation> violations2 = actualAnalysisResult.getViolations();
        assertEquals(1, violations2.size());
        assertNull(violations2.get(0));
        assertEquals(1L, actualAnalysisResult.getFileChecksum());
        assertSame(violations, violations2);
    }
}

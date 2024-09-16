package net.sourceforge.pmd.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

class TimingReportDiffblueTest {
    /**
     * Method under test:
     * {@link TimingReport#getLabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetLabeledMeasurements() {
        // Arrange, Act and Assert
        assertTrue((new TimingReport(1L, new HashMap<>())).getLabeledMeasurements(TimedOperationCategory.RULE).isEmpty());
    }

    /**
     * Method under test:
     * {@link TimingReport#getLabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetLabeledMeasurements2() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                "Label");

        TimeTracker.TimedResult timedResult = new TimeTracker.TimedResult();
        accumulatedResults.put(timedOperationKey, timedResult);

        // Act
        Map<String, TimeTracker.TimedResult> actualLabeledMeasurements = (new TimingReport(1L, accumulatedResults))
                .getLabeledMeasurements(TimedOperationCategory.RULE);

        // Assert
        assertEquals(1, actualLabeledMeasurements.size());
        assertSame(timedResult, actualLabeledMeasurements.get("Label"));
    }

    /**
     * Method under test:
     * {@link TimingReport#getLabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetLabeledMeasurements3() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        accumulatedResults.computeIfPresent(new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE, "Label"),
                mock(BiFunction.class));
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                "Label");

        TimeTracker.TimedResult timedResult = new TimeTracker.TimedResult();
        accumulatedResults.put(timedOperationKey, timedResult);

        // Act
        Map<String, TimeTracker.TimedResult> actualLabeledMeasurements = (new TimingReport(1L, accumulatedResults))
                .getLabeledMeasurements(TimedOperationCategory.RULE);

        // Assert
        assertEquals(1, actualLabeledMeasurements.size());
        assertSame(timedResult, actualLabeledMeasurements.get("Label"));
    }

    /**
     * Method under test:
     * {@link TimingReport#getLabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetLabeledMeasurements4() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(null, "Label");

        accumulatedResults.put(timedOperationKey, new TimeTracker.TimedResult());

        // Act and Assert
        assertTrue(
                (new TimingReport(1L, accumulatedResults)).getLabeledMeasurements(TimedOperationCategory.RULE).isEmpty());
    }

    /**
     * Method under test:
     * {@link TimingReport#getLabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetLabeledMeasurements5() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                null);

        accumulatedResults.put(timedOperationKey, new TimeTracker.TimedResult());

        // Act and Assert
        assertTrue(
                (new TimingReport(1L, accumulatedResults)).getLabeledMeasurements(TimedOperationCategory.RULE).isEmpty());
    }

    /**
     * Method under test:
     * {@link TimingReport#getUnlabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetUnlabeledMeasurements() {
        // Arrange, Act and Assert
        assertNull((new TimingReport(1L, new HashMap<>())).getUnlabeledMeasurements(TimedOperationCategory.RULE));
    }

    /**
     * Method under test:
     * {@link TimingReport#getUnlabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetUnlabeledMeasurements2() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                "Label");

        accumulatedResults.put(timedOperationKey, new TimeTracker.TimedResult());

        // Act and Assert
        assertNull((new TimingReport(1L, accumulatedResults)).getUnlabeledMeasurements(TimedOperationCategory.RULE));
    }

    /**
     * Method under test:
     * {@link TimingReport#getUnlabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetUnlabeledMeasurements3() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        accumulatedResults.computeIfPresent(new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE, "Label"),
                mock(BiFunction.class));
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                "Label");

        accumulatedResults.put(timedOperationKey, new TimeTracker.TimedResult());

        // Act and Assert
        assertNull((new TimingReport(1L, accumulatedResults)).getUnlabeledMeasurements(TimedOperationCategory.RULE));
    }

    /**
     * Method under test:
     * {@link TimingReport#getUnlabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetUnlabeledMeasurements4() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(null, "Label");

        accumulatedResults.put(timedOperationKey, new TimeTracker.TimedResult());

        // Act and Assert
        assertNull((new TimingReport(1L, accumulatedResults)).getUnlabeledMeasurements(TimedOperationCategory.RULE));
    }

    /**
     * Method under test:
     * {@link TimingReport#getUnlabeledMeasurements(TimedOperationCategory)}
     */
    @Test
    void testGetUnlabeledMeasurements5() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        TimeTracker.TimedOperationKey timedOperationKey = new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE,
                null);

        TimeTracker.TimedResult timedResult = new TimeTracker.TimedResult();
        accumulatedResults.put(timedOperationKey, timedResult);

        // Act and Assert
        assertSame(timedResult,
                (new TimingReport(1L, accumulatedResults)).getUnlabeledMeasurements(TimedOperationCategory.RULE));
    }

    /**
     * Method under test: {@link TimingReport#getWallClockMillis()}
     */
    @Test
    void testGetWallClockMillis() {
        // Arrange, Act and Assert
        assertEquals(1L, (new TimingReport(1L, new HashMap<>())).getWallClockMillis());
    }

    /**
     * Method under test: {@link TimingReport#TimingReport(long, Map)}
     */
    @Test
    void testNewTimingReport() {
        // Arrange
        HashMap<TimeTracker.TimedOperationKey, TimeTracker.TimedResult> accumulatedResults = new HashMap<>();
        accumulatedResults.computeIfPresent(new TimeTracker.TimedOperationKey(TimedOperationCategory.RULE, "Label"),
                mock(BiFunction.class));

        // Act and Assert
        assertEquals(1L, (new TimingReport(1L, accumulatedResults)).getWallClockMillis());
    }
}

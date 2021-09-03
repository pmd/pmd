/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.benchmark.TimeTracker.TimedOperationKey;
import net.sourceforge.pmd.benchmark.TimeTracker.TimedResult;

/**
 * A report on timing data obtained through the execution of PMD
 * @author Juan Martín Sotuyo Dodero
 */
public class TimingReport {

    private final long wallClockMillis;
    private final Map<TimedOperationKey, TimedResult> results;

    /* package */ TimingReport(final long wallClockMillis, final Map<TimedOperationKey, TimedResult> accumulatedResults) {
        this.wallClockMillis = wallClockMillis;
        results = accumulatedResults;
    }

    public Map<String, TimedResult> getLabeledMeasurements(final TimedOperationCategory category) {
        final Map<String, TimedResult> ret = new HashMap<>();

        for (final Map.Entry<TimedOperationKey, TimedResult> entry : results.entrySet()) {
            final TimedOperationKey timedOperation = entry.getKey();
            if (timedOperation.category == category && timedOperation.label != null) {
                ret.put(timedOperation.label, entry.getValue());
            }
        }

        return ret;
    }

    public TimedResult getUnlabeledMeasurements(final TimedOperationCategory category) {
        for (final Map.Entry<TimedOperationKey, TimedResult> entry : results.entrySet()) {
            final TimedOperationKey timedOperation = entry.getKey();
            if (timedOperation.category == category && timedOperation.label == null) {
                return entry.getValue();
            }
        }

        return null;
    }

    public long getWallClockMillis() {
        return wallClockMillis;
    }
}

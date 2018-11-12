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
    private final Map<TimedOperationCategory, Map<String, TimedResult>> results;


    /* package */ TimingReport(final long wallClockMillis, final Map<TimedOperationKey, TimedResult> accumulatedResults) {
        this.wallClockMillis = wallClockMillis;
        results = new HashMap<>();

        for (final Map.Entry<TimedOperationKey, TimedResult> entry : accumulatedResults.entrySet()) {
            final TimedOperationKey timedOperation = entry.getKey();
            results.computeIfAbsent(timedOperation.category, t -> new HashMap<>())
                   .put(timedOperation.label, entry.getValue());
        }
    }


    public Map<TimedOperationCategory, Map<String, TimedResult>> getAllMeasurements() {
        return results;
    }
    
    public long getWallClockMillis() {
        return wallClockMillis;
    }
}

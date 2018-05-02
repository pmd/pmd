/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import net.sourceforge.pmd.benchmark.TimeTracker.TimedOperation;
import net.sourceforge.pmd.benchmark.TimeTracker.TimedResult;

/**
 * A report on timing data obtained through the execution of PMD
 * @author Juan Martín Sotuyo Dodero
 */
public class TimingReport {

    private final long wallClockMs;
    private final ConcurrentMap<TimedOperation, TimedResult> results;
    
    /* package */ TimingReport(final long wallClock, final ConcurrentMap<TimedOperation, TimedResult> accumulatedResults) {
        wallClockMs = wallClock;
        results = accumulatedResults;
    }
    
    public Map<String, TimedResult> getLabeledMeassurements(final TimedOperationCategory category) {
        final Map<String, TimedResult> ret = new HashMap<>();
        
        for (final Map.Entry<TimedOperation, TimedResult> entry : results.entrySet()) {
            final TimedOperation timedOperation = entry.getKey();
            if (timedOperation.category == category && timedOperation.label != null) {
                ret.put(timedOperation.label, entry.getValue());
            }
        }
        
        return ret;
    }
    
    public TimedResult getUnlabeledMeassurements(final TimedOperationCategory category) {
        for (final Map.Entry<TimedOperation, TimedResult> entry : results.entrySet()) {
            final TimedOperation timedOperation = entry.getKey();
            if (timedOperation.category == category && timedOperation.label == null) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    public long getWallClockTimeMs() {
        return wallClockMs;
    }
}

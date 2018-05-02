/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A time tracker class to measure time spent on different sections of PMD analysis.
 * The class is thread-aware, allowing to differentiate CPU and wall clock time.
 * 
 * @author Juan Martín Sotuyo Dodero
 */
public final class TimeTracker {

    private static boolean trackTime = false;
    private static long wallClockStart = -1;
    private static final ThreadLocal<Queue<TimerEntry>> TIMER_ENTRIES;
    private static final ConcurrentMap<TimedOperation, TimedResult> ACCUMULATED_RESULTS = new ConcurrentHashMap<>();
    
    static {
        TIMER_ENTRIES = new ThreadLocal<Queue<TimerEntry>>() {
            @Override
            protected Queue<TimerEntry> initialValue() {
                final Queue<TimerEntry> queue = Collections.asLifoQueue(new LinkedList<TimerEntry>());
                queue.add(new TimerEntry(TimedOperationCategory.UNACCOUNTED, null));
                return queue;
            }
        };
    }
    
    private TimeTracker() {
        throw new AssertionError("Can't instantiate utility class");
    }
    
    /**
     * Starts global tracking. Allows tracking operations to take place and starts the wall clock.
     * Must be called once PMD starts if tracking is desired, no tracking will be performed otherwise.
     */
    public static void startGlobalTracking() {
        wallClockStart = System.currentTimeMillis();
        trackTime = true;
        initThread(); // init main thread
    }
    
    /**
     * Stops global tracking. Stops the wall clock. All further operations will be treated as NOOP.
     * @return The timed data obtained through the run.
     */
    public static TimingReport stopGlobalTracking() {
        if (!trackTime) {
            return null;
        }
        
        finishThread(); // finish the main thread
        trackTime = false;
        
        // Fix UNACCOUNTED metric (total time is meaningless)
        final TimedResult unaccountedResult = ACCUMULATED_RESULTS.get(
                new TimedOperation(TimedOperationCategory.UNACCOUNTED, null));
        unaccountedResult.totalTime.set(unaccountedResult.selfTime.get());
        
        return new TimingReport(System.currentTimeMillis() - wallClockStart, ACCUMULATED_RESULTS);
    }
    
    /**
     * Initialize a thread, starting to track it's own time.
     */
    public static void initThread() {
        if (!trackTime) {
            return;
        }
        
        TIMER_ENTRIES.get(); // Just make sure it's initialized
    }
    
    /**
     * Finishes tracking a thread.
     */
    public static void finishThread() {
        if (!trackTime) {
            return;
        }
        
        // if using a mono-thread, we may not be empty...
        if (TIMER_ENTRIES.get().size() == 1) {
            finishOperation();
            TIMER_ENTRIES.remove();
        }
    }
    
    /**
     * Starts tracking an operation
     * @param category The category under which to track the operation.
     */
    public static void startOperation(final TimedOperationCategory category) {
        startOperation(category, null);
    }
    
    /**
     * Starts tracking an operation
     * @param category The category under which to track the operation.
     * @param label A label to be added to the category. Allows to differentiate measures within a single category.
     */
    public static void startOperation(final TimedOperationCategory category, final String label) {
        if (!trackTime) {
            return;
        }
        
        TIMER_ENTRIES.get().add(new TimerEntry(category, label));
    }
    
    /**
     * Finishes tracking an operation
     */
    public static void finishOperation() {
        finishOperation(0);
    }
    
    /**
     * Finishes tracking an operation
     * @param extraDataCounter An optional additional data counter to track along the measurements
     */
    public static void finishOperation(final long extraDataCounter) {
        if (!trackTime) {
            return;
        }

        final Queue<TimerEntry> queue = TIMER_ENTRIES.get();
        final TimerEntry timerEntry = queue.remove();

        // Compute if absent
        TimedResult result = ACCUMULATED_RESULTS.get(timerEntry.operation);
        if (result == null) {
            ACCUMULATED_RESULTS.putIfAbsent(timerEntry.operation, new TimedResult());
            result = ACCUMULATED_RESULTS.get(timerEntry.operation);
        }

        // Update counters and let next element on the stack ignore the time we spent
        final long delta = result.accumulate(timerEntry, extraDataCounter);
        if (!queue.isEmpty()) { 
            queue.peek().inNestedOperations += delta;
        }
    }
    
    private static class TimerEntry {
        /* package */ final TimedOperation operation;
        /* package */ final long start;
        /* package */ long inNestedOperations = 0;
        
        /* package */ TimerEntry(final TimedOperationCategory category, final String label) {
            this.operation = new TimedOperation(category, label);
            this.start = System.nanoTime();
        }

        @Override
        public String toString() {
            return "TimerEntry for " + operation;
        }
    }
    
    /* package */ static class TimedResult {
        /* package */ AtomicLong totalTime = new AtomicLong();
        /* package */ AtomicLong selfTime = new AtomicLong();
        /* package */ AtomicInteger callCount = new AtomicInteger();
        /* package */ AtomicLong extraDataCounter = new AtomicLong();
        
        /* package */ long accumulate(final TimerEntry timerEntry, final long extraData) {
            final long delta = System.nanoTime() - timerEntry.start;
            
            totalTime.getAndAdd(delta);
            selfTime.getAndAdd(delta - timerEntry.inNestedOperations);
            callCount.getAndIncrement();
            extraDataCounter.getAndAdd(extraData);
            
            return delta;
        }
        
        /* package */ void mergeTimes(final TimedResult timedResult) {
            totalTime.getAndAdd(timedResult.totalTime.get());
            selfTime.getAndAdd(timedResult.selfTime.get());
        }
    }
    
    /* package */ static class TimedOperation {
        /* package */ final TimedOperationCategory category;
        /* package */ final String label;
        
        /* package */ TimedOperation(final TimedOperationCategory category, final String label) {
            this.category = category;
            this.label = label;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((category == null) ? 0 : category.hashCode());
            result = prime * result + ((label == null) ? 0 : label.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TimedOperation other = (TimedOperation) obj;
            if (category != other.category) {
                return false;
            }
            if (label == null) {
                if (other.label != null) {
                    return false;
                }
            } else if (!label.equals(other.label)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "TimedOperation [category=" + category + ", label=" + label + "]";
        }
    }
}

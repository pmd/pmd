/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
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
    private static long wallClockStartMillis = -1;
    private static final ThreadLocal<Queue<TimerEntry>> TIMER_ENTRIES;
    private static final ConcurrentMap<TimedOperationKey, TimedResult> ACCUMULATED_RESULTS = new ConcurrentHashMap<>();
    private static final TimedOperation NOOP_TIMED_OPERATION = new TimedOperation() {

        @Override
        public void close() {
            // noop
        }

        @Override
        public void close(final int count) {
         // noop
        }
    };

    static {
        TIMER_ENTRIES = new ThreadLocal<Queue<TimerEntry>>() {
            @Override
            protected Queue<TimerEntry> initialValue() {
                return Collections.asLifoQueue(new LinkedList<TimerEntry>());
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
        wallClockStartMillis = System.currentTimeMillis();
        trackTime = true;
        ACCUMULATED_RESULTS.clear(); // just in case
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

        // Fix UNACCOUNTED metric (total time is meaningless as is call count)
        final TimedResult unaccountedResult = ACCUMULATED_RESULTS.get(
                new TimedOperationKey(TimedOperationCategory.UNACCOUNTED, null));
        unaccountedResult.totalTimeNanos.set(unaccountedResult.selfTimeNanos.get());
        unaccountedResult.callCount.set(0);

        return new TimingReport(System.currentTimeMillis() - wallClockStartMillis, ACCUMULATED_RESULTS);
    }

    /**
     * Initialize a thread, starting to track it's own time.
     */
    public static void initThread() {
        if (!trackTime) {
            return;
        }

        startOperation(TimedOperationCategory.UNACCOUNTED);
    }

    /**
     * Finishes tracking a thread.
     */
    public static void finishThread() {
        if (!trackTime) {
            return;
        }

        finishOperation(0);

        // clean up thread-locals in multithread analysis
        if (TIMER_ENTRIES.get().isEmpty()) {
            TIMER_ENTRIES.remove();
        }
    }

    /**
     * Starts tracking an operation.
     * @param category The category under which to track the operation.
     * @return The current timed operation being tracked.
     */
    public static TimedOperation startOperation(final TimedOperationCategory category) {
        return startOperation(category, null);
    }

    /**
     * Starts tracking an operation.
     * @param category The category under which to track the operation.
     * @param label A label to be added to the category. Allows to differentiate measures within a single category.
     * @return The current timed operation being tracked.
     */
    public static TimedOperation startOperation(final TimedOperationCategory category, final String label) {
        if (!trackTime) {
            return NOOP_TIMED_OPERATION;
        }

        TIMER_ENTRIES.get().add(new TimerEntry(category, label));
        return new TimedOperationImpl();
    }

    /**
     * Finishes tracking an operation.
     * @param extraDataCounter An optional additional data counter to track along the measurements.
     *                         Users are free to track any extra value they want (ie: number of analyzed nodes,
     *                         iterations in a loop, etc.)
     */
    /* default */ static void finishOperation(final long extraDataCounter) {
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
            queue.peek().inNestedOperationsNanos += delta;
        }
    }

    /**
     * An entry in the open timers queue. Defines an operation that has started and hasn't finished yet.
     */
    private static class TimerEntry {
        /* package */ final TimedOperationKey operation;
        /* package */ final long start;
        /* package */ long inNestedOperationsNanos = 0;

        /* package */ TimerEntry(final TimedOperationCategory category, final String label) {
            this.operation = new TimedOperationKey(category, label);
            this.start = System.nanoTime();
        }

        @Override
        public String toString() {
            return "TimerEntry for " + operation;
        }
    }

    /**
     * Aggregate results measured so far for a given category + label.
     */
    /* package */ static class TimedResult {
        /* package */ AtomicLong totalTimeNanos = new AtomicLong();
        /* package */ AtomicLong selfTimeNanos = new AtomicLong();
        /* package */ AtomicInteger callCount = new AtomicInteger();
        /* package */ AtomicLong extraDataCounter = new AtomicLong();

        /**
         * Adds a new {@link TimerEntry} to the results.
         * @param timerEntry The entry to be added
         * @param extraData Any extra data counter to be added
         * @return The delta time transcurred since the {@link TimerEntry} began in nanos.
         */
        /* package */ long accumulate(final TimerEntry timerEntry, final long extraData) {
            final long delta = System.nanoTime() - timerEntry.start;

            totalTimeNanos.getAndAdd(delta);
            selfTimeNanos.getAndAdd(delta - timerEntry.inNestedOperationsNanos);
            callCount.getAndIncrement();
            extraDataCounter.getAndAdd(extraData);

            return delta;
        }

        /**
         * Merges the times (and only the times) from another {@link TimedResult} into self.
         * @param timedResult The {@link TimedResult} to merge
         */
        /* package */ void mergeTimes(final TimedResult timedResult) {
            totalTimeNanos.getAndAdd(timedResult.totalTimeNanos.get());
            selfTimeNanos.getAndAdd(timedResult.selfTimeNanos.get());
        }
    }

    /**
     * A unique identifier for a timed operation
     */
    /* package */ static class TimedOperationKey {
        /* package */ final TimedOperationCategory category;
        /* package */ final String label;

        /* package */ TimedOperationKey(final TimedOperationCategory category, final String label) {
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
            TimedOperationKey other = (TimedOperationKey) obj;
            if (category != other.category) {
                return false;
            }
            return Objects.equals(label, other.label);
        }

        @Override
        public String toString() {
            return "TimedOperationKey [category=" + category + ", label=" + label + "]";
        }
    }

    /**
     * A standard timed operation implementation.
     */
    private static class TimedOperationImpl implements TimedOperation {
        private boolean closed = false;

        @Override
        public void close() {
            close(0);
        }

        @Override
        public void close(int extraDataCounter) {
            if (closed) {
                return;
            }

            closed = true;
            TimeTracker.finishOperation(extraDataCounter);
        }
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newInsertRewriteEvent;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newRemoveRewriteEvent;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newReplaceRewriteEvent;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.INSERT;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.REMOVE;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.REPLACE;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Implementation of {@link RewriteEventsRecorder}, that records modifications as {@link RewriteEvent}s.
 * <p>
 * This class is not able to record rewrite events for different nodes, so each node may hold its own instance.
 * </p>
 * <p>
 * Rewrite events occurring on the same index are merged straight away, so at all moment only one rewrite event
 * is hold for a given index. This helps to understand exactly what kind of change has the node suffer,
 * independently of how many times it has been modified.
 * </p>
 */
public class RewriteEventsRecorderImpl implements RewriteEventsRecorder {
    /**
     * All rewrite events hold by this instance. The rewrite event for a given index corresponds to the modification
     * that the child node at that position suffered.
     */
    private RewriteEvent[] rewriteEvents;

    @Override
    public void recordRemove(final Node parentNode, final Node oldChildNode, final int childIndex) {
        Objects.requireNonNull(parentNode);
        Objects.requireNonNull(oldChildNode);
        validateNonNullIndex(childIndex);

        recordRewriteEvent(RewriteEventFactory.newRemoveRewriteEvent(parentNode, oldChildNode, childIndex));
    }

    @Override
    public void recordInsert(final Node parentNode, final Node newChildNode, final int childIndex) {
        Objects.requireNonNull(parentNode);
        Objects.requireNonNull(newChildNode);
        validateNonNullIndex(childIndex);

        recordRewriteEvent(RewriteEventFactory.newInsertRewriteEvent(parentNode, newChildNode, childIndex));
    }

    @Override
    public void recordReplace(final Node parentNode, final Node oldChildNode, final Node newChildNode, final int childIndex) {
        Objects.requireNonNull(parentNode);
        Objects.requireNonNull(oldChildNode);
        Objects.requireNonNull(newChildNode);
        validateNonNullIndex(childIndex);

        recordRewriteEvent(RewriteEventFactory.newReplaceRewriteEvent(parentNode, oldChildNode, newChildNode, childIndex));
    }

    @Override
    public boolean hasRewriteEvents() {
        return rewriteEvents != null && rewriteEvents.length > 0;
    }

    @Override
    public RewriteEvent[] getRewriteEvents() {
        // Completely immutable as RewriteEvent has all its fields final
        return Arrays.copyOf(rewriteEvents, rewriteEvents.length);
    }

    private void validateNonNullIndex(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException(String.format("index <%d> is lower than 0", index));
        }
    }

    private void recordRewriteEvent(final RewriteEvent rewriteEvent) {
        final int childIndex = rewriteEvent.getChildNodeIndex();
        if (rewriteEvents == null) {
            rewriteEvents = new RewriteEvent[childIndex + 1];
        } else if (childIndex >= rewriteEvents.length) {
            final RewriteEvent[] newRewriteEvents = new RewriteEvent[childIndex + 1];
            System.arraycopy(rewriteEvents, 0, newRewriteEvents, 0, rewriteEvents.length);
            rewriteEvents = newRewriteEvents;
        }

        final RewriteEvent oldRewriteEvent = rewriteEvents[childIndex];
        if (oldRewriteEvent == null) { // This is the first event for this child index
            rewriteEvents[childIndex] = rewriteEvent;
        } else {
            // There is a previous event for the given index => we have to merge the old node event
            //  with the new one before recording the given event
            rewriteEvents = recordMergedRewriteEvents(rewriteEvents, childIndex, oldRewriteEvent, rewriteEvent);
        }
    }

    private RewriteEvent[] recordMergedRewriteEvents(final RewriteEvent[] pRewriteEvents, final int childIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
        final RewriteEventType oldRewriteEventType = oldRewriteEvent.getRewriteEventType();
        final RewriteEventType newRewriteEventType = newRewriteEvent.getRewriteEventType();
        final RewriteEventsMerger rewriteEventsMerger = RewriteEventsMergers.getRewriteEventsMerger(oldRewriteEventType, newRewriteEventType);
        return rewriteEventsMerger.recordMerge(pRewriteEvents, childIndex, oldRewriteEvent, newRewriteEvent);
    }

    /**
     * Interface that describe the main method to record a merge of two rewrite events.
     */
    private interface RewriteEventsMerger {
        /**
         * <p>
         * Record a rewrite event at the given {@code rewriteEventIndex} on the given {@code rewriteEvents} array.
         * </p>
         * <p>
         * This rewrite event is the result of merging the {@code oldRewriteEvent} with the {@code newRewriteEvent}.
         * The merging policy may vary depending on the type of rewrite event that each of them (old an new)
         * represent.
         * </p>
         * <p>
         * Interface's implementations are in charge of carrying out the correct merge policy in each case.
         * </p>
         * <p>
         * <strong>The original {@code rewriteEvents} array is not modified</strong>; instead, a new updated copy
         * of the given array is returned.
         * </p>
         *
         * @param rewriteEvents     The rewrite events where to record the merged rewrite event.
         * @param rewriteEventIndex The index where to record the merged rewrite event
         * @param oldRewriteEvent   The old rewrite event to be merged with the new one.
         * @param newRewriteEvent   The new rewrite event to be merged with the old one.
         * @return An updated copy of the given {@code rewriteEvents}.
         */
        RewriteEvent[] recordMerge(RewriteEvent[] rewriteEvents, int rewriteEventIndex, RewriteEvent oldRewriteEvent, RewriteEvent newRewriteEvent);
    }

    /**
     * <p>
     * Class implementing all the different merging policies based on the type of {@code oldRewriteEvent}
     * and {@code newRewriteEvent}.
     * </p>
     * <p>
     * Each merging policy should be obtained with the method {@code getRewriteEventsMerger},
     * and not directly accessed.
     * </p>
     */
    private abstract static class RewriteEventsMergers {
        private static final RewriteEventsMerger INSERT_NEW_REWRITE_EVENT_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                return ArrayUtils.insert(rewriteEventIndex, rewriteEvents, newRewriteEvent);
            }
        };

        private static final RewriteEventsMerger REMOVE_ORIGINAL_REWRITE_EVENT_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                return ArrayUtils.remove(rewriteEvents, rewriteEventIndex);
            }
        };

        private static final RewriteEventsMerger INSERT_REWRITE_EVENTS_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                final RewriteEvent mergedRewriteEvent = newInsertRewriteEvent(newRewriteEvent.getParentNode(), newRewriteEvent.getNewChildNode(), rewriteEventIndex);
                rewriteEvents[rewriteEventIndex] = mergedRewriteEvent;
                return rewriteEvents;
            }
        };

        private static final RewriteEventsMerger REPLACE_REWRITE_EVENTS_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                final RewriteEvent mergedRewriteEvent = newReplaceRewriteEvent(newRewriteEvent.getParentNode(), oldRewriteEvent.getOldChildNode(), newRewriteEvent.getNewChildNode(), rewriteEventIndex);
                rewriteEvents[rewriteEventIndex] = mergedRewriteEvent;
                return rewriteEvents;
            }
        };

        private static final RewriteEventsMerger REMOVE_REWRITE_EVENTS_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                final RewriteEvent mergedRewriteEvent = newRemoveRewriteEvent(newRewriteEvent.getParentNode(), oldRewriteEvent.getOldChildNode(), rewriteEventIndex);
                rewriteEvents[rewriteEventIndex] = mergedRewriteEvent;
                return rewriteEvents;
            }
        };

        private static final RewriteEventsMerger INVALID_MERGER = new RewriteEventsMerger() {
            @Override
            public RewriteEvent[] recordMerge(final RewriteEvent[] rewriteEvents, final int rewriteEventIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
                validate(rewriteEventIndex, oldRewriteEvent, newRewriteEvent);
                final String msg = String.format("Cannot merge events: <%s> -> <%s>", oldRewriteEvent.getRewriteEventType(), newRewriteEvent.getRewriteEventType());
                throw new IllegalStateException(msg);
            }
        };

        private static final RewriteEventsMerger[][] REWRITE_EVENTS_MERGERS;

        static {
            final int size = RewriteEventType.values().length;
            REWRITE_EVENTS_MERGERS = new RewriteEventsMerger[size][size];
            final int iInsert = INSERT.getIndex();
            final int iRemove = REMOVE.getIndex();
            final int iReplace = REPLACE.getIndex();

            // Insert -> Insert = both Inserts are kept
            REWRITE_EVENTS_MERGERS[iInsert][iInsert] = INSERT_NEW_REWRITE_EVENT_MERGER;

            // Insert -> Replace = Insert, with the newRewriteEvent of the Replace event
            REWRITE_EVENTS_MERGERS[iInsert][iReplace] = INSERT_REWRITE_EVENTS_MERGER;

            // Insert -> Remove = remove the original Insert event
            REWRITE_EVENTS_MERGERS[iInsert][iRemove] = REMOVE_ORIGINAL_REWRITE_EVENT_MERGER;

            // Replace -> Insert = Replace & Insert are kept
            REWRITE_EVENTS_MERGERS[iReplace][iInsert] = INSERT_NEW_REWRITE_EVENT_MERGER;

            // Replace -> Replace = Replace, with the oldRewriteEvent of the original Replace and the newRewriteEvent of the new Replace
            REWRITE_EVENTS_MERGERS[iReplace][iReplace] = REPLACE_REWRITE_EVENTS_MERGER;

            // Replace -> Remove = Remove, with the oldRewriteEvent of the original Replace
            REWRITE_EVENTS_MERGERS[iReplace][iRemove] = REMOVE_REWRITE_EVENTS_MERGER;

            // Remove -> Insert = Replace, with the oldRewriteEvent of the Remove and the newRewriteEvent of the Insert
            REWRITE_EVENTS_MERGERS[iRemove][iInsert] = REPLACE_REWRITE_EVENTS_MERGER;

            // Cannot replace or remove an already removed node
            REWRITE_EVENTS_MERGERS[iRemove][iReplace] = INVALID_MERGER;
            REWRITE_EVENTS_MERGERS[iRemove][iRemove] = INVALID_MERGER;
        }

        /**
         * @param oldEventType The old event type.
         * @param newEventType The new event type.
         * @return The rewrite events merger that implements the correct merging policy for the given
         * {@code oldEventType} and {@code newEventType}.
         */
        private static RewriteEventsMerger getRewriteEventsMerger(final RewriteEventType oldEventType, final RewriteEventType newEventType) {
            return REWRITE_EVENTS_MERGERS[oldEventType.getIndex()][newEventType.getIndex()];
        }

        private static void validate(final int childIndex, final RewriteEvent oldRewriteEvent, final RewriteEvent newRewriteEvent) {
            final int oldEventIndex = oldRewriteEvent.getChildNodeIndex();
            final int newEventIndex = newRewriteEvent.getChildNodeIndex();
            if (childIndex != oldEventIndex || childIndex != newEventIndex) {
                final String msg = String.format("Invalid childIndex. childIndex: <%d>, "
                        + "oldRewriteEvent.childIndex: <%d>, newRewriteEvent.childIndex: <%d>",
                    childIndex, oldEventIndex, newEventIndex);
                throw new IllegalArgumentException(msg);
            }

            final Node oldEventParentNode = oldRewriteEvent.getParentNode();
            final Node newEventParentNode = newRewriteEvent.getParentNode();
            if (!oldEventParentNode.equals(newEventParentNode)) {
                throw new IllegalArgumentException("Parent nodes of both rewrite events should be the same.");
            }

            final Node oldEventNewChild = oldRewriteEvent.getNewChildNode();
            final Node newEventOldChild = newRewriteEvent.getOldChildNode();
            if (newEventOldChild != null && !newEventOldChild.equals(oldEventNewChild)) {
                throw new IllegalArgumentException("oldChildNode of the new record event should be "
                    + "the same as the newChildNode of the old record event in order to "
                    + "be able to merge these events");
            }
        }
    }

}

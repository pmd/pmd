/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newInsertRewriteEvent;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newRemoveRewriteEvent;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory.newReplaceRewriteEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

@RunWith(JUnitParamsRunner.class)
public class RewriteEventsRecorderImplTest {
    private static final Node PARENT_NODE = DummyNode.newInstance();
    private static final Node PARENT_NODE_2 = DummyNode.newInstance();
    private static final Node OLD_CHILD_NODE = DummyNode.newInstance();
    private static final Node NEW_CHILD_NODE = DummyNode.newInstance();
    private static final Node OLD_CHILD_NODE_2 = DummyNode.newInstance();
    private static final Node NEW_CHILD_NODE_2 = DummyNode.newInstance();
    private static final int INSERT_I = 0;
    private static final int REPLACE_I = 1;
    private static final int REMOVE_I = 2;
    private static final Recorder ORIGINAL_INSERT_RECORDER = new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE, INSERT_I);
    private static final Recorder ORIGINAL_REPLACE_RECORDER = new ReplaceRecorder(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE, REPLACE_I);
    private static final Recorder ORIGINAL_REMOVE_RECORDER = new RemoveRecorder(PARENT_NODE, OLD_CHILD_NODE, REMOVE_I);

    private RewriteEventsRecorder rewriteEventsRecorder;

    @Before
    public void initializeRewriteEventsRecorder() {
        rewriteEventsRecorder = new RewriteEventsRecorderImpl();
    }

    // -----------------** Single Rewrite Events Test Cases **----------------- //
    // -----------------* Valid cases *----------------- //
    @SuppressWarnings("unused") // Used by JUnitParams in `testSingleRecord` test case
    private Object testSingleRecordParameter() {
        return new Object[] {
            // Insert
            new Object[] {
                "Insert rewrite event",
                new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE, INSERT_I),
                newInsertRewriteEvent(PARENT_NODE, NEW_CHILD_NODE, INSERT_I),
                INSERT_I,
            },
            // Replace
            new Object[] {
                "Replace rewrite event",
                new ReplaceRecorder(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE, REPLACE_I),
                newReplaceRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE, REPLACE_I),
                REPLACE_I,
            },
            // Remove
            new Object[] {
                "Remove rewrite event",
                new RemoveRecorder(PARENT_NODE, OLD_CHILD_NODE, REMOVE_I),
                newRemoveRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, REMOVE_I),
                REMOVE_I,
            },
        };
    }

    @Test
    @Parameters(method = "testSingleRecordParameter")
    @TestCaseName("Single Rewrite Event: <{0}>")
    public void testSingleRecord(@SuppressWarnings("unused") final String testCaseName,
                                 final Recorder recorder,
                                 final RewriteEvent expectedRewriteEvent,
                                 final int rewriteEventIndex) {
        // Do the actual record
        recorder.record(rewriteEventsRecorder);
        // Event should have been correctly recorded
        assertTrue(rewriteEventsRecorder.hasRewriteEvents());
        assertEquals(expectedRewriteEvent, rewriteEventsRecorder.getRewriteEvents()[rewriteEventIndex]);
    }

    // -----------------* Invalid cases *----------------- //
    @SuppressWarnings("unused") // Used by JUnitParams in `testInvalidSingleRecord` test case
    private Object testInvalidSingleRecordParameters() {
        return new Object[] {
            // Insert cases
            new Object[] {"Insert: Invalid index", new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE, -1)},
            // Replace cases
            new Object[] {"Replace: Invalid index", new ReplaceRecorder(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE, -1)},
            // Remove cases
            new Object[] {"Remove: Invalid index", new RemoveRecorder(PARENT_NODE, OLD_CHILD_NODE, -1)},
        };
    }


    @Test
    @Parameters(method = "testInvalidSingleRecordParameters")
    @TestCaseName("Invalid Single Rewrite Event: {0}")
    public void testInvalidSingleRecord(@SuppressWarnings("unused") final String testCaseName,
                                        final Recorder recorder) {
        try {
            // Do the actual record
            recorder.record(rewriteEventsRecorder);
            fail(); // Reach here if the expected exception has not been thrown
        } catch (final Exception ignored) {
            // Expected flow
        }
    }

    // -----------------** Merge Rewrite Events Test Cases ***----------------- //
    // -----------------* Valid Cases *----------------- //
    @SuppressWarnings("unused") // Used by JUnitParams in `testValidMergeRewriteEvents` test case
    private Object testValidMergeRewriteEventsParameters() {
        return new Object[] {
            // `Insert` As Original Event Test Cases
            new Object[] { // insert -> insert
                ORIGINAL_INSERT_RECORDER,
                new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE_2, INSERT_I),
                newInsertRewriteEvent(PARENT_NODE, NEW_CHILD_NODE_2, INSERT_I),
                INSERT_I,
                new InsertedNewRewriteEventExpectation(),
            },
            new Object[] { // insert -> replace
                ORIGINAL_INSERT_RECORDER,
                new ReplaceRecorder(PARENT_NODE, NEW_CHILD_NODE, NEW_CHILD_NODE_2, INSERT_I),
                newInsertRewriteEvent(PARENT_NODE, NEW_CHILD_NODE_2, INSERT_I),
                INSERT_I,
                new ReplacedOriginalRewriteEventExpectation(),
            },
            new Object[] { // insert -> remove
                ORIGINAL_INSERT_RECORDER,
                new RemoveRecorder(PARENT_NODE, NEW_CHILD_NODE, INSERT_I),
                null, // not expecting new rewrite event
                INSERT_I,
                new RemovedOriginalRewriteEventExpectation(),
            },
            // `Replace` As Original Event Test Cases
            new Object[] { // replace -> insert
                ORIGINAL_REPLACE_RECORDER,
                new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE_2, REPLACE_I),
                newInsertRewriteEvent(PARENT_NODE, NEW_CHILD_NODE_2, REPLACE_I),
                REPLACE_I,
                new InsertedNewRewriteEventExpectation(),
            },
            new Object[] { // replace -> replace
                ORIGINAL_REPLACE_RECORDER,
                new ReplaceRecorder(PARENT_NODE, NEW_CHILD_NODE, NEW_CHILD_NODE_2, REPLACE_I),
                newReplaceRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE_2, REPLACE_I),
                REPLACE_I,
                new ReplacedOriginalRewriteEventExpectation(),
            },
            new Object[] { // replace -> remove
                ORIGINAL_REPLACE_RECORDER,
                new RemoveRecorder(PARENT_NODE, NEW_CHILD_NODE, REPLACE_I),
                newRemoveRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, REPLACE_I),
                REPLACE_I,
                new ReplacedOriginalRewriteEventExpectation(),
            },
            // `Remove` As Original Event Test Cases
            new Object[] { // remove -> insert
                ORIGINAL_REMOVE_RECORDER,
                new InsertRecorder(PARENT_NODE, NEW_CHILD_NODE_2, REMOVE_I),
                newReplaceRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE_2, REMOVE_I),
                REMOVE_I,
                new ReplacedOriginalRewriteEventExpectation(),
            },
            // remove -> replace & remove -> remove are both invalid cases; check `testInvalidMergeRewriteEvents`
        };
    }

    @Test
    @Parameters(method = "testValidMergeRewriteEventsParameters")
    @TestCaseName("Valid Merge Rewrite Events: Original: <{0}> -> New: <{1}>")
    public void testValidMergeRewriteEvents(final Recorder originalEventRecorder,
                                            final Recorder newEventRecorder,
                                            final RewriteEvent expectedNewRewriteEvent,
                                            final int rewriteEventIndex,
                                            final Expectation expectation) {
        // Record the original event
        originalEventRecorder.record(rewriteEventsRecorder);
        // Grab the original rewrite events
        final RewriteEvent[] originalRewriteEvents = rewriteEventsRecorder.getRewriteEvents();
        // Record the new event
        newEventRecorder.record(rewriteEventsRecorder);
        // Grab the updated rewrite events
        final RewriteEvent[] updatedRewriteEvents = rewriteEventsRecorder.getRewriteEvents();
        // Validate state with the given expectation
        expectation.expect(originalRewriteEvents, updatedRewriteEvents, expectedNewRewriteEvent, rewriteEventIndex);
    }

    // -----------------* Invalid Cases *----------------- //
    @SuppressWarnings("unused") // Used by JUnitParams in `testInvalidMergeRewriteEvents` test case
    private Object testInvalidMergeRewriteEventsParameters() {
        final DummyNode node = new DummyNode(0);
        return new Object[] {
            // Insert cases
            new Object[] {
                "Insert -> Insert: Not the same parent",
                ORIGINAL_INSERT_RECORDER,
                new InsertRecorder(PARENT_NODE_2, NEW_CHILD_NODE_2, INSERT_I),
            },
            new Object[] {
                "Insert -> Replace: Not the same parent",
                ORIGINAL_INSERT_RECORDER,
                new ReplaceRecorder(PARENT_NODE_2, OLD_CHILD_NODE, NEW_CHILD_NODE_2, INSERT_I),
            },
            new Object[] {
                "Insert -> Remove: Not the same parent",
                ORIGINAL_INSERT_RECORDER,
                new RemoveRecorder(PARENT_NODE_2, OLD_CHILD_NODE, INSERT_I),
            },
            // Replace cases
            new Object[] {
                "Replace -> Insert: Not the same parent",
                ORIGINAL_REPLACE_RECORDER,
                new InsertRecorder(PARENT_NODE_2, NEW_CHILD_NODE_2, REPLACE_I),
            },
            new Object[] {
                "Replace -> Replace: Not the same parent",
                ORIGINAL_REPLACE_RECORDER,
                new ReplaceRecorder(PARENT_NODE_2, OLD_CHILD_NODE, NEW_CHILD_NODE_2, REPLACE_I),
            },
            new Object[] {
                "Replace -> Remove: Not the same parent",
                ORIGINAL_REPLACE_RECORDER,
                new RemoveRecorder(PARENT_NODE_2, OLD_CHILD_NODE, REPLACE_I),
            },
            // - [replace->replace] oldChildNode of the new event should be the same as the newChildNode of the original event for merging
            new Object[] {
                "Replace -> Replace: Not matching old event",
                ORIGINAL_REPLACE_RECORDER,
                new ReplaceRecorder(PARENT_NODE, OLD_CHILD_NODE_2, NEW_CHILD_NODE_2, REPLACE_I),
            },
            // - [replace->remove] oldChildNode of the new event should be the same as the newChildNode of the original event for merging
            new Object[] {
                "Replace -> Remove: Not matching old event",
                ORIGINAL_REPLACE_RECORDER,
                new RemoveRecorder(PARENT_NODE, OLD_CHILD_NODE_2, REPLACE_I),
            },
            // Remove cases
            new Object[] {
                "Remove -> Insert: Not the same parent",
                ORIGINAL_REMOVE_RECORDER,
                new InsertRecorder(PARENT_NODE_2, NEW_CHILD_NODE_2, REMOVE_I),
            },
            // - Expecting fail as a remove event cannot be followed by a replace event.
            //      This would mean that an already removed node is then trying to be replaced, which makes no sense
            new Object[] {
                "Remove -> Replace: Remove event should not be followed by a replace event",
                ORIGINAL_REMOVE_RECORDER,
                new ReplaceRecorder(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE_2, REMOVE_I),
            },
            // - Expecting fail as a remove event cannot be followed by another remove event.
            //      This would mean that an already removed node is then trying to be removed again, which makes no sense
            new Object[] {
                "Remove -> Remove: Remove event should not be followed by a remove event",
                ORIGINAL_REMOVE_RECORDER,
                new RemoveRecorder(PARENT_NODE, OLD_CHILD_NODE, REMOVE_I),
            },
        };
    }

    @Test
    @Parameters(method = "testInvalidMergeRewriteEventsParameters")
    @TestCaseName("Invalid Merge Rewrite Events: {0}")
    public void testInvalidMergeRewriteEvents(@SuppressWarnings("unused") final String testCaseName,
                                              final Recorder originalEventRecorder,
                                              final Recorder newEventRecorder) {
        // Record the original event
        originalEventRecorder.record(rewriteEventsRecorder);

        try {
            // Record the new event
            newEventRecorder.record(rewriteEventsRecorder);
            fail(); // Reach here if the expected exception has not been thrown
        } catch (final Exception ignored) {
            // Expected flow
        }
    }

    /**
     * Interface to represent an expectation over a test case.
     * This allows you to send a custom interface implementation as an argument to a test method
     * in order to execute all expectations/validations for that test case,
     * enhancing code reusability (i.e., avoiding duplicated code).
     */
    private interface Expectation {
        /**
         * <p>
         * Execute all expectations/validations for a given test case, contrasting the {@code originalRewriteEvents}
         * array with the {@code updatedRewriteEvents} array, checking if the expected modification (which should have
         * occurred at the {@code rewriteEventIndex}) have been carried out.
         * </p>
         * <p>
         * If {@code expectedNewRewriteEvent} is not null, then it is expected that the {@code updatedRewriteEvents}
         * array contains that event at the {@code rewriteEventIndex} position.
         * </p>
         *
         * @param originalRewriteEvents   The original rewrite events.
         * @param updatedRewriteEvents    The update rewrite events.
         * @param expectedNewRewriteEvent The expected new rewrite event (may be null).
         * @param rewriteEventIndex       The index where the expected rewrite event modification should have been performed.
         */
        void expect(RewriteEvent[] originalRewriteEvents, RewriteEvent[] updatedRewriteEvents, RewriteEvent expectedNewRewriteEvent, int rewriteEventIndex);
    }

    /**
     * This class expects the {@code expectedNewRewriteEvent} to have been inserted at the given
     * {@code rewriteEventIndex} position, and the following rewrite events to have been shifted to the right.
     */
    private static class InsertedNewRewriteEventExpectation implements Expectation {

        @Override
        public void expect(final RewriteEvent[] originalRewriteEvents,
                           final RewriteEvent[] updatedRewriteEvents,
                           final RewriteEvent expectedNewRewriteEvent,
                           final int rewriteEventIndex) {
            // Check updated array size is one more of the original size
            assertEquals(originalRewriteEvents.length + 1, updatedRewriteEvents.length);

            // Check updated array content has the new rewrite event
            int rewriteEventsIndex = 0;
            int updatedRewriteEventsIndex = 0;
            while (rewriteEventsIndex < originalRewriteEvents.length && updatedRewriteEventsIndex < updatedRewriteEvents.length) {
                if (rewriteEventsIndex == rewriteEventIndex && updatedRewriteEventsIndex == rewriteEventIndex) {
                    assertEquals(expectedNewRewriteEvent, updatedRewriteEvents[updatedRewriteEventsIndex++]);
                } else {
                    assertEquals(originalRewriteEvents[rewriteEventsIndex++], updatedRewriteEvents[updatedRewriteEventsIndex++]);
                }
            }
        }
    }

    /**
     * This class expects the original rewrite event at the given {@code rewriteEventIndex} position
     * to have been replaced with the new {@code expectedNewRewriteEvent}.
     */
    private static class ReplacedOriginalRewriteEventExpectation implements Expectation {

        @Override
        public void expect(final RewriteEvent[] originalRewriteEvents,
                           final RewriteEvent[] updatedRewriteEvents,
                           final RewriteEvent expectedNewRewriteEvent,
                           final int rewriteEventIndex) {
            // Check the updated array is of the same size
            assertEquals(originalRewriteEvents.length, updatedRewriteEvents.length);

            // Check that the array has been correctly updated
            for (int i = 0; i < updatedRewriteEvents.length; i++) {
                final RewriteEvent expectedRewriteEvent = i == rewriteEventIndex ? expectedNewRewriteEvent : originalRewriteEvents[i];
                assertEquals(expectedRewriteEvent, updatedRewriteEvents[i]);
            }
        }
    }

    /**
     * This class expects the original rewrite event at the given {@code rewriteEventIndex} position
     * to have been removed.
     */
    private static class RemovedOriginalRewriteEventExpectation implements Expectation {

        @Override
        public void expect(final RewriteEvent[] originalRewriteEvents,
                           final RewriteEvent[] updatedRewriteEvents,
                           final RewriteEvent expectedNewRewriteEvent,
                           final int rewriteEventIndex) {
            if (expectedNewRewriteEvent != null) {
                throw new IllegalArgumentException("Expecting `expectedNewRewriteEvent` to be null");
            }

            // Check updated array size
            assertEquals(originalRewriteEvents.length - 1, updatedRewriteEvents.length);

            // Check updated array content
            int rewriteEventsIndex = 0;
            int updatedRewriteEventsIndex = 0;
            while (rewriteEventsIndex < originalRewriteEvents.length && updatedRewriteEventsIndex < updatedRewriteEvents.length) {
                if (rewriteEventsIndex == rewriteEventIndex && updatedRewriteEventsIndex == rewriteEventIndex) {
                    rewriteEventsIndex++;
                } else {
                    assertEquals(originalRewriteEvents[rewriteEventsIndex++], updatedRewriteEvents[updatedRewriteEventsIndex++]);
                }
            }
        }
    }

    /**
     * Interface representing a record event that will be performed over a {@code RewriteEventsRecorder} instance.
     */
    private interface Recorder {
        /**
         * Record a rewrite event using the given {@code rewriteEventsRecorder} instance.
         *
         * @param rewriteEventsRecorder The instance used to record a rewrite event.
         */
        void record(RewriteEventsRecorder rewriteEventsRecorder);
    }

    private abstract static class AbstractRecorder implements Recorder {
        /* package-private */ final Node parentNode;
        /* package-private */ final Node oldChildNode;
        /* package-private */ final Node newChildNode;
        /* package-private */ final int childIndex;


        /* package-private */ AbstractRecorder(final Node parentNode, final Node oldChildNode, final Node newChildNode, final int childIndex) {
            this.parentNode = parentNode;
            this.oldChildNode = oldChildNode;
            this.newChildNode = newChildNode;
            this.childIndex = childIndex;
        }
    }

    private static class InsertRecorder extends AbstractRecorder {
        private InsertRecorder(final Node parentNode, final Node newChildNode, final int childIndex) {
            super(parentNode, null, newChildNode, childIndex);
        }

        @Override
        public void record(final RewriteEventsRecorder rewriteEventsRecorder) {
            rewriteEventsRecorder.recordInsert(parentNode, newChildNode, childIndex);
        }
    }

    private static class ReplaceRecorder extends AbstractRecorder {
        private ReplaceRecorder(final Node parentNode, final Node oldChildNode, final Node newChildNode, final int childIndex) {
            super(parentNode, oldChildNode, newChildNode, childIndex);
        }

        @Override
        public void record(final RewriteEventsRecorder rewriteEventsRecorder) {
            rewriteEventsRecorder.recordReplace(parentNode, oldChildNode, newChildNode, childIndex);
        }
    }

    private static class RemoveRecorder extends AbstractRecorder {
        private RemoveRecorder(final Node parentNode, final Node oldChildNode, final int childIndex) {
            super(parentNode, oldChildNode, null, childIndex);
        }

        @Override
        public void record(final RewriteEventsRecorder rewriteEventsRecorder) {
            rewriteEventsRecorder.recordRemove(parentNode, oldChildNode, childIndex);
        }
    }
}

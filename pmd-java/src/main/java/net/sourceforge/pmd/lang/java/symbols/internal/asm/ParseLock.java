/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static net.sourceforge.pmd.util.AssertionUtil.isAssertEnabled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple double-checked initializer, that parses something (a class,
 * or a type signature).
 */
@SuppressWarnings({"PMD.AvoidUsingVolatile", "PMD.AvoidCatchingThrowable"})
abstract class ParseLock {

    private static final Logger LOG = LoggerFactory.getLogger(ParseLock.class);

    private volatile ParseStatus status = ParseStatus.NOT_PARSED;
    private final String name;

    protected ParseLock(String name) {
        this.name = name;
    }

    public void ensureParsed() {
        getFinalStatus();
    }


    private void logParseLockTrace(String prefix) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("{} {}: {}", Thread.currentThread().getName(), String.format("%-15s", prefix), this);
        }
    }

    void checkWeAreNotParsingAnother() {
       // by default do nothing, overridden by CheckedParseLock
    }


    void releaseLock() {
        // by default do nothing, overridden by CheckedParseLock
    }

    private ParseStatus getFinalStatus() {
        ParseStatus status = this.status;
        if (!status.isFinished) {
            logParseLockTrace("waiting on");

            try {
                checkWeAreNotParsingAnother();
                synchronized (this) {
                    logParseLockTrace("locked");


                    status = this.status;
                    if (status == ParseStatus.NOT_PARSED) {
                        this.status = ParseStatus.BEING_PARSED;
                        try {
                            boolean success = doParse();
                            status = success ? ParseStatus.FULL : ParseStatus.FAILED;
                            finishParse(!success);
                        } catch (Throwable t) {
                            status = ParseStatus.FAILED;
                            LOG.error("Parsing failed in ParseLock#doParse() of {}", name, t);
                            finishParse(true);
                        }

                        // the status must be updated as last statement, so that
                        // other threads see the status FULL or FAILED only after finishParse()
                        // returns. Otherwise, some fields might not have been initialized.
                        //
                        // Note: the current thread might reenter the parsing logic through
                        // finishParse() -> ... -> ensureParsed(). In that case the status is still BEING_PARSED,
                        // and we don't call finishParse() again. See below for canReenter()
                        this.status = status;

                        assert status.isFinished : "Inconsistent status " + status;
                        assert postCondition() : "Post condition not satisfied after parsing sig " + this;
                    } else if (status == ParseStatus.BEING_PARSED && !canReenter()) {
                        throw new IllegalStateException("Thread is reentering the parse lock");
                    }
                }
            } finally {
                logParseLockTrace("released");
                releaseLock();
            }
        }
        return status;
    }

    protected boolean canReenter() {
        return false;
    }

    public boolean isFailed() {
        return getFinalStatus() == ParseStatus.FAILED;
    }

    public boolean isNotParsed() {
        return status == ParseStatus.NOT_PARSED;
    }

    // will be called in the critical section after parse is done
    protected void finishParse(boolean failed) {
        // by default do nothing
    }

    /** Returns true if parse is successful. */
    protected abstract boolean doParse() throws Throwable; // SUPPRESS CHECKSTYLE IllegalThrows

    /** Checked by an assert after parse. */
    protected boolean postCondition() {
        return true;
    }

    @Override
    public String toString() {
        return "ParseLock{name=" + name + ",status=" + status + '}';
    }

    private enum ParseStatus {
        NOT_PARSED(false),
        BEING_PARSED(false),
        FULL(true),
        FAILED(true);

        final boolean isFinished;

        ParseStatus(boolean finished) {
            this.isFinished = finished;
        }
    }

    /**
     * Subclasses of this assert that any thread can hold at most one lock at a time. Threads cannot even reenter in the
     * lock they currently own. This prevents deadlocks while parsing ClassStub. However, all the instances of all derived
     * subclasses are mutually exclusive. This is meant for the parse lock of ClassStub, and should therefore only be extended
     * by that one.
     */
    abstract static class CheckedParseLock extends ParseLock {
        private static final ThreadLocal<ParseLock> CURRENT_LOCK = new ThreadLocal<>();

        protected CheckedParseLock(String name) {
            super(name);
        }

        @Override
        final void checkWeAreNotParsingAnother() {
            if (isAssertEnabled()) {
                ParseLock lock = CURRENT_LOCK.get();
                if (lock != null) {
                    throw new AssertionError("Parsing " + lock + " requested parsing of " + this);
                }
                CURRENT_LOCK.set(this);
            }
        }


        @Override
        final void releaseLock() {
            if (isAssertEnabled()) {
                ParseLock lock = CURRENT_LOCK.get();
                assert lock == this : "Tried to release different parse lock " + lock + " from " + this; // NOPMD CompareObjectsWithEquals
                CURRENT_LOCK.remove();
            }
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

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

    public void ensureParsed() {
        getFinalStatus();
    }

    private ParseStatus getFinalStatus() {
        ParseStatus status = this.status;
        if (!status.isFinished) {
            synchronized (this) {
                status = this.status;
                if (status == ParseStatus.NOT_PARSED) {
                    this.status = ParseStatus.BEING_PARSED;
                    try {
                        boolean success = doParse();
                        status = success ? ParseStatus.FULL : ParseStatus.FAILED;
                        this.status = status;
                        finishParse(!success);
                    } catch (Throwable t) {
                        status = ParseStatus.FAILED;
                        this.status = status;
                        LOG.error(t.toString(), t);
                        finishParse(true);
                    }
                    assert status.isFinished : "Inconsistent status " + status;
                    assert postCondition() : "Post condition not satisfied after parsing sig " + this;
                } else if (status == ParseStatus.BEING_PARSED && !canReenter()) {
                    throw new IllegalStateException("Thread is reentering the parse lock");
                }
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
        return "ParseLock{status=" + status + '}';
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
}

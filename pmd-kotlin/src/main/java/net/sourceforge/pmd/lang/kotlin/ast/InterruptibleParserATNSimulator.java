/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Set;
import java.util.concurrent.Future;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

/**
 * A {@link ParserATNSimulator} that checks for thread interruption at each
 * recursive closure step. This allows {@link Future#cancel(boolean)} to actually
 * stop an ANTLR parse that has entered exponential ATN state explosion.
 *
 * <p>ANTLR's {@code closure_()} and {@code closureCheckingStopState()} are a
 * tight mutual recursion with no interruption points. Overriding
 * {@code closureCheckingStopState} to check thread interruption lets the
 * timeout mechanism in {@link PmdKotlinParser} terminate the parse thread
 * promptly rather than leaving it spinning until JVM exit.
 */
final class InterruptibleParserATNSimulator extends ParserATNSimulator {

    InterruptibleParserATNSimulator(Parser parser, ATN atn, DFA[] decisionToDfa,
                                    PredictionContextCache sharedContextCache) {
        super(parser, atn, decisionToDfa, sharedContextCache);
    }

    @Override
    protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs,
                                            Set<ATNConfig> closureBusy,
                                            boolean collectPredicates, boolean fullCtx,
                                            int depth, boolean treatEofAsEpsilon) {
        if (Thread.currentThread().isInterrupted()) {
            throw new ParseCancelledException();
        }
        super.closureCheckingStopState(config, configs, closureBusy,
                collectPredicates, fullCtx, depth, treatEofAsEpsilon);
    }

    /** Unchecked exception thrown when the parse thread is interrupted. */
    static final class ParseCancelledException extends RuntimeException {
        ParseCancelledException() {
            super("Parse cancelled due to thread interruption");
        }
    }
}

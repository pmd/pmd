/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.Set;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

/**
 * A {@link ParserATNSimulator} that checks for thread interruption at each
 * recursive closure step. This allows {@link java.util.concurrent.Future#cancel(boolean)}
 * to actually stop an ANTLR parse that has entered exponential ATN state expansion.
 *
 * <p>ANTLR's {@code closure_()} and {@code closureCheckingStopState()} are a
 * tight mutual recursion with no interruption points. Overriding
 * {@code closureCheckingStopState} to check {@link Thread#interrupted()} lets
 * a timeout mechanism terminate the parse thread promptly rather than leaving
 * it spinning until JVM exit.
 *
 * <p><b>Scope:</b> This only intercepts the <em>parser</em>'s ATN closure loop,
 * which is the main source of exponential state explosion in ANTLR grammars.
 * The ANTLR lexer ({@link org.antlr.v4.runtime.atn.LexerATNSimulator}) is not
 * covered here; lexer execution is typically linear in the input length and
 * therefore not a practical source of unbounded loops.
 *
 * <p><b>Usage:</b> create a fresh instance per parse (new {@code DFA[]} array and
 * a new {@link PredictionContextCache}) and inject it into the generated parser via
 * {@link Parser#setInterpreter(org.antlr.v4.runtime.atn.ParserATNSimulator)}.
 * Creating a fresh instance per parse also prevents cross-file ATN state
 * accumulation from the static shared fields in generated ANTLR parsers.
 */
public final class InterruptibleParserATNSimulator extends ParserATNSimulator {

    public InterruptibleParserATNSimulator(Parser parser, ATN atn, DFA[] decisionToDfa,
                                           PredictionContextCache sharedContextCache) {
        super(parser, atn, decisionToDfa, sharedContextCache);
    }

    @Override
    protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs,
                                            Set<ATNConfig> closureBusy,
                                            boolean collectPredicates, boolean fullCtx,
                                            int depth, boolean treatEofAsEpsilon) {
        if (Thread.interrupted()) {
            throw new ParseCancelledException();
        }
        super.closureCheckingStopState(config, configs, closureBusy,
                collectPredicates, fullCtx, depth, treatEofAsEpsilon);
    }

    /** Unchecked exception thrown when the parse thread is interrupted. */
    public static final class ParseCancelledException extends RuntimeException {
        public ParseCancelledException() {
            super("Parse cancelled due to thread interruption");
        }
    }
}

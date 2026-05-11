/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Adapter for the KotlinParser.
 *
 * <p>Error handling strategy:
 * <ul>
 *   <li><b>Lexer errors</b> (e.g. unrecognized tokens): logged as
 *       {@code [WARN] Lexer error at <file>:<line>:<col>: ...} and a
 *       {@link net.sourceforge.pmd.lang.ast.LexException} is thrown immediately.</li>
 *   <li><b>Parser errors</b> (e.g. unexpected token structure): logged as
 *       {@code [WARN] Parser error at <file>:<line>:<col>: ...} with a short message
 *       (without the verbose expected-token list), full details at DEBUG.
 *       A {@link net.sourceforge.pmd.lang.ast.ParseException} is thrown immediately.
 *       PMD reports the file as a {@code ProcessingError} and skips rule analysis for it.
 *       All other files continue to be processed. Exit code 5 is returned by the CLI if
 *       any processing errors occurred (suppressible with {@code --no-fail-on-error}).</li>
 * </ul>
 */
public final class PmdKotlinParser extends AntlrBaseParser<KotlinNode, KtKotlinFile> {

    private static final Logger LOG = LoggerFactory.getLogger(PmdKotlinParser.class);

    @Override
    protected KtKotlinFile parse(final Lexer lexer, ParserTask task) {
        lexer.removeErrorListeners();
        lexer.addErrorListener(buildLexerErrorListener(task));

        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(buildParserErrorListener(task));

        return parser.kotlinFile().makeAstInfo(task);
    }

    private static BaseErrorListener buildLexerErrorListener(ParserTask task) {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                LOG.warn("Lexer error at {}:{}:{}: {}", task.getFileId().getOriginalPath(), line, charPositionInLine, msg);
                throw new LexException(line, charPositionInLine, task.getFileId(), msg, null);
            }
        };
    }

    private static BaseErrorListener buildParserErrorListener(ParserTask task) {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                LOG.warn("Parser error at {}:{}:{}: {}", task.getFileId().getOriginalPath(), line, charPositionInLine, shortMessage(msg));
                LOG.debug("Parser error at {}:{}:{}: {}", task.getFileId().getOriginalPath(), line, charPositionInLine, msg);
                throw new ParseException(shortMessage(msg))
                    .withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine));
            }
        };
    }

    /** Strips the verbose "expecting {...}" token list from ANTLR parser error messages for concise WARN output. */
    private static String shortMessage(String msg) {
        int idx = msg.indexOf(" expecting ");
        return idx >= 0 ? msg.substring(0, idx) : msg;
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }
}

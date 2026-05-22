/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
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
 *   <li><b>Lexer errors</b> (e.g. unrecognized tokens) and <b>Parser errors</b> (e.g. unexpected token structure)
 *       are collected and lexing/parsing continues.</li>
 *   <li>Further lexer/parser errors are collected as suppressed exceptions.</li>
 *   <li>The first occurred lexer/parser exception is thrown at the end and no rules are executed.</li>
 * </ul>
 *
 * <p>PMD reports the file as a {@code ProcessingError} and skips rule analysis for it.
 * All other files continue to be processed. Exit code 5 is returned by the CLI if
 * any processing errors occurred (suppressible with {@code --no-fail-on-error}).
 */
public final class PmdKotlinParser extends AntlrBaseParser<KotlinNode, KtKotlinFile> {

    private static final Logger LOG = LoggerFactory.getLogger(PmdKotlinParser.class);

    @Override
    protected KtKotlinFile parse(final Lexer lexer, ParserTask task) {
        AntlrErrorListener errorListener = new AntlrErrorListener(task);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener.lexerErrorListener());

        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener.parserErrorListener());

        LOG.debug("Parsing Kotlin file {}", task.getFileId());
        KtKotlinFile ktKotlinFile = parser.kotlinFile().makeAstInfo(task);

        if (errorListener.hasErrors()) {
            throw errorListener.getException();
        }

        return ktKotlinFile;
    }

    private static final class AntlrErrorListener {
        private final ParserTask task;
        private FileAnalysisException exception;

        private AntlrErrorListener(ParserTask task) {
            this.task = task;
        }

        private void addException(FileAnalysisException exception) {
            if (this.exception == null) {
                this.exception = exception;
            } else {
                this.exception.addSuppressed(exception);
            }
        }

        boolean hasErrors() {
            return exception != null;
        }

        FileAnalysisException getException() {
            return exception;
        }

        ANTLRErrorListener lexerErrorListener() {
            return new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg, RecognitionException e) {
                    addException(new LexException(line, charPositionInLine, task.getFileId(), msg, null));
                }
            };
        }

        ANTLRErrorListener parserErrorListener() {
            return new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg, RecognitionException e) {
                    addException(new ParseException(msg)
                            .withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine)));
                }
            };
        }
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }
}

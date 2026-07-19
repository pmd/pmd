/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Error listener to be used during lexing and parsing with ANTLR.
 * It collects multiple errors as suppressed exceptions. After parsing
 * is done, the error state should be checked ({@link #hasErrors()})
 * and the exception should be thrown ({@link #getException()}).
 *
 * <p>Use {@link #lexerErrorListener()} and {@link #parserErrorListener()}
 * for registering the error listeners with ANTLR.</p>
 *
 * @since 7.26.0
 */
public class AntlrErrorListener {
    private final Parser.ParserTask task;
    private FileAnalysisException exception;

    public AntlrErrorListener(Parser.ParserTask task) {
        this.task = task;
    }

    public void addException(FileAnalysisException exception) {
        if (this.exception == null) {
            this.exception = exception;
        } else {
            this.exception.addSuppressed(exception);
        }
    }

    public boolean hasErrors() {
        return exception != null;
    }

    public FileAnalysisException getException() {
        return exception;
    }

    /**
     * Records an ANTLR syntax error as a {@link LexException}.
     */
    public ANTLRErrorListener lexerErrorListener() {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                addException(new LexException(line, charPositionInLine + 1, task.getFileId(), msg, e));
            }
        };
    }

    /**
     * Records an ANTLR syntax error as a {@link ParseException}.
     */
    public ANTLRErrorListener parserErrorListener() {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                ParseException parseException = new ParseException(msg)
                        .withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine + 1));
                if (e != null) {
                    parseException.initCause(e);
                }
                addException(parseException);
            }
        };
    }
}

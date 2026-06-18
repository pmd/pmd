/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.document.FileId;

class AntlrErrorListenerTest {
    private Parser.ParserTask parserTask;

    @BeforeEach
    void setUp() {
        parserTask = Mockito.mock(Parser.ParserTask.class);
        Mockito.when(parserTask.getFileId()).thenReturn(FileId.fromPathLikeString("file.txt"));
    }

    @Test
    void lexerError() {
        AntlrErrorListener listener = new AntlrErrorListener(parserTask);
        assertFalse(listener.hasErrors());

        RecognitionException originalException = new RecognitionException(null, null, null);
        listener.lexerErrorListener().syntaxError(null, null, 1, 2,
                "test lex error", originalException);
        assertTrue(listener.hasErrors());

        FileAnalysisException exception = listener.getException();
        assertInstanceOf(LexException.class, exception);
        assertEquals(0, exception.getSuppressed().length);
        assertEquals("Lexical error in file 'file.txt' at line 1, column 2: test lex error", exception.getMessage());
        assertSame(originalException, exception.getCause());
        assertSame(parserTask.getFileId(), exception.getFileId());
        LexException lexException = (LexException) exception;
        assertEquals(1, lexException.getLine());
        assertEquals(2, lexException.getColumn());
    }

    @Test
    void parserError() {
        AntlrErrorListener listener = new AntlrErrorListener(parserTask);
        assertFalse(listener.hasErrors());

        RecognitionException originalException = new RecognitionException(null, null, null);
        listener.parserErrorListener().syntaxError(null, null, 1, 2,
                "test parser error", originalException);
        assertTrue(listener.hasErrors());

        FileAnalysisException exception = listener.getException();
        assertInstanceOf(ParseException.class, exception);
        assertEquals(0, exception.getSuppressed().length);
        assertEquals("Parse exception in file 'file.txt' at line 1, column 2: test parser error", exception.getMessage());
        assertSame(originalException, exception.getCause());
        assertSame(parserTask.getFileId(), exception.getFileId());
    }

    @Test
    void suppressedErrors() {
        AntlrErrorListener listener = new AntlrErrorListener(parserTask);
        assertFalse(listener.hasErrors());

        listener.parserErrorListener().syntaxError(null, null, 1, 2,
                "first test parser error", null);
        listener.parserErrorListener().syntaxError(null, null, 2, 2,
                "second test parser error", null);
        assertTrue(listener.hasErrors());

        FileAnalysisException exception = listener.getException();
        assertInstanceOf(ParseException.class, exception);
        assertEquals("Parse exception in file 'file.txt' at line 1, column 2: first test parser error", exception.getMessage());
        assertEquals(1, exception.getSuppressed().length);
        assertEquals("Parse exception in file 'file.txt' at line 2, column 2: second test parser error", exception.getSuppressed()[0].getMessage());
    }
}

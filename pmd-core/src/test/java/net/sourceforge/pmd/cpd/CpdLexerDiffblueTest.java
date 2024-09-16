package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.junit.jupiter.api.Test;

class CpdLexerDiffblueTest {
    /**
     * Method under test: {@link CpdLexer#tokenize(CpdLexer, TextDocument)}
     */
    @Test
    void testTokenize() throws IOException {
        // Arrange
        AnyCpdLexer cpdLexer = new AnyCpdLexer();
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getText()).thenReturn(Chars.EMPTY);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        Tokens actualTokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);

        // Assert
        verify(textDocument).getFileId();
        verify(textDocument).getText();
        List<TokenEntry> tokens = actualTokenizeResult.getTokens();
        assertEquals(1, tokens.size());
        TokenEntry getResult = tokens.get(0);
        assertEquals(0, getResult.getIdentifier());
        assertEquals(0, getResult.getIndex());
        assertEquals(1, getResult.getBeginColumn());
        assertEquals(1, getResult.getBeginLine());
        assertEquals(1, getResult.getEndColumn());
        assertEquals(1, getResult.getEndLine());
        assertEquals(1, actualTokenizeResult.size());
        assertTrue(getResult.isEof());
    }

    /**
     * Method under test: {@link CpdLexer#tokenize(CpdLexer, TextDocument, Tokens)}
     */
    @Test
    void testTokenize2() throws IOException {
        // Arrange
        AnyCpdLexer cpdLexer = new AnyCpdLexer();
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getText()).thenReturn(Chars.EMPTY);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = new Tokens();

        // Act
        CpdLexer.tokenize(cpdLexer, textDocument, tokens);

        // Assert
        verify(textDocument).getFileId();
        verify(textDocument).getText();
        List<TokenEntry> tokens2 = tokens.getTokens();
        assertEquals(1, tokens2.size());
        TokenEntry getResult = tokens2.get(0);
        assertEquals(0, getResult.getIdentifier());
        assertEquals(0, getResult.getIndex());
        assertEquals(1, getResult.getBeginColumn());
        assertEquals(1, getResult.getBeginLine());
        assertEquals(1, getResult.getEndColumn());
        assertEquals(1, getResult.getEndLine());
        assertEquals(1, tokens.size());
        assertTrue(getResult.isEof());
    }
}

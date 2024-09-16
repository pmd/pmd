package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TokensDiffblueTest {
    /**
     * Method under test: {@link Tokens#imageFromId(int)}
     */
    @Test
    void testImageFromId() {
        // Arrange, Act and Assert
        assertNull((new Tokens()).imageFromId(1));
    }

    /**
     * Method under test: {@link Tokens#imageFromId(int)}
     */
    @Test
    void testImageFromId2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        String actualImageFromIdResult = CpdLexer.tokenize(cpdLexer, textDocument).imageFromId(1);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertNull(actualImageFromIdResult);
    }

    /**
     * Method under test: {@link Tokens#imageFromId(int)}
     */
    @Test
    void testImageFromId3() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);
        tokenizeResult.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);

        // Act
        String actualImageFromIdResult = tokenizeResult.imageFromId(1);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals("java.lang.Integer", actualImageFromIdResult);
    }

    /**
     * Method under test: {@link Tokens#imageFromId(int)}
     */
    @Test
    void testImageFromId4() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);
        tokenizeResult.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);

        // Act
        String actualImageFromIdResult = tokenizeResult.imageFromId(0);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertNull(actualImageFromIdResult);
    }

    /**
     * Method under test: {@link Tokens#imageFromId(int)}
     */
    @Test
    void testImageFromId5() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);
        tokenizeResult.addToken("Image", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);
        tokenizeResult.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);

        // Act
        String actualImageFromIdResult = tokenizeResult.imageFromId(0);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertNull(actualImageFromIdResult);
    }

    /**
     * Method under test: {@link Tokens#size()}
     */
    @Test
    void testSize() {
        // Arrange, Act and Assert
        assertEquals(0, (new Tokens()).size());
    }

    /**
     * Method under test: {@link Tokens#size()}
     */
    @Test
    void testSize2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        int actualSizeResult = CpdLexer.tokenize(cpdLexer, textDocument).size();

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, actualSizeResult);
    }

    /**
     * Method under test: {@link Tokens#getEndToken(TokenEntry, Match)}
     */
    @Test
    void testGetEndToken() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);
        TokenEntry mark = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        TokenEntry actualEndToken = tokenizeResult.getEndToken(mark,
                new Match(1, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(mark, actualEndToken);
    }

    /**
     * Method under test:
     * {@link Tokens#addToken(String, FileId, int, int, int, int)}
     */
    @Test
    void testAddToken() {
        // Arrange
        Tokens tokens = new Tokens();
        FileId fileName = CpdTestUtils.BAR_FILE_ID;

        // Act
        TokenEntry actualAddTokenResult = tokens.addToken("Image", fileName, 2, 1, 3, 3);

        // Assert
        assertEquals(0, actualAddTokenResult.getIndex());
        assertEquals(1, tokens.getTokens().size());
        assertEquals(1, actualAddTokenResult.getBeginColumn());
        assertEquals(1, actualAddTokenResult.getIdentifier());
        assertEquals(1, tokens.size());
        assertEquals(2, actualAddTokenResult.getBeginLine());
        assertEquals(3, actualAddTokenResult.getEndColumn());
        assertEquals(3, actualAddTokenResult.getEndLine());
        assertFalse(actualAddTokenResult.isEof());
        assertSame(fileName, actualAddTokenResult.getFileId());
    }

    /**
     * Method under test:
     * {@link Tokens#addToken(String, FileId, int, int, int, int)}
     */
    @Test
    void testAddToken2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokenizeResult = CpdLexer.tokenize(cpdLexer, textDocument);
        FileId fileName = CpdTestUtils.BAR_FILE_ID;

        // Act
        TokenEntry actualAddTokenResult = tokenizeResult.addToken("Image", fileName, 1, 1, 1, 3);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, actualAddTokenResult.getBeginColumn());
        assertEquals(1, actualAddTokenResult.getBeginLine());
        assertEquals(1, actualAddTokenResult.getEndLine());
        assertEquals(1, actualAddTokenResult.getIdentifier());
        assertEquals(1, actualAddTokenResult.getIndex());
        assertEquals(2, tokenizeResult.getTokens().size());
        assertEquals(2, tokenizeResult.size());
        assertEquals(3, actualAddTokenResult.getEndColumn());
        assertFalse(actualAddTokenResult.isEof());
        assertSame(fileName, actualAddTokenResult.getFileId());
    }

    /**
     * Method under test: {@link Tokens#savePoint()}
     */
    @Test
    void testSavePoint() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        CpdLexer.tokenize(cpdLexer, textDocument).savePoint();

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link Tokens#factoryForFile(TextDocument, Tokens)}
     */
    @Test
    void testFactoryForFile() {
        // Arrange
        TextDocument file = mock(TextDocument.class);
        when(file.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        Tokens.factoryForFile(file, new Tokens());

        // Assert
        verify(file).getFileId();
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>default or parameterless constructor of {@link Tokens}
     *   <li>{@link Tokens#getTokens()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange, Act and Assert
        assertTrue((new Tokens()).getTokens().isEmpty());
    }

    /**
     * Method under test: {@link Tokens.State#State(Tokens)}
     */
    @Test
    void testStateNewState() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        new Tokens.State(CpdLexer.tokenize(cpdLexer, textDocument));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link Tokens.State#restore(Tokens)}
     */
    @Test
    void testStateRestore() {
        // Arrange
        Tokens.State state = new Tokens.State(new Tokens());
        Tokens tokens = new Tokens();

        // Act
        state.restore(tokens);

        // Assert
        assertEquals(0, tokens.size());
        assertTrue(tokens.getTokens().isEmpty());
    }

    /**
     * Method under test: {@link Tokens.State#restore(Tokens)}
     */
    @Test
    void testStateRestore2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens.State state = new Tokens.State(CpdLexer.tokenize(cpdLexer, textDocument));

        Tokens tokens = new Tokens();
        tokens.addToken("Image", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);

        // Act
        state.restore(tokens);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, tokens.getTokens().size());
        assertEquals(1, tokens.size());
    }

    /**
     * Method under test: {@link Tokens.State#restore(Tokens)}
     */
    @Test
    void testStateRestore3() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens.State state = new Tokens.State(CpdLexer.tokenize(cpdLexer, textDocument));

        Tokens tokens = new Tokens();
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        tokens.addToken("Image", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);

        // Act
        state.restore(tokens);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, tokens.getTokens().size());
        assertEquals(1, tokens.size());
    }
}

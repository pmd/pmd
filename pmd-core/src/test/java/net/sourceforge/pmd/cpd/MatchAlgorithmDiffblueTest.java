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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.document.FileLocation;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRange2d;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MatchAlgorithmDiffblueTest {
    /**
     * Method under test: {@link MatchAlgorithm#tokenAt(int, TokenEntry)}
     */
    @Test
    void testTokenAt() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1);
        TokenEntry m = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act
        TokenEntry actualTokenAtResult = matchAlgorithm.tokenAt(0, m);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(m, actualTokenAtResult);
    }

    /**
     * Method under test: {@link MatchAlgorithm#getMinimumTileSize()}
     */
    @Test
    void testGetMinimumTileSize() {
        // Arrange, Act and Assert
        assertEquals(1, (new MatchAlgorithm(new Tokens(), 1)).getMinimumTileSize());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches() {
        // Arrange
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(new Tokens(), 1);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act and Assert
        assertTrue(matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>())).isEmpty());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertTrue(actualFindMatchesResult.isEmpty());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches3() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 0);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertTrue(actualFindMatchesResult.isEmpty());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches4() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        tokens.addToken("Image", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 0);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, actualFindMatchesResult.size());
        Match getResult = actualFindMatchesResult.get(0);
        Mark secondMark = getResult.getSecondMark();
        FileLocation location = secondMark.getLocation();
        TextPos2d startPos = location.getStartPos();
        assertEquals("(line=1, column=2)", startPos.toTupleString());
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=2)", endPos.toTupleString());
        assertEquals("1:2", startPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("1:2-2:2", toRange2dResult.toDisplayStringWithColon());
        assertEquals("2:2", endPos.toDisplayStringWithColon());
        assertEquals("line 1, column 2", startPos.toDisplayStringInEnglish());
        assertEquals("line 2, column 2", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        Mark firstMark = getResult.getFirstMark();
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(0, getResult.getEndIndex());
        assertEquals(0, getResult.getTokenCount());
        TokenEntry endToken = firstMark.getEndToken();
        assertEquals(0, endToken.getIdentifier());
        assertEquals(0, endToken.getIndex());
        assertEquals(1, firstMark.getBeginTokenIndex());
        assertEquals(1, secondMark.getEndTokenIndex());
        assertEquals(1, endToken.getBeginColumn());
        assertEquals(1, endToken.getBeginLine());
        TokenEntry token = firstMark.getToken();
        assertEquals(1, token.getBeginLine());
        assertEquals(1, endToken.getEndColumn());
        assertEquals(1, endToken.getEndLine());
        assertEquals(1, token.getIdentifier());
        assertEquals(1, token.getIndex());
        assertEquals(1, location.getStartLine());
        assertEquals(1, startPos.getLine());
        assertEquals(1, toRange2dResult.getStartLine());
        assertEquals(2, getResult.getMarkSet().size());
        assertEquals(2, secondMark.getBeginTokenIndex());
        assertEquals(2, getResult.getMarkCount());
        assertEquals(2, token.getBeginColumn());
        assertEquals(2, token.getEndColumn());
        assertEquals(2, token.getEndLine());
        assertEquals(2, location.getEndColumn());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getLineCount());
        assertEquals(2, location.getStartColumn());
        assertEquals(2, endPos.getColumn());
        assertEquals(2, startPos.getColumn());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndColumn());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartColumn());
        assertFalse(token.isEof());
        assertTrue(endToken.isEof());
        assertEquals(token, secondMark.getToken());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(startPos, toRange2dResult.getStartPos());
        assertSame(token, secondMark.getEndToken());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches5() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, -1);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertTrue(actualFindMatchesResult.isEmpty());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches6() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("42", CpdTestUtils.BAR_FILE_ID, 37, 37, 1, 1);
        tokens.addToken("42", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 0);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, actualFindMatchesResult.size());
        Match getResult = actualFindMatchesResult.get(0);
        Mark firstMark = getResult.getFirstMark();
        assertEquals(1, firstMark.getBeginTokenIndex());
        assertEquals(1, firstMark.getEndTokenIndex());
        assertEquals(1, getResult.getEndIndex());
        assertEquals(1, getResult.getTokenCount());
        TokenEntry endToken = firstMark.getEndToken();
        assertEquals(1, endToken.getEndColumn());
        assertEquals(1, endToken.getEndLine());
        assertEquals(1, endToken.getIdentifier());
        assertEquals(1, endToken.getIndex());
        assertEquals(2, getResult.getMarkSet().size());
        assertEquals(2, getResult.getMarkCount());
        assertEquals(37, endToken.getBeginColumn());
        assertEquals(37, endToken.getBeginLine());
        assertFalse(endToken.isEof());
        assertEquals(firstMark, getResult.getSecondMark());
        assertSame(endToken, firstMark.getToken());
    }

    /**
     * Method under test:
     * {@link MatchAlgorithm#findMatches(CPDListener, SourceManager)}
     */
    @Test
    void testFindMatches7() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("Image", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);
        tokens.addToken("42", CpdTestUtils.BAR_FILE_ID, 37, 37, 1, 1);
        tokens.addToken("Image", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 0);
        CPDNullListener cpdListener = new CPDNullListener();

        // Act
        List<Match> actualFindMatchesResult = matchAlgorithm.findMatches(cpdListener, new SourceManager(new ArrayList<>()));

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(2, actualFindMatchesResult.size());
        Match getResult = actualFindMatchesResult.get(0);
        Mark firstMark = getResult.getFirstMark();
        FileLocation location = firstMark.getLocation();
        TextPos2d startPos = location.getStartPos();
        assertEquals("(line=2, column=1)", startPos.toTupleString());
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=3, column=3)", endPos.toTupleString());
        assertEquals("2:1", startPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("2:1-3:3", toRange2dResult.toDisplayStringWithColon());
        assertEquals("3:3", endPos.toDisplayStringWithColon());
        assertEquals("line 2, column 1", startPos.toDisplayStringInEnglish());
        assertEquals("line 3, column 3", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        Match getResult2 = actualFindMatchesResult.get(1);
        Mark firstMark2 = getResult2.getFirstMark();
        assertEquals(0, firstMark2.getEndTokenIndex());
        assertEquals(0, getResult2.getEndIndex());
        assertEquals(0, getResult2.getTokenCount());
        TokenEntry endToken = firstMark2.getEndToken();
        assertEquals(0, endToken.getIdentifier());
        assertEquals(0, endToken.getIndex());
        assertEquals(1, firstMark.getBeginTokenIndex());
        assertEquals(1, firstMark2.getBeginTokenIndex());
        assertEquals(1, firstMark.getEndTokenIndex());
        assertEquals(1, getResult.getEndIndex());
        assertEquals(1, getResult.getTokenCount());
        TokenEntry endToken2 = firstMark.getEndToken();
        assertEquals(1, endToken2.getBeginColumn());
        assertEquals(1, endToken.getBeginColumn());
        assertEquals(1, endToken.getBeginLine());
        assertEquals(1, endToken.getEndColumn());
        assertEquals(1, endToken.getEndLine());
        assertEquals(1, endToken2.getIdentifier());
        assertEquals(1, endToken2.getIndex());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, startPos.getColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, getResult.getMarkSet().size());
        assertEquals(2, getResult.getLineCount());
        assertEquals(2, getResult.getMarkCount());
        assertEquals(2, endToken2.getBeginLine());
        assertEquals(2, location.getLineCount());
        assertEquals(2, location.getStartLine());
        assertEquals(2, startPos.getLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(3, endToken2.getEndColumn());
        assertEquals(3, endToken2.getEndLine());
        assertEquals(3, location.getEndColumn());
        assertEquals(3, location.getEndLine());
        assertEquals(3, endPos.getColumn());
        assertEquals(3, endPos.getLine());
        assertEquals(3, toRange2dResult.getEndColumn());
        assertEquals(3, toRange2dResult.getEndLine());
        assertEquals(4, getResult2.getMarkSet().size());
        assertEquals(4, getResult2.getMarkCount());
        assertFalse(endToken2.isEof());
        assertTrue(endToken.isEof());
        assertEquals(firstMark, getResult.getSecondMark());
        assertEquals(firstMark, getResult2.getSecondMark());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(startPos, toRange2dResult.getStartPos());
        assertSame(endToken2, firstMark.getToken());
        assertSame(endToken2, firstMark2.getToken());
    }

    /**
     * Method under test: {@link MatchAlgorithm#MatchAlgorithm(Tokens, int)}
     */
    @Test
    void testNewMatchAlgorithm() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        MatchAlgorithm actualMatchAlgorithm = new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertEquals(1, actualMatchAlgorithm.getMinimumTileSize());
    }
}

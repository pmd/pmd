package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class MatchCollectorDiffblueTest {
    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchCollector matchCollector = new MatchCollector(
                new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1));

        // Act
        matchCollector.collect(new ArrayList<>());

        // Assert that nothing has changed
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect2() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchCollector matchCollector = new MatchCollector(
                new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        matchCollector.collect(marks);

        // Assert that nothing has changed
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect3() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchCollector matchCollector = new MatchCollector(
                new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        matchCollector.collect(marks);

        // Assert that nothing has changed
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect4() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchCollector matchCollector = new MatchCollector(
                new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 0));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        marks.add(tokenEntry);
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        matchCollector.collect(marks);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        List<Match> matches = matchCollector.getMatches();
        assertEquals(1, matches.size());
        Match getResult = matches.get(0);
        Mark firstMark = getResult.getFirstMark();
        FileLocation location = firstMark.getLocation();
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        assertEquals(-1, getResult.getEndIndex());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(0, getResult.getTokenCount());
        assertEquals(1, getResult.getMarkSet().size());
        assertEquals(1, getResult.getLineCount());
        assertEquals(1, getResult.getMarkCount());
        assertEquals(1, location.getEndColumn());
        assertEquals(1, location.getLineCount());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(endPos, location.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
        assertSame(tokenEntry, firstMark.getEndToken());
        assertSame(tokenEntry, firstMark.getToken());
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect5() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        MatchCollector matchCollector = new MatchCollector(
                new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 0));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        marks.add(tokenEntry);
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        matchCollector.collect(marks);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        List<Match> matches = matchCollector.getMatches();
        assertEquals(1, matches.size());
        Match getResult = matches.get(0);
        Mark firstMark = getResult.getFirstMark();
        FileLocation location = firstMark.getLocation();
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        assertEquals(-1, getResult.getEndIndex());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(0, getResult.getTokenCount());
        assertEquals(1, getResult.getMarkSet().size());
        assertEquals(1, getResult.getLineCount());
        assertEquals(1, getResult.getMarkCount());
        assertEquals(1, location.getEndColumn());
        assertEquals(1, location.getLineCount());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(endPos, location.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
        assertSame(tokenEntry, firstMark.getEndToken());
        assertSame(tokenEntry, firstMark.getToken());
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect6() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchCollector matchCollector = new MatchCollector(new MatchAlgorithm(tokens, 1));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        marks.add(new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1));

        // Act
        matchCollector.collect(marks);

        // Assert that nothing has changed
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect7() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchCollector matchCollector = new MatchCollector(new MatchAlgorithm(tokens, 0));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        marks.add(tokenEntry);
        TokenEntry tokenEntry2 = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        marks.add(tokenEntry2);

        // Act
        matchCollector.collect(marks);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        List<Match> matches = matchCollector.getMatches();
        assertEquals(1, matches.size());
        Match getResult = matches.get(0);
        Mark secondMark = getResult.getSecondMark();
        FileLocation location = secondMark.getLocation();
        TextPos2d startPos = location.getStartPos();
        assertEquals("(line=1, column=1)", startPos.toTupleString());
        Mark firstMark = getResult.getFirstMark();
        FileLocation location2 = firstMark.getLocation();
        TextPos2d endPos = location2.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        TextPos2d endPos2 = location.getEndPos();
        assertEquals("(line=3, column=3)", endPos2.toTupleString());
        assertEquals("1:1", startPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("1:1-3:3", toRange2dResult.toDisplayStringWithColon());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult2 = location2.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult2.toDisplayStringWithColon());
        assertEquals("3:3", endPos2.toDisplayStringWithColon());
        assertEquals("line 1, column 1", startPos.toDisplayStringInEnglish());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertEquals("line 3, column 3", endPos2.toDisplayStringInEnglish());
        assertNull(location2.getRegionInFile());
        assertNull(location.getRegionInFile());
        assertEquals(-1, getResult.getEndIndex());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(0, getResult.getTokenCount());
        assertEquals(1, secondMark.getBeginTokenIndex());
        assertEquals(1, secondMark.getEndTokenIndex());
        assertEquals(1, getResult.getLineCount());
        assertEquals(1, location2.getEndColumn());
        assertEquals(1, location2.getLineCount());
        assertEquals(1, location2.getStartColumn());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, location.getStartLine());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, startPos.getColumn());
        assertEquals(1, startPos.getLine());
        assertEquals(1, toRange2dResult2.getEndColumn());
        assertEquals(1, toRange2dResult2.getStartColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(1, toRange2dResult.getStartLine());
        assertEquals(2, getResult.getMarkSet().size());
        assertEquals(2, getResult.getMarkCount());
        assertEquals(2, location2.getEndLine());
        assertEquals(2, location2.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult2.getEndLine());
        assertEquals(2, toRange2dResult2.getStartLine());
        assertEquals(3, location.getEndColumn());
        assertEquals(3, location.getEndLine());
        assertEquals(3, location.getLineCount());
        assertEquals(3, endPos2.getColumn());
        assertEquals(3, endPos2.getLine());
        assertEquals(3, toRange2dResult.getEndColumn());
        assertEquals(3, toRange2dResult.getEndLine());
        assertEquals(endPos, location2.getStartPos());
        assertEquals(endPos, toRange2dResult2.getEndPos());
        assertEquals(endPos, toRange2dResult2.getStartPos());
        assertEquals(endPos2, toRange2dResult.getEndPos());
        assertEquals(startPos, toRange2dResult.getStartPos());
        assertSame(tokenEntry, firstMark.getEndToken());
        assertSame(tokenEntry, firstMark.getToken());
        assertSame(tokenEntry2, secondMark.getEndToken());
        assertSame(tokenEntry2, secondMark.getToken());
    }

    /**
     * Method under test: {@link MatchCollector#collect(List)}
     */
    @Test
    void testCollect8() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        Tokens tokens = CpdLexer.tokenize(cpdLexer, textDocument);
        tokens.addToken("java.lang.Integer", CpdTestUtils.BAR_FILE_ID, 1, 2, 2, 2);
        MatchCollector matchCollector = new MatchCollector(new MatchAlgorithm(tokens, 0));

        ArrayList<TokenEntry> marks = new ArrayList<>();
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        marks.add(tokenEntry);
        marks.add(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        TokenEntry tokenEntry2 = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        marks.add(tokenEntry2);

        // Act
        matchCollector.collect(marks);

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        List<Match> matches = matchCollector.getMatches();
        assertEquals(2, matches.size());
        Match getResult = matches.get(1);
        Mark secondMark = getResult.getSecondMark();
        FileLocation location = secondMark.getLocation();
        TextPos2d startPos = location.getStartPos();
        assertEquals("(line=1, column=1)", startPos.toTupleString());
        Match getResult2 = matches.get(0);
        Mark firstMark = getResult2.getFirstMark();
        FileLocation location2 = firstMark.getLocation();
        TextPos2d endPos = location2.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        TextPos2d endPos2 = location.getEndPos();
        assertEquals("(line=3, column=3)", endPos2.toTupleString());
        assertEquals("1:1", startPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("1:1-3:3", toRange2dResult.toDisplayStringWithColon());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult2 = location2.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult2.toDisplayStringWithColon());
        assertEquals("3:3", endPos2.toDisplayStringWithColon());
        assertEquals("line 1, column 1", startPos.toDisplayStringInEnglish());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertEquals("line 3, column 3", endPos2.toDisplayStringInEnglish());
        assertNull(location2.getRegionInFile());
        assertNull(location.getRegionInFile());
        assertEquals(-1, getResult2.getEndIndex());
        assertEquals(-1, getResult.getEndIndex());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(0, getResult2.getTokenCount());
        assertEquals(0, getResult.getTokenCount());
        assertEquals(1, getResult2.getMarkSet().size());
        assertEquals(1, secondMark.getBeginTokenIndex());
        assertEquals(1, secondMark.getEndTokenIndex());
        assertEquals(1, getResult2.getLineCount());
        assertEquals(1, getResult.getLineCount());
        assertEquals(1, getResult2.getMarkCount());
        assertEquals(1, location2.getEndColumn());
        assertEquals(1, location2.getLineCount());
        assertEquals(1, location2.getStartColumn());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, location.getStartLine());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, startPos.getColumn());
        assertEquals(1, startPos.getLine());
        assertEquals(1, toRange2dResult2.getEndColumn());
        assertEquals(1, toRange2dResult2.getStartColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(1, toRange2dResult.getStartLine());
        assertEquals(2, getResult.getMarkSet().size());
        assertEquals(2, getResult.getMarkCount());
        assertEquals(2, location2.getEndLine());
        assertEquals(2, location2.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult2.getEndLine());
        assertEquals(2, toRange2dResult2.getStartLine());
        assertEquals(3, location.getEndColumn());
        assertEquals(3, location.getEndLine());
        assertEquals(3, location.getLineCount());
        assertEquals(3, endPos2.getColumn());
        assertEquals(3, endPos2.getLine());
        assertEquals(3, toRange2dResult.getEndColumn());
        assertEquals(3, toRange2dResult.getEndLine());
        assertEquals(firstMark, getResult.getFirstMark());
        assertEquals(endPos, location2.getStartPos());
        assertEquals(endPos, toRange2dResult2.getEndPos());
        assertEquals(endPos, toRange2dResult2.getStartPos());
        assertEquals(endPos2, toRange2dResult.getEndPos());
        assertEquals(startPos, toRange2dResult.getStartPos());
        assertSame(tokenEntry, firstMark.getEndToken());
        assertSame(tokenEntry, firstMark.getToken());
        assertSame(tokenEntry2, secondMark.getEndToken());
        assertSame(tokenEntry2, secondMark.getToken());
    }

    /**
     * Method under test: {@link MatchCollector#getMatches()}
     */
    @Test
    void testGetMatches() throws IOException {
        // Arrange
        CpdLexer cpdLexer = mock(CpdLexer.class);
        doNothing().when(cpdLexer).tokenize(Mockito.<TextDocument>any(), Mockito.<TokenFactory>any());
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        List<Match> actualMatches = (new MatchCollector(new MatchAlgorithm(CpdLexer.tokenize(cpdLexer, textDocument), 1)))
                .getMatches();

        // Assert
        verify(cpdLexer).tokenize(isA(TextDocument.class), isA(TokenFactory.class));
        verify(textDocument).getFileId();
        assertTrue(actualMatches.isEmpty());
    }

    /**
     * Method under test: {@link MatchCollector#MatchCollector(MatchAlgorithm)}
     */
    @Test
    void testNewMatchCollector() {
        // Arrange, Act and Assert
        assertTrue((new MatchCollector(new MatchAlgorithm(new Tokens(), 1))).getMatches().isEmpty());
    }
}

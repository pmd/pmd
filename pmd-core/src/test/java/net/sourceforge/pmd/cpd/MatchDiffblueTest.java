package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRange2d;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MatchDiffblueTest {
    /**
     * Method under test: {@link Match#addMark(TokenEntry)}
     */
    @Test
    void testAddMark() {
        // Arrange
        TokenEntry token = mock(TokenEntry.class);
        when(token.getIndex()).thenReturn(1);
        when(token.compareTo(Mockito.<TokenEntry>any())).thenReturn(1);
        Mark first = new Mark(token);
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        // Act
        match.addMark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Assert
        verify(token).compareTo(isA(TokenEntry.class));
        verify(token, atLeast(1)).getIndex();
        assertEquals(2, match.getMarkSet().size());
        assertEquals(2, match.getMarkCount());
    }

    /**
     * Method under test: {@link Match#addMark(TokenEntry)}
     */
    @Test
    void testAddMark2() {
        // Arrange
        TokenEntry token = mock(TokenEntry.class);
        when(token.getIndex()).thenReturn(1);
        when(token.compareTo(Mockito.<TokenEntry>any())).thenReturn(1);
        Mark first = new Mark(token);
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
        TokenEntry entry = mock(TokenEntry.class);
        when(entry.compareTo(Mockito.<TokenEntry>any())).thenReturn(1);

        // Act
        match.addMark(entry);

        // Assert
        verify(token).compareTo(isA(TokenEntry.class));
        verify(entry).compareTo(isA(TokenEntry.class));
        verify(token).getIndex();
        assertEquals(3, match.getMarkSet().size());
        assertEquals(3, match.getMarkCount());
    }

    /**
     * Method under test: {@link Match#getMarkCount()}
     */
    @Test
    void testGetMarkCount() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(1, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getMarkCount());
    }

    /**
     * Method under test: {@link Match#getMarkCount()}
     */
    @Test
    void testGetMarkCount2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertEquals(1, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getMarkCount());
    }

    /**
     * Method under test: {@link Match#getLineCount()}
     */
    @Test
    void testGetLineCount() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(1, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getLineCount());
    }

    /**
     * Method under test: {@link Match#getLineCount()}
     */
    @Test
    void testGetLineCount2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertEquals(1, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getLineCount());
    }

    /**
     * Method under test: {@link Match#getLineCount()}
     */
    @Test
    void testGetLineCount3() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        first.setEndToken(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(1, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getLineCount());
    }

    /**
     * Method under test: {@link Match#getLineCount()}
     */
    @Test
    void testGetLineCount4() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 1, 1));
        first.setEndToken(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(2, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getLineCount());
    }

    /**
     * Method under test: {@link Match#getMarkSet()}
     */
    @Test
    void testGetMarkSet() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(1,
                (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getMarkSet().size());
    }

    /**
     * Method under test: {@link Match#getMarkSet()}
     */
    @Test
    void testGetMarkSet2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertEquals(1,
                (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getMarkSet().size());
    }

    /**
     * Method under test: {@link Match#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     PrivateEntryIterator.expectedModCount
        //     PrivateEntryIterator.lastReturned
        //     PrivateEntryIterator.next

        // Arrange
        // TODO: Populate arranged inputs
        Match match = null;

        // Act
        Iterator<Mark> actualIteratorResult = match.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link Match#compareTo(Match)}
     */
    @Test
    void testCompareTo() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(0, match.compareTo(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))));
    }

    /**
     * Method under test: {@link Match#compareTo(Match)}
     */
    @Test
    void testCompareTo2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        Match match = new Match(1, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(2, match.compareTo(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))));
    }

    /**
     * Method under test: {@link Match#compareTo(Match)}
     */
    @Test
    void testCompareTo3() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(0, match.compareTo(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))));
    }

    /**
     * Method under test: {@link Match#getFirstMark()}
     */
    @Test
    void testGetFirstMark() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertSame(first, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getFirstMark());
    }

    /**
     * Method under test: {@link Match#getFirstMark()}
     */
    @Test
    void testGetFirstMark2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertSame(first, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getFirstMark());
    }

    /**
     * Method under test: {@link Match#getSecondMark()}
     */
    @Test
    void testGetSecondMark() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertThrows(NoSuchElementException.class,
                () -> (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getSecondMark());
    }

    /**
     * Method under test: {@link Match#getSecondMark()}
     */
    @Test
    void testGetSecondMark2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1));

        // Act and Assert
        assertSame(first, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getSecondMark());
    }

    /**
     * Method under test: {@link Match#getSecondMark()}
     */
    @Test
    void testGetSecondMark3() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));
        Mark second = new Mark(new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1));

        // Act and Assert
        assertSame(second, (new Match(3, first, second)).getSecondMark());
    }

    /**
     * Method under test: {@link Match#getEndIndex()}
     */
    @Test
    void testGetEndIndex() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(2, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getEndIndex());
    }

    /**
     * Method under test: {@link Match#getEndIndex()}
     */
    @Test
    void testGetEndIndex2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertEquals(2, (new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)))).getEndIndex());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link Match#toString()}
     *   <li>{@link Match#getTokenCount()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        // Act
        String actualToStringResult = match.toString();

        // Assert
        assertEquals("Match: \ntokenCount = 3\nmarks = 1", actualToStringResult);
        assertEquals(3, match.getTokenCount());
    }

    /**
     * Method under test: {@link Match#Match(int, Mark, Mark)}
     */
    @Test
    void testNewMatch() {
        // Arrange
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        Match actualMatch = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        // Assert
        assertEquals(1, actualMatch.getMarkSet().size());
        assertEquals(1, actualMatch.getLineCount());
        assertEquals(1, actualMatch.getMarkCount());
        assertEquals(2, actualMatch.getEndIndex());
        assertEquals(3, actualMatch.getTokenCount());
        assertSame(first, actualMatch.getFirstMark());
    }

    /**
     * Method under test: {@link Match#Match(int, Mark, Mark)}
     */
    @Test
    void testNewMatch2() {
        // Arrange
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act
        Match actualMatch = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        // Assert
        assertEquals(1, actualMatch.getMarkSet().size());
        assertEquals(1, actualMatch.getLineCount());
        assertEquals(1, actualMatch.getMarkCount());
        assertEquals(2, actualMatch.getEndIndex());
        assertEquals(3, actualMatch.getTokenCount());
        assertSame(first, actualMatch.getFirstMark());
    }

    /**
     * Method under test: {@link Match#Match(int, TokenEntry, TokenEntry)}
     */
    @Test
    void testNewMatch3() {
        // Arrange
        TokenEntry first = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act
        Match actualMatch = new Match(3, first, new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Assert
        Mark firstMark = actualMatch.getFirstMark();
        FileLocation location = firstMark.getLocation();
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(1, actualMatch.getMarkSet().size());
        assertEquals(1, actualMatch.getLineCount());
        assertEquals(1, actualMatch.getMarkCount());
        assertEquals(1, location.getEndColumn());
        assertEquals(1, location.getLineCount());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, actualMatch.getEndIndex());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(3, actualMatch.getTokenCount());
        assertEquals(endPos, location.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
        assertSame(first, firstMark.getEndToken());
        assertSame(first, firstMark.getToken());
    }

    /**
     * Method under test: {@link Match#Match(int, TokenEntry, TokenEntry)}
     */
    @Test
    void testNewMatch4() {
        // Arrange
        TokenEntry first = new TokenEntry(mock(FileId.class), 2, 1);

        // Act
        Match actualMatch = new Match(3, first, new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Assert
        Mark firstMark = actualMatch.getFirstMark();
        FileLocation location = firstMark.getLocation();
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(location.getRegionInFile());
        assertEquals(0, firstMark.getBeginTokenIndex());
        assertEquals(0, firstMark.getEndTokenIndex());
        assertEquals(1, actualMatch.getMarkSet().size());
        assertEquals(1, actualMatch.getLineCount());
        assertEquals(1, actualMatch.getMarkCount());
        assertEquals(1, location.getEndColumn());
        assertEquals(1, location.getLineCount());
        assertEquals(1, location.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, actualMatch.getEndIndex());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(3, actualMatch.getTokenCount());
        assertEquals(endPos, location.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
        assertSame(first, firstMark.getEndToken());
        assertSame(first, firstMark.getToken());
    }
}

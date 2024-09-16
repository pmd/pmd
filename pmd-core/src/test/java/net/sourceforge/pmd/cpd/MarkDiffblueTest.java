package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRange2d;
import org.junit.jupiter.api.Test;

class MarkDiffblueTest {
    /**
     * Method under test: {@link Mark#getEndToken()}
     */
    @Test
    void testGetEndToken() {
        // Arrange
        TokenEntry token = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertSame(token, (new Mark(token)).getEndToken());
    }

    /**
     * Method under test: {@link Mark#getEndToken()}
     */
    @Test
    void testGetEndToken2() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        TokenEntry endToken = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        mark.setEndToken(endToken);

        // Act and Assert
        assertSame(endToken, mark.getEndToken());
    }

    /**
     * Method under test: {@link Mark#getEndToken()}
     */
    @Test
    void testGetEndToken3() {
        // Arrange
        TokenEntry token = new TokenEntry(mock(FileId.class), 2, 1);

        // Act and Assert
        assertSame(token, (new Mark(token)).getEndToken());
    }

    /**
     * Method under test: {@link Mark#getLocation()}
     */
    @Test
    void testGetLocation() {
        // Arrange and Act
        FileLocation actualLocation = (new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))).getLocation();

        // Assert
        TextPos2d endPos = actualLocation.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = actualLocation.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(actualLocation.getRegionInFile());
        assertEquals(1, actualLocation.getEndColumn());
        assertEquals(1, actualLocation.getLineCount());
        assertEquals(1, actualLocation.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, actualLocation.getEndLine());
        assertEquals(2, actualLocation.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(endPos, actualLocation.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
    }

    /**
     * Method under test: {@link Mark#getLocation()}
     */
    @Test
    void testGetLocation2() {
        // Arrange and Act
        FileLocation actualLocation = (new Mark(new TokenEntry(mock(FileId.class), 2, 1))).getLocation();

        // Assert
        TextPos2d endPos = actualLocation.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = actualLocation.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(actualLocation.getRegionInFile());
        assertEquals(1, actualLocation.getEndColumn());
        assertEquals(1, actualLocation.getLineCount());
        assertEquals(1, actualLocation.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, actualLocation.getEndLine());
        assertEquals(2, actualLocation.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(endPos, actualLocation.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
    }

    /**
     * Method under test: {@link Mark#getLocation()}
     */
    @Test
    void testGetLocation3() {
        // Arrange and Act
        FileLocation actualLocation = (new Mark(new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1))).getLocation();

        // Assert
        TextPos2d startPos = actualLocation.getStartPos();
        assertEquals("(line=1, column=1)", startPos.toTupleString());
        TextPos2d endPos = actualLocation.getEndPos();
        assertEquals("(line=3, column=3)", endPos.toTupleString());
        assertEquals("1:1", startPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = actualLocation.toRange2d();
        assertEquals("1:1-3:3", toRange2dResult.toDisplayStringWithColon());
        assertEquals("3:3", endPos.toDisplayStringWithColon());
        assertEquals("line 1, column 1", startPos.toDisplayStringInEnglish());
        assertEquals("line 3, column 3", endPos.toDisplayStringInEnglish());
        assertNull(actualLocation.getRegionInFile());
        assertEquals(1, actualLocation.getStartColumn());
        assertEquals(1, actualLocation.getStartLine());
        assertEquals(1, startPos.getColumn());
        assertEquals(1, startPos.getLine());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(1, toRange2dResult.getStartLine());
        assertEquals(3, actualLocation.getEndColumn());
        assertEquals(3, actualLocation.getEndLine());
        assertEquals(3, actualLocation.getLineCount());
        assertEquals(3, endPos.getColumn());
        assertEquals(3, endPos.getLine());
        assertEquals(3, toRange2dResult.getEndColumn());
        assertEquals(3, toRange2dResult.getEndLine());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(startPos, toRange2dResult.getStartPos());
    }

    /**
     * Method under test: {@link Mark#getLocation()}
     */
    @Test
    void testGetLocation4() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        mark.setEndToken(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act
        FileLocation actualLocation = mark.getLocation();

        // Assert
        TextPos2d endPos = actualLocation.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        TextRange2d toRange2dResult = actualLocation.toRange2d();
        assertEquals("2:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertNull(actualLocation.getRegionInFile());
        assertEquals(1, actualLocation.getEndColumn());
        assertEquals(1, actualLocation.getLineCount());
        assertEquals(1, actualLocation.getStartColumn());
        assertEquals(1, endPos.getColumn());
        assertEquals(1, toRange2dResult.getEndColumn());
        assertEquals(1, toRange2dResult.getStartColumn());
        assertEquals(2, actualLocation.getEndLine());
        assertEquals(2, actualLocation.getStartLine());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(2, toRange2dResult.getStartLine());
        assertEquals(endPos, actualLocation.getStartPos());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertEquals(endPos, toRange2dResult.getStartPos());
    }

    /**
     * Method under test: {@link Mark#getFileId()}
     */
    @Test
    void testGetFileId() {
        // Arrange
        TokenEntry token = mock(TokenEntry.class);
        when(token.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);

        // Act
        (new Mark(token)).getFileId();

        // Assert
        verify(token).getFileId();
    }

    /**
     * Method under test: {@link Mark#getBeginTokenIndex()}
     */
    @Test
    void testGetBeginTokenIndex() {
        // Arrange, Act and Assert
        assertEquals(0, (new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))).getBeginTokenIndex());
        assertEquals(0, (new Mark(new TokenEntry(mock(FileId.class), 2, 1))).getBeginTokenIndex());
    }

    /**
     * Method under test: {@link Mark#getEndTokenIndex()}
     */
    @Test
    void testGetEndTokenIndex() {
        // Arrange, Act and Assert
        assertEquals(0, (new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))).getEndTokenIndex());
        assertEquals(0, (new Mark(new TokenEntry(mock(FileId.class), 2, 1))).getEndTokenIndex());
    }

    /**
     * Method under test: {@link Mark#getEndTokenIndex()}
     */
    @Test
    void testGetEndTokenIndex2() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        mark.setEndToken(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(0, mark.getEndTokenIndex());
    }

    /**
     * Method under test: {@link Mark#setEndToken(TokenEntry)}
     */
    @Test
    void testSetEndToken() {
        // Arrange
        TokenEntry token = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        Mark mark = new Mark(token);

        // Act
        mark.setEndToken(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Assert
        assertSame(token, mark.getToken());
    }

    /**
     * Method under test: {@link Mark#setEndToken(TokenEntry)}
     */
    @Test
    void testSetEndToken2() {
        // Arrange
        TokenEntry token = new TokenEntry(FileId.STDIN, 2, 1);

        Mark mark = new Mark(token);

        // Act
        mark.setEndToken(new TokenEntry(FileId.STDIN, 2, 1));

        // Assert
        assertSame(token, mark.getToken());
    }

    /**
     * Method under test: {@link Mark#setEndToken(TokenEntry)}
     */
    @Test
    void testSetEndToken3() {
        // Arrange
        FileId fileId = mock(FileId.class);
        when(fileId.getUriString()).thenReturn("file:///var/Bar.java");
        TokenEntry token = new TokenEntry(fileId, 1, 1);

        Mark mark = new Mark(token);
        TokenEntry endToken = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act
        mark.setEndToken(endToken);

        // Assert
        verify(fileId).getUriString();
        FileLocation location = mark.getLocation();
        TextPos2d endPos = location.getEndPos();
        assertEquals("(line=2, column=1)", endPos.toTupleString());
        TextRange2d toRange2dResult = location.toRange2d();
        assertEquals("1:1-2:1", toRange2dResult.toDisplayStringWithColon());
        assertEquals("2:1", endPos.toDisplayStringWithColon());
        assertEquals("line 2, column 1", endPos.toDisplayStringInEnglish());
        assertEquals(2, location.getEndLine());
        assertEquals(2, location.getLineCount());
        assertEquals(2, endPos.getLine());
        assertEquals(2, toRange2dResult.getEndLine());
        assertEquals(endPos, toRange2dResult.getEndPos());
        assertSame(endToken, mark.getEndToken());
        assertSame(token, mark.getToken());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link Mark#equals(Object)}
     *   <li>{@link Mark#hashCode()}
     * </ul>
     */
    @Test
    void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        Mark mark2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(mark, mark2);
        int expectedHashCodeResult = mark.hashCode();
        assertEquals(expectedHashCodeResult, mark2.hashCode());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link Mark#equals(Object)}
     *   <li>{@link Mark#hashCode()}
     * </ul>
     */
    @Test
    void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(mark, mark);
        int expectedHashCodeResult = mark.hashCode();
        assertEquals(expectedHashCodeResult, mark.hashCode());
    }

    /**
     * Method under test: {@link Mark#compareTo(Mark)}
     */
    @Test
    void testCompareTo() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));

        // Act and Assert
        assertEquals(0, mark.compareTo(new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
    }

    /**
     * Method under test: {@link Mark#compareTo(Mark)}
     */
    @Test
    void testCompareTo2() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(mock(FileId.class), 2, 1));

        // Act and Assert
        assertEquals(0, mark.compareTo(new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
    }

    /**
     * Method under test: {@link Mark#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
        // Arrange
        Mark mark = new Mark(new TokenEntry(CpdTestUtils.FOO_FILE_ID, 2, 1));

        // Act and Assert
        assertNotEquals(mark, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
    }

    /**
     * Method under test: {@link Mark#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
        // Arrange
        FileId fileId = mock(FileId.class);
        when(fileId.getUriString()).thenReturn("Uri String");
        Mark mark = new Mark(new TokenEntry(fileId, 2, 1));

        // Act and Assert
        assertNotEquals(mark, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
    }

    /**
     * Method under test: {@link Mark#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsNull_thenReturnNotEqual() {
        // Arrange, Act and Assert
        assertNotEquals(new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)), null);
    }

    /**
     * Method under test: {@link Mark#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
        // Arrange, Act and Assert
        assertNotEquals(new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)), "Different type to Mark");
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link Mark#Mark(TokenEntry)}
     *   <li>{@link Mark#getToken()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        TokenEntry token = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertSame(token, (new Mark(token)).getToken());
    }
}

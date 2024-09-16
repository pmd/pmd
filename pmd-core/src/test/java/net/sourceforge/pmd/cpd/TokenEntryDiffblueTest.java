package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.document.FileId;
import org.junit.jupiter.api.Test;

class TokenEntryDiffblueTest {
    /**
     * Method under test: {@link TokenEntry#isEof()}
     */
    @Test
    void testIsEof() {
        // Arrange, Act and Assert
        assertTrue((new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)).isEof());
        assertTrue((new TokenEntry(mock(FileId.class), 2, 1)).isEof());
        assertFalse((new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1)).isEof());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link TokenEntry#equals(Object)}
     *   <li>{@link TokenEntry#hashCode()}
     * </ul>
     */
    @Test
    void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);
        TokenEntry tokenEntry2 = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertEquals(tokenEntry, tokenEntry2);
        int expectedHashCodeResult = tokenEntry.hashCode();
        assertEquals(expectedHashCodeResult, tokenEntry2.hashCode());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link TokenEntry#equals(Object)}
     *   <li>{@link TokenEntry#hashCode()}
     * </ul>
     */
    @Test
    void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);
        TokenEntry tokenEntry2 = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        // Act and Assert
        assertEquals(tokenEntry, tokenEntry2);
        int expectedHashCodeResult = tokenEntry.hashCode();
        assertEquals(expectedHashCodeResult, tokenEntry2.hashCode());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link TokenEntry#equals(Object)}
     *   <li>{@link TokenEntry#hashCode()}
     * </ul>
     */
    @Test
    void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertEquals(tokenEntry, tokenEntry);
        int expectedHashCodeResult = tokenEntry.hashCode();
        assertEquals(expectedHashCodeResult, tokenEntry.hashCode());
    }

    /**
     * Method under test: {@link TokenEntry#compareTo(TokenEntry)}
     */
    @Test
    void testCompareTo() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertEquals(0, tokenEntry.compareTo(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
    }

    /**
     * Method under test: {@link TokenEntry#compareTo(TokenEntry)}
     */
    @Test
    void testCompareTo2() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(mock(FileId.class), 2, 1);

        // Act and Assert
        assertEquals(0, tokenEntry.compareTo(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act and Assert
        assertEquals("EOF", tokenEntry.getImage(new Tokens()));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage2() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(mock(FileId.class), 2, 1);

        // Act and Assert
        assertEquals("EOF", tokenEntry.getImage(new Tokens()));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage3() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        // Act and Assert
        assertEquals("--unknown--", tokenEntry.getImage(new Tokens()));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage4() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        Tokens tokens = new Tokens();
        tokens.addToken("EOF", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);

        // Act and Assert
        assertEquals("EOF", tokenEntry.getImage(tokens));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage5() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(2, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        Tokens tokens = new Tokens();
        tokens.addToken("EOF", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);

        // Act and Assert
        assertEquals("--unknown--", tokenEntry.getImage(tokens));
    }

    /**
     * Method under test: {@link TokenEntry#getImage(Tokens)}
     */
    @Test
    void testGetImage6() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(2, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        Tokens tokens = new Tokens();
        tokens.addToken("--unknown--", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);
        tokens.addToken("EOF", CpdTestUtils.BAR_FILE_ID, 2, 1, 3, 3);

        // Act and Assert
        assertEquals("EOF", tokenEntry.getImage(tokens));
    }

    /**
     * Method under test: {@link TokenEntry#toString()}
     */
    @Test
    void testToString() {
        // Arrange, Act and Assert
        assertEquals("EOF", (new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)).toString());
        assertEquals("EOF", (new TokenEntry(mock(FileId.class), 2, 1)).toString());
        assertEquals("1", (new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1)).toString());
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.FOO_FILE_ID, 2, 1);

        // Act and Assert
        assertNotEquals(tokenEntry, new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
        // Arrange
        FileId fileId = mock(FileId.class);
        when(fileId.getUriString()).thenReturn("Uri String");
        TokenEntry tokenEntry = new TokenEntry(fileId, 2, 1);

        // Act and Assert
        assertNotEquals(tokenEntry, new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);

        // Act and Assert
        assertNotEquals(tokenEntry, new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1);
        tokenEntry.setHashCode(19088743);

        // Act and Assert
        assertNotEquals(tokenEntry, new TokenEntry(1, CpdTestUtils.BAR_FILE_ID, 1, 1, 3, 3, 1));
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsNull_thenReturnNotEqual() {
        // Arrange, Act and Assert
        assertNotEquals(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1), null);
    }

    /**
     * Method under test: {@link TokenEntry#equals(Object)}
     */
    @Test
    void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
        // Arrange, Act and Assert
        assertNotEquals(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1), "Different type to TokenEntry");
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link TokenEntry#setHashCode(int)}
     *   <li>{@link TokenEntry#setImageIdentifier(int)}
     *   <li>{@link TokenEntry#getBeginColumn()}
     *   <li>{@link TokenEntry#getBeginLine()}
     *   <li>{@link TokenEntry#getEndColumn()}
     *   <li>{@link TokenEntry#getEndLine()}
     *   <li>{@link TokenEntry#getFileId()}
     *   <li>{@link TokenEntry#getIdentifier()}
     *   <li>{@link TokenEntry#getIndex()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        TokenEntry tokenEntry = new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1);

        // Act
        tokenEntry.setHashCode(19088743);
        tokenEntry.setImageIdentifier(1);
        int actualBeginColumn = tokenEntry.getBeginColumn();
        int actualBeginLine = tokenEntry.getBeginLine();
        int actualEndColumn = tokenEntry.getEndColumn();
        int actualEndLine = tokenEntry.getEndLine();
        tokenEntry.getFileId();
        int actualIdentifier = tokenEntry.getIdentifier();

        // Assert that nothing has changed
        assertEquals(0, tokenEntry.getIndex());
        assertEquals(1, actualBeginColumn);
        assertEquals(1, actualEndColumn);
        assertEquals(1, actualIdentifier);
        assertEquals(2, actualBeginLine);
        assertEquals(2, actualEndLine);
    }

    /**
     * Method under test:
     * {@link TokenEntry#TokenEntry(int, FileId, int, int, int, int, int)}
     */
    @Test
    void testNewTokenEntry() {
        // Arrange
        FileId fileId = CpdTestUtils.BAR_FILE_ID;

        // Act
        TokenEntry actualTokenEntry = new TokenEntry(1, fileId, 1, 1, 3, 3, 1);

        // Assert
        assertEquals(1, actualTokenEntry.getBeginColumn());
        assertEquals(1, actualTokenEntry.getBeginLine());
        assertEquals(1, actualTokenEntry.getIdentifier());
        assertEquals(1, actualTokenEntry.getIndex());
        assertEquals(3, actualTokenEntry.getEndColumn());
        assertEquals(3, actualTokenEntry.getEndLine());
        assertFalse(actualTokenEntry.isEof());
        assertSame(fileId, actualTokenEntry.getFileId());
    }

    /**
     * Method under test: {@link TokenEntry#TokenEntry(FileId, int, int)}
     */
    @Test
    void testNewTokenEntry2() {
        // Arrange
        FileId fileId = CpdTestUtils.BAR_FILE_ID;

        // Act
        TokenEntry actualTokenEntry = new TokenEntry(fileId, 2, 1);

        // Assert
        assertEquals(0, actualTokenEntry.getIdentifier());
        assertEquals(0, actualTokenEntry.getIndex());
        assertEquals(1, actualTokenEntry.getBeginColumn());
        assertEquals(1, actualTokenEntry.getEndColumn());
        assertEquals(2, actualTokenEntry.getBeginLine());
        assertEquals(2, actualTokenEntry.getEndLine());
        assertTrue(actualTokenEntry.isEof());
        assertSame(fileId, actualTokenEntry.getFileId());
    }

    /**
     * Method under test: {@link TokenEntry#TokenEntry(FileId, int, int)}
     */
    @Test
    void testNewTokenEntry3() {
        // Arrange
        FileId fileId = mock(FileId.class);

        // Act
        TokenEntry actualTokenEntry = new TokenEntry(fileId, 2, 1);

        // Assert
        assertEquals(0, actualTokenEntry.getIdentifier());
        assertEquals(0, actualTokenEntry.getIndex());
        assertEquals(1, actualTokenEntry.getBeginColumn());
        assertEquals(1, actualTokenEntry.getEndColumn());
        assertEquals(2, actualTokenEntry.getBeginLine());
        assertEquals(2, actualTokenEntry.getEndLine());
        assertTrue(actualTokenEntry.isEof());
        assertSame(fileId, actualTokenEntry.getFileId());
    }
}

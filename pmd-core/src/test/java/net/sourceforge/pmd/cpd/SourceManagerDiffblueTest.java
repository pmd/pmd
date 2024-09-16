package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.document.Chars;

import net.sourceforge.pmd.lang.document.FileId;

import net.sourceforge.pmd.lang.document.SimpleTestTextFile;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;

class SourceManagerDiffblueTest {
    /**
     * Method under test: {@link SourceManager#getTextFiles()}
     */
    @Test
    void testGetTextFiles() {
        // Arrange, Act and Assert
        assertTrue((new SourceManager(new ArrayList<>())).getTextFiles().isEmpty());
    }

    /**
     * Method under test: {@link SourceManager#get(TextFile)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGet() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Parameter language version is null
        //       at net.sourceforge.pmd.util.AssertionUtil.requireParamNotNull(AssertionUtil.java:209)
        //       at net.sourceforge.pmd.lang.document.StringTextFile.<init>(StringTextFile.java:25)
        //       at net.sourceforge.pmd.lang.document.SimpleTestTextFile.<init>(SimpleTestTextFile.java:15)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        SourceManager sourceManager = null;
        TextFile file = null;

        // Act
        TextDocument actualGetResult = sourceManager.get(file);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link SourceManager#size()}
     */
    @Test
    void testSize() {
        // Arrange, Act and Assert
        assertEquals(0, (new SourceManager(new ArrayList<>())).size());
    }

    /**
     * Method under test: {@link SourceManager#close()}
     */
    @Test
    void testClose() throws Exception {
        // Arrange
        SimpleTestTextFile simpleTestTextFile = mock(SimpleTestTextFile.class);
        when(simpleTestTextFile.getFileId()).thenReturn(CpdTestUtils.BAR_FILE_ID);
        doNothing().when(simpleTestTextFile).close();

        ArrayList<TextFile> files = new ArrayList<>();
        files.add(simpleTestTextFile);

        // Act
        (new SourceManager(files)).close();

        // Assert
        verify(simpleTestTextFile).close();
        verify(simpleTestTextFile).getFileId();
    }

    /**
     * Method under test: {@link SourceManager#getSlice(Mark)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetSlice() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.cpd.TokenEntry.getFileId()" because the return value of "net.sourceforge.pmd.cpd.Mark.getToken()" is null
        //       at net.sourceforge.pmd.cpd.SourceManager.getSlice(SourceManager.java:80)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        SourceManager sourceManager = null;
        Mark mark = null;

        // Act
        Chars actualSlice = sourceManager.getSlice(mark);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link SourceManager#getFileDisplayName(FileId)}
     */
    @Test
    void testGetFileDisplayName() {
        // Arrange, Act and Assert
        assertEquals("/var/Bar.java", (new SourceManager(new ArrayList<>())).getFileDisplayName(CpdTestUtils.BAR_FILE_ID));
    }

    /**
     * Method under test: {@link SourceManager#getFileDisplayName(FileId)}
     */
    @Test
    void testGetFileDisplayName2() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        FileId fileId = mock(FileId.class);
        when(fileId.getAbsolutePath()).thenReturn("Absolute Path");

        // Act
        String actualFileDisplayName = sourceManager.getFileDisplayName(fileId);

        // Assert
        verify(fileId).getAbsolutePath();
        assertEquals("Absolute Path", actualFileDisplayName);
    }

    /**
     * Method under test: {@link SourceManager#getFileDisplayName(FileId)}
     */
    @Test
    void testGetFileDisplayName3() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        FileId fileId = mock(FileId.class);
        when(fileId.getAbsolutePath()).thenThrow(new UncheckedIOException(new IOException("foo")));

        // Act and Assert
        assertThrows(UncheckedIOException.class, () -> sourceManager.getFileDisplayName(fileId));
        verify(fileId).getAbsolutePath();
    }

    /**
     * Method under test: {@link SourceManager#SourceManager(List)}
     */
    @Test
    void testNewSourceManager() {
        // Arrange and Act
        SourceManager actualSourceManager = new SourceManager(new ArrayList<>());

        // Assert
        assertEquals(0, actualSourceManager.size());
        assertTrue(actualSourceManager.getTextFiles().isEmpty());
    }
}

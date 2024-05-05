/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdSourceManagerBuilder;
import net.sourceforge.pmd.cpd.TokenFileSet.TokenFile;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRange2d;

/**
 * @author Clément Fournier
 */
public class TokenFileSetTest {


    @Test
    public void testFullWithOffsets() throws Exception {
        try (
            SourceManager sources = buildSourceManager().setFileContent(CpdTestUtils.FOO_FILE_ID, "a b c d").build();
            TextFile file = sources.getTextFiles().get(0);
            TextDocument doc = TextDocument.create(file)) {

            TokenFileSet set = new TokenFileSet(sources);

            TokenFile tokenFile = set.tokenize(doc, (doc2, factory) -> {
                factory.recordToken("a", 0, 1);
                factory.recordToken("b", 2, 3);
            });

            assertEquals(tokenFile.size(), 2);
            assertFalse(tokenFile.isEmpty());

            TokenEntry fst = tokenFile.getTokenEntry(0, doc, sources);
            assertEquals(set.getImage(fst), "a");
            assertEquals(fst.getLocation(), FileLocation.range(file.getFileId(), TextRange2d.range2d(1, 1, 1, 2)));
        }

    }


    @Test
    public void testFullWithLineCol() throws Exception {
        try (
            SourceManager sources = buildSourceManager().setFileContent(CpdTestUtils.FOO_FILE_ID, "a b c d").build();
            TextFile file = sources.getTextFiles().get(0);
            TextDocument doc = TextDocument.create(file)) {

            TokenFileSet set = new TokenFileSet(sources);

            TokenFile tokenFile = set.tokenize(doc, (doc2, factory) -> {
                factory.recordToken("a", 1, 1, 2, 4);
                factory.recordToken("b", 2, 3, 4, 5);
            });

            assertEquals(tokenFile.size(), 2);
            assertFalse(tokenFile.isEmpty());

            TokenEntry fst = tokenFile.getTokenEntry(0, doc, sources);
            assertEquals(set.getImage(fst), "a");
            assertEquals(fst.getLocation(), FileLocation.range(file.getFileId(), TextRange2d.range2d(1, 1, 2, 4)));
        }

    }


    @ParameterizedTest
    @EnumSource
    void testGrowTokenFile(TokenStyle style) {
        TokenFile file = new TokenFile(CpdTestUtils.FOO_FILE_ID);
        style.addDummyToken(file, 0, 1);
        assertEquals(file.size(), 1);
        file.trimToSize(); // trim to have size 1
        assertEquals(file.capacity(), 1);
        style.addDummyToken(file, 0, 1);
        style.addDummyToken(file, 0, 1);
        assertEquals(file.size(), 3);
        assertEquals(file.capacity(), 4);
        assertEquals(file.coordinates().length, 4 * style.coordinateFactor);
    }

    @Test
    void testCloseAfterAddingZeroTokens() {
        TokenFile file = new TokenFile(CpdTestUtils.FOO_FILE_ID);
        file.finish();
        assertEquals(file.size(), 0);
    }



    @ParameterizedTest
    @EnumSource
    void testAddingTokensWithDifferentStyleNotSupported(TokenStyle style) {
        TokenFile file = new TokenFile(CpdTestUtils.FOO_FILE_ID);
        style.addDummyToken(file, 0, 1);
        Assertions.assertThrows(IllegalStateException.class, () -> style.opposite().addDummyToken(file, 1, 2));
    }


    @Test
    void testHashFile() {
        TokenFile file = new TokenFile(CpdTestUtils.FOO_FILE_ID);
        for (int i = 0; i < 40; i++) {
            file.addTokenByOffsets(i, i * 10, i * 10 + 1);
        }
        file.finish();
        assertEquals(file.size(), 40);
        TokenHashMap map = Mockito.mock(TokenHashMap.class);
        file.computeHashesTestOnly(4, map);

        Mockito.verify(map, times(36)).addTokenToHashTable(anyInt(), notNull());
    }

    @Test
    void testHashFileSmallerThanTileSize() {
        TokenFile file = new TokenFile(CpdTestUtils.FOO_FILE_ID);
        for (int i = 0; i < 40; i++) {
            file.addTokenByOffsets(i, i * 10, i * 10 + 1);
        }
        file.finish();
        assertEquals(file.size(), 40);
        TokenHashMap map = Mockito.mock(TokenHashMap.class);
        file.computeHashesTestOnly(100, map);

        Mockito.verify(map, never()).addTokenToHashTable(anyInt(), notNull());
    }



    enum TokenStyle {
        OFFSET(2) {
            @Override
            void addDummyToken(TokenFile tf, int identifier, int coord) {
                tf.addTokenByOffsets(identifier, coord, coord + 1);
            }
        },
        LINE_COLUMN(4) {
            @Override
            void addDummyToken(TokenFile tf, int identifier, int coord) {
                tf.addToken(identifier, coord, 1, coord, 2);
            }
        };

        private final int coordinateFactor;

        TokenStyle(int coordinateFactor) {
            this.coordinateFactor = coordinateFactor;
        }

        abstract void addDummyToken(TokenFile tf, int identifier, int coord);

        TokenStyle opposite() {
            return this == OFFSET ? LINE_COLUMN : OFFSET;
        }

    }

    private static CpdTestUtils.CpdSourceManagerBuilder buildSourceManager() {
        return new CpdSourceManagerBuilder();
    }
}

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

final class TokenFileSet {

    private final List<TokenFile> files = new ArrayList<>();
    private final ConcurrentMap<String, Integer> images = new ConcurrentHashMap<>();

    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;


    int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return null;
    }


    int countDupTokens(SmallTokenEntry fst, SmallTokenEntry snd) {
        int[] f1 = files.get(fst.fileId).identifiers;
        int[] f2 = files.get(snd.fileId).identifiers;
        final int i1 = fst.indexInFile;
        final int i2 = snd.indexInFile;
        if (i1 < 0 || i2 < 0) {
            return 0;
        }

        int i = 0;
        while (i1 + i < f1.length
            && i2 + i < f2.length
            && f1[i1 + i] == f2[i2 + i]) {
            i++;
        }
        return i;
    }


    boolean isPreviousTokenEqual(SmallTokenEntry fst, SmallTokenEntry snd) {
        TokenFile f1 = files.get(fst.fileId);
        TokenFile f2 = files.get(snd.fileId);
        final int i1 = fst.indexInFile - 1;
        final int i2 = snd.indexInFile - 1;
        if (i1 >= 0 && i2 >= 0) {
            return f1.identifiers[i1] == f2.identifiers[i2];
        }
        return false;
    }

    List<List<SmallTokenEntry>> hashAll(int minTileSize, int mod) {
        IntObjectMap<Object> markGroups = new IntObjectHashMap<>();
        int lastMod = 1;
        for (int i = 0; i < minTileSize; i++) {
            lastMod *= mod;
        }
        List<List<SmallTokenEntry>> matches = new ArrayList<>();
        for (TokenFile file : files) {
            file.computeHashes(minTileSize, mod, lastMod, markGroups, matches::add);
        }
        return matches;
    }

    public TokenFile openFile(FileId fileId) {
        synchronized (files) {
            TokenFile tokenFile = new TokenFile(fileId, files.size());
            files.add(tokenFile);
            return tokenFile;
        }
    }

    public TokenEntry toTokenEntry(SmallTokenEntry fstTok) {
        return files.get(fstTok.fileId).getTokenEntry(fstTok.indexInFile);
    }

    public TokenEntry getEndToken(TokenEntry token, Match match) {
        TokenFile tokenFile = files.get(token.getFileIdInternal());
        return tokenFile.getTokenEntry(token.getIndex() + match.getTokenCount() - 1);
    }

    TokenFile tokenize(CpdLexer cpdLexer, TextDocument textDocument) throws IOException {
        try (TokenFileFactory tf = this.factoryForFile(textDocument)) {
            try {
                cpdLexer.tokenize(textDocument, tf);
            } catch (IOException | LexException e) {
                this.files.remove(tf.tokenFile.internalId);
                throw e;
            }
            return tf.tokenFile;
        }
    }

    /**
     * Creates a token factory to process the given file with
     * {@link CpdLexer#tokenize(TextDocument, TokenFactory)}.
     * Tokens are accumulated in the {@link Tokens} parameter.
     *
     * @param file Document for the file to process
     *
     * @return A new token factory
     */
    TokenFileFactory factoryForFile(TextDocument file) {
        return new TokenFileFactory(file);
    }

    final class TokenFileFactory implements TokenFactory {

        final FileId fileId;
        final TokenFile tokenFile;

        TokenFileFactory(TextDocument file) {
            this.fileId = file.getFileId();
            this.tokenFile = openFile(fileId);
        }

        @Override
        public void recordToken(@NonNull String image, int startLine, int startCol, int endLine, int endCol) {
            tokenFile.addToken(getImageId(image), startLine, startCol, endLine, endCol);
        }

        @Override
        public void setImage(TokenEntry entry, @NonNull String newImage) {
            if (!entry.getFileId().equals(fileId)) {
                throw new IllegalArgumentException("Cannot operate on token for different file");
            }
            int indexInFile = entry.getIndex();
            tokenFile.setImage(indexInFile, getImageId(newImage));
        }

        @Override
        public LexException makeLexException(int line, int column, String message, @Nullable Throwable cause) {
            return new LexException(line, column, fileId, message, cause);
        }

        @Override
        public @Nullable TokenEntry peekLastToken() {
            if (tokenFile.isEmpty()) {
                return null; // no token has been added yet in this file
            }
            return tokenFile.getTokenEntry(tokenFile.size - 1);
        }

        @Override
        public void close() {
            tokenFile.finish();
        }
    }

    static final class TokenFile {
        private final int internalId;
        private final FileId fileId;
        private int size = 0;
        private int[] identifiers = new int[256];

        /**
         * Token coordinates are stored contiguously in this array to place
         * them off the hot path and optimize cache loads.
         */
        private int[] coordinates = new int[256 * 4];

        TokenFile(FileId fileId, int internalId) {
            this.fileId = fileId;
            this.internalId = internalId;
        }

        void addToken(int identifier, int beginLine, int beginColumn, int endLine, int endColumn) {
            final int size = this.size;
            if (size == identifiers.length) {
                grow();
            }
            identifiers[size] = identifier;
            assert beginLine > 0 && beginColumn > 0 && endLine > 0 && endColumn > 0;
            // store coordinates
            coordinates[size * 4] = beginLine;
            coordinates[size * 4 + 1] = beginColumn;
            coordinates[size * 4 + 2] = endLine;
            coordinates[size * 4 + 3] = endColumn;
            this.size++;
        }

        TokenEntry getTokenEntry(int i) {
            assert i >= 0 && i < size;
            return new TokenEntry(
                identifiers[i],
                fileId,
                coordinates[i * 4],
                coordinates[i * 4 + 1],
                coordinates[i * 4 + 2],
                coordinates[i * 4 + 3],
                i, // this is a local id
                internalId
            );
        }

        void computeHashes(final int tileSize, final int mod, final int lastMod, IntObjectMap<Object> markGroups, Consumer<List<SmallTokenEntry>> recordList) {
            if (size < tileSize) {
                // nothing to do, the file does not contain a full tile
                return;
            }

            final int size = this.size;
            final int[] hashCodes = new int[size];
            int hash = 0;

            int last = size - tileSize;
            for (int i = size - 1; i >= last; i--) {
                int id = this.identifiers[i];
                hash = mod * hash + id;
                hashCodes[i] = hash;
            }

            for (int i = last - 1; i >= 0; i--) {
                int thisId = identifiers[i];
                int lastId = identifiers[i + tileSize];
                hash = mod * hash + thisId - lastMod * lastId;
                hashCodes[i] = hash;
            }

            for (int i = 0; i < size; i++) {
                int h = hashCodes[i];
                int prevToken = i == 0 ? 0 : identifiers[i - 1];
                SmallTokenEntry thisEntry = new SmallTokenEntry(this.internalId, i, prevToken);
                addTokenToHashTable(markGroups, recordList, h, thisEntry);
            }
        }

        private static void addTokenToHashTable(IntObjectMap<Object> markGroups, Consumer<List<SmallTokenEntry>> recordList, int h, SmallTokenEntry thisEntry) {
            Object curEntry = markGroups.get(h);
            if (curEntry instanceof SmallTokenEntry) {
                List<SmallTokenEntry> arr = new ArrayList<>(2);
                SmallTokenEntry fstTok = (SmallTokenEntry) curEntry;
                arr.add(fstTok);
                markGroups.put(h, arr);
                recordList.accept(arr);
            } else if (curEntry instanceof List) {
                ((List<SmallTokenEntry>) curEntry).add(thisEntry);
            } else {
                markGroups.put(h, thisEntry);
            }
        }

        private void grow() {
            int newLength = identifiers.length * 2;
            this.identifiers = Arrays.copyOf(identifiers, newLength);
            this.coordinates = Arrays.copyOf(coordinates, newLength * 4);
        }

        void setImage(int tokenIdx, int imageId) {
            assert tokenIdx >= 0 && tokenIdx < size;
            this.identifiers[tokenIdx] = imageId;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        public void finish() {
            if (size < identifiers.length) {
                // trim to length
                identifiers = Arrays.copyOf(identifiers, size);
                coordinates = Arrays.copyOf(coordinates, size * 4);
            }
        }
    }

    static final class SmallTokenEntry {
        static final Comparator<SmallTokenEntry> COMP =
            Comparator.<SmallTokenEntry>comparingInt(it -> it.fileId)
                      .thenComparingInt(it -> it.indexInFile);
        final int fileId;
        final int indexInFile;
        final int prevToken;

        SmallTokenEntry(int fileId, int indexInFile, int prevToken) {
            this.fileId = fileId;
            this.indexInFile = indexInFile;
            this.prevToken = prevToken;
        }
    }

}

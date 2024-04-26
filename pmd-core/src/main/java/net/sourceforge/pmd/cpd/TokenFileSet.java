package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    /** A list of token files that are valid (tokenizer did not error). */
    private final List<TokenFile> files = new ArrayList<>();
    /** Global map of string (token images) to an integer identifier. */
    private final ConcurrentMap<String, Integer> images = new ConcurrentHashMap<>();

    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;


    int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Map.Entry::getKey).orElse(null);
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

    public TokenEntry toTokenEntry(SmallTokenEntry fstTok) {
        return files.get(fstTok.fileId).getTokenEntry(fstTok.indexInFile);
    }

    public TokenEntry getEndToken(TokenEntry token, int matchLen) {
        TokenFile tokenFile = files.get(token.getFileIdInternal());
        return tokenFile.getTokenEntry(token.getIndex() + matchLen - 1);
    }

    /** This is called during building. May be called by parallel threads. */
    TokenFile tokenize(CpdLexer cpdLexer, TextDocument textDocument) throws IOException {
        try (TokenFileFactory tf = this.factoryForFile(textDocument)) {
            // This tokenize method may throw, in which case the file is not added to this tokenfileset
            cpdLexer.tokenize(textDocument, tf);
            this.recordFile(tf.tokenFile);
            return tf.tokenFile;
        }
    }

    private void recordFile(TokenFile file) {
        synchronized (files) {
            int id = files.size();
            file.setInternalId(id);
            files.add(file);
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

    public FileId getFileId(int it) {
        return files.get(it).fileId;
    }

    final class TokenFileFactory implements TokenFactory {

        final FileId fileId;
        final TokenFile tokenFile;

        TokenFileFactory(TextDocument file) {
            this.fileId = file.getFileId();
            this.tokenFile = new TokenFile(fileId);
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
        /**
         * This is the index of this file in the containing TokenFileSet's list.
         * Only set if the tokenization did not error, as only then is the TokenFile
         * placed in the list.
         */
        private int internalId = -1;
        private final FileId fileId;
        private int size = 0;
        private int[] identifiers = new int[256];

        /**
         * Token coordinates are stored contiguously in this array to place
         * them off the hot path and optimize cache loads.
         */
        private int[] coordinates = new int[256 * 4];

        TokenFile(FileId fileId) {
            this.fileId = fileId;
        }

        void setInternalId(int internalId) {
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

        /**
         * Build a token entry for the token at index i.
         */
        TokenEntry getTokenEntry(int i) {
            assert i >= 0 && i < size : "Invalid token index " + i + " for size " + size;
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
            int index = markGroups.indexOf(h);
            if (markGroups.indexExists(index)) {
                Object curEntry = markGroups.indexGet(index);
                if (curEntry instanceof SmallTokenEntry) {
                    SmallTokenEntry fstTok = (SmallTokenEntry) curEntry;
                    List<SmallTokenEntry> arr = new ArrayList<>(2);
                    arr.add(fstTok);
                    arr.add(thisEntry);
                    markGroups.indexReplace(index, arr);
                    recordList.accept(arr);
                } else if (curEntry instanceof List) {
                    ((List<SmallTokenEntry>) curEntry).add(thisEntry);
                }
            } else {
                markGroups.indexInsert(index, h, thisEntry);
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

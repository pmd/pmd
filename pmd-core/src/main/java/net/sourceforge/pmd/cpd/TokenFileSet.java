/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Stores the lexed tokens by file (one {@link TokenFile} per file). Token files can be lexed in parallel and recorded
 * into this data structure. Once lexing is done, this data structure implements routines for hashing tokens and for the
 * {@link MatchCollector} to use.
 *
 * <p>This is a more space-efficient replacement for {@link Tokens}, also Tokens never had the ability to lex in parallel.
 */
final class TokenFileSet {

    static final int MOD = 37;
    /** A list of token files that are valid (tokenizer did not error). */
    private final List<TokenFile> files = new ArrayList<>();
    /** Global map of string (token images) to an integer identifier. */
    private final ConcurrentMap<String, Integer> images = new ConcurrentHashMap<>();
    private final SourceManager sourceManager;

    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;
    private CpdState state = CpdState.BUILDING;

    enum CpdState {
        BUILDING, HASHING, MATCHING
    }

    TokenFileSet(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    private void checkState(CpdState state, String mname) {
        assert state == this.state : "Cannot call " + mname + " in state " + this.state;
    }

    int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String getImage(TokenEntry entry) {
        return imageFromId(entry.getIdentifier());
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Count the maximum common prefix length of the token sequence starting at
     * each token + the given offset in their respective file.
     */
    int countDupTokens(SmallTokenEntry fst, SmallTokenEntry snd, int offset) {
        checkState(CpdState.MATCHING, "countDupTokens");

        int[] f1 = files.get(fst.fileId).identifiers;
        int[] f2 = files.get(snd.fileId).identifiers;
        final int i1 = fst.indexInFile + offset;
        final int i2 = snd.indexInFile + offset;
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

    void setState(CpdState state) {
        assert this.state.compareTo(state) < 0: "Cannot change state of " + this.state + " to " + state;
        this.state = state;
    }

    /**
     * The top level hash function. Followed by {@link MatchCollector#collect(List)}
     *
     * @param minTileSize Minimum size of a duplication
     *
     * @return A list of buckets of tokens that have the same hash and should be processed by the matching algorithm.
     */
    Iterator<List<SmallTokenEntry>> hashAll(int minTileSize) {
        checkState(CpdState.HASHING, "hashAll");

        int lastMod = computeTrailingModulus(minTileSize);
        int totalNumTokens = files.parallelStream().mapToInt(TokenFile::size).sum();
        TokenHashMap map = new TokenHashMap(totalNumTokens);
        for (TokenFile file : files) {
            file.computeHashes(minTileSize, lastMod, map);
        }

        return map.getFinalMatches().iterator();
    }

    private static int computeTrailingModulus(int minTileSize) {
        int lastMod = 1;
        for (int i = 0; i < minTileSize; i++) {
            lastMod *= MOD;
        }
        return lastMod;
    }

    public TokenEntry toTokenEntry(SmallTokenEntry fstTok) {
        checkState(CpdState.MATCHING, "toTokenEntry");
        return files.get(fstTok.fileId).getTokenEntry(fstTok.indexInFile, sourceManager);
    }

    public TokenEntry getEndToken(TokenEntry token, int matchLen) {
        checkState(CpdState.MATCHING, "getEndToken");

        TokenFile tokenFile = files.get(token.getFileIdInternal());
        return tokenFile.getTokenEntry(token.getLocalIndex() + matchLen - 1, sourceManager);
    }

    /** This is called during building. May be called by parallel threads. */
    TokenFile tokenize(TextDocument textDocument, CpdLexer cpdLexer) throws IOException {
        checkState(CpdState.BUILDING, "tokenize");

        try (TokenFileFactory tf = new TokenFileFactory(textDocument)) {
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
     * Return the file id from an internal id.
     */
    FileId getFileId(int internalId) {
        return files.get(internalId).fileId;
    }

    final class TokenFileFactory implements TokenFactory {

        final FileId fileId;
        private final TextDocument file;
        final TokenFile tokenFile;

        TokenFileFactory(TextDocument file) {
            this.fileId = file.getFileId();
            this.file = file;
            this.tokenFile = new TokenFile(fileId);
        }

        @Override
        public void recordToken(@NonNull String image, int startLine, int startCol, int endLine, int endCol) {
            tokenFile.addToken(getImageId(image), startLine, startCol, endLine, endCol);
        }

        @Override
        public void recordToken(@NonNull String image, int startOffset, int endOffset) {
            tokenFile.addTokenByOffsets(getImageId(image), startOffset, endOffset);
        }

        @Override
        public void setImage(TokenEntry entry, @NonNull String newImage) {
            if (!entry.getFileId().equals(fileId)) {
                throw new IllegalArgumentException("Cannot operate on token for different file");
            }
            int indexInFile = entry.getLocalIndex();
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
            return tokenFile.getTokenEntry(tokenFile.size - 1, file, sourceManager);
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
         * placed in the list. This is not stable from run to run because the files
         * may be lexed in parallel.
         */
        private int internalId = -1;
        private final FileId fileId;
        private int size = 0;
        private static final int BASE_SIZE = 1024;
        private int[] identifiers = new int[BASE_SIZE];

        /**
         * Token coordinates are stored contiguously in this array to place
         * them off the hot path and optimize cache loads. This is populated
         * as soon as we know whether we'll be storing line/column or offsets
         * in the table. If we store offsets then we need half the size.
         */
        private int[] coordinates = new int[0];
        private OptionalBool offsetCoordinates = OptionalBool.UNKNOWN;

        TokenFile(FileId fileId) {
            this.fileId = fileId;
        }

        void setInternalId(int internalId) {
            this.internalId = internalId;
        }

        void addToken(int identifier, int beginLine, int beginColumn, int endLine, int endColumn) {
            setUseOffsetCoordinates(false);
            assert beginLine > 0 && beginColumn > 0 && endLine > 0 && endColumn > 0;

            final int size = this.size;
            if (size == capacity()) {
                grow();
            }
            identifiers[size] = identifier;
            // store coordinates
            coordinates[size * 4] = beginLine;
            coordinates[size * 4 + 1] = beginColumn;
            coordinates[size * 4 + 2] = endLine;
            coordinates[size * 4 + 3] = endColumn;
            this.size++;
        }

        void addTokenByOffsets(int identifier, int startOffset, int endOffset) {
            setUseOffsetCoordinates(true);
            assert startOffset <= endOffset && startOffset >= 0
                : "startOffset=" + startOffset + ", endOffset=" + endOffset;

            final int size = this.size;
            if (size == capacity()) {
                grow();
            }
            identifiers[size] = identifier;
            // store coordinates
            coordinates[size * 2] = startOffset;
            coordinates[size * 2 + 1] = endOffset;
            this.size++;
        }

        private void setUseOffsetCoordinates(boolean isOffsetCoordinates) {
            boolean wasKnown = this.offsetCoordinates.isKnown();
            if (wasKnown && this.offsetCoordinates.isTrue() != isOffsetCoordinates) {
                throw new IllegalStateException("Cannot record tokens with line/column and with offset in same file");
            }
            if (!wasKnown) {
                this.offsetCoordinates = OptionalBool.definitely(isOffsetCoordinates);
                this.coordinates = new int[lengthOfCoordinatesArray(this.identifiers.length)];
            }
        }

        /**
         * Build a token entry for the token at index i.
         */
        TokenEntry getTokenEntry(int i, SourceManager sourceManager) {
            return getTokenEntry(i, null, sourceManager);
        }

        TokenEntry getTokenEntry(int i, @Nullable TextDocument doc, SourceManager sourceManager) {
            assert i >= 0 && i < size : "Invalid token index " + i + " for size " + size;
            assert offsetCoordinates.isKnown();

            if (offsetCoordinates.isTrue()) {
                TextRegion region = TextRegion.fromBothOffsets(coordinates[i * 2], coordinates[i * 2 + 1]);
                FileLocation loc = doc == null ? sourceManager.toLocation(fileId, region) : doc.toLocation(region);
                return new TokenEntry(
                    identifiers[i],
                    fileId,
                    loc.getStartLine(),
                    loc.getStartColumn(),
                    loc.getEndLine(),
                    loc.getEndColumn(),
                    i,
                    internalId
                );
            }

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

        void computeHashesTestOnly(int tileSize, TokenHashMap map) {
            computeHashes(tileSize, computeTrailingModulus(tileSize), map);
        }

        /**
         * Hash the entire file and put the hashed tokens into the hashmap (key is hash, value is token or list of tokens).
         */
        private void computeHashes(final int tileSize, final int lastMod, TokenHashMap map) {
            final int size = this.size;
            if (size < tileSize) {
                // nothing to do, the file does not contain a full tile
                return;
            }

            final int[] hashCodes = new int[size];
            int hash = 0;

            int last = size - tileSize;
            for (int i = size - 1; i >= last; i--) {
                int id = this.identifiers[i];
                hash = MOD * hash + id;
                hashCodes[i] = hash;
            }

            for (int i = last - 1; i >= 0; i--) {
                int thisId = identifiers[i];
                int lastId = identifiers[i + tileSize];
                hash = MOD * hash + thisId - lastMod * lastId;
                hashCodes[i] = hash;
            }

            // Note we don't put the last `tileSize` tokens in the hashmap
            // because by definition they cannot be followed by at least tileSize tokens
            // and so will never be a match. Additionally, since their hash is incomplete
            // they will often collide and form large buckets.
            for (int i = 0; i < size - tileSize; i++) {
                int h = hashCodes[i];
                int prevToken = i == 0 ? 0 : identifiers[i - 1];
                SmallTokenEntry thisEntry = new SmallTokenEntry(this.internalId, i, prevToken);
                map.addTokenToHashTable(h, thisEntry);
            }
        }

        private int lengthOfCoordinatesArray(int lengthOfIdentifiers) {
            assert lengthOfIdentifiers == 0 || offsetCoordinates.isKnown();
            int coordinateFactor = offsetCoordinates.isTrue() ? 2 : 4;
            return lengthOfIdentifiers * coordinateFactor;
        }

        private void grow() {
            assert offsetCoordinates.isKnown();

            int newLength = identifiers.length * 2;
            this.identifiers = Arrays.copyOf(identifiers, newLength);
            this.coordinates = Arrays.copyOf(coordinates, lengthOfCoordinatesArray(newLength));
        }

        private void setImage(int tokenIdx, int imageId) {
            assert tokenIdx >= 0 && tokenIdx < size;
            this.identifiers[tokenIdx] = imageId;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        void finish() {
            trimToSize();
        }

        // test only
        void trimToSize() {
            if (size < capacity()) {
                // trim to length
                identifiers = Arrays.copyOf(identifiers, size);
                coordinates = Arrays.copyOf(coordinates, lengthOfCoordinatesArray(size));
            }
        }

        // test only
        int capacity() {
            return identifiers.length;
        }

        // test only
        int[] coordinates() {
            return coordinates;
        }
    }

    /**
     * Small token identifier that is basically only an index to find info
     * in the containing {@link TokenFileSet}.
     */
    static final class SmallTokenEntry implements Comparable<SmallTokenEntry> {
        final int fileId;
        final int indexInFile;
        /** This is here to do quick checks for containment in another match during hashing. */
        final int prevToken;

        SmallTokenEntry(int fileId, int indexInFile, int prevToken) {
            this.fileId = fileId;
            this.indexInFile = indexInFile;
            this.prevToken = prevToken;
        }

        /**
         * Note that the compare function depends on the file internal id  which is
         * not stable from run to run.
         */
        @Override
        public int compareTo(SmallTokenEntry o) {
            int cmp = Integer.compare(fileId, o.fileId);
            cmp = cmp != 0 ? cmp : Integer.compare(indexInFile, o.indexInFile);
            return cmp;
        }

        @Override
        public String toString() {
            return "Token(file=" + fileId + ", index=" + indexInFile + ")";
        }

        boolean hasSamePrevToken(SmallTokenEntry other) {
            // If this is the first token in the file, then prevToken
            // does not have a useful value
            return this.indexInFile > 0
                && other.indexInFile > 0
                && this.prevToken == other.prevToken;
        }
    }

}

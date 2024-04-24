package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

final class TokenFileSet {

    private final List<TokenFile> files = new ArrayList<>();
    private final Map<String, Integer> images = new HashMap<>();

    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;


    private int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Map.Entry::getKey).orElse(null);
    }


    int countDupTokens(SmallTokenEntry fst, SmallTokenEntry snd, int offset, int stopAfter) {
        TokenFile f1 = files.get(fst.fileId);
        TokenFile f2 = files.get(snd.fileId);
        final int i1 = fst.indexInFile + offset;
        final int i2 = snd.indexInFile + offset;
        if (i1 < 0 || i2 < 0) {
            return 0;
        }

        int i = 0;
        while (i1 + i < f1.size
            && i2 + i < f2.size
            && f1.identifiers[i1 + i] == f2.identifiers[i2 + i]
            && i < stopAfter) {
            i++;
        }
        return i;
    }

    Map<Integer, Object> hashAll(int minTileSize, int mod) {
        Map<Integer, Object> markGroups = new HashMap<>();
        int lastMod = 1;
        for (int i = 0; i < minTileSize; i++) {
            lastMod *= mod;
        }
        for (TokenFile file : files) {
            file.computeHashes(minTileSize, mod, lastMod, markGroups);
        }
        return markGroups;
    }

    public TokenFile openFile(FileId fileId) {
        TokenFile tokenFile = new TokenFile(fileId, files.size());
        files.add(tokenFile);
        return tokenFile;
    }

    void deleteFile(TokenFile tokenFile) {
        files.remove(tokenFile.internalId);
    }

    public TokenEntry toTokenEntry(SmallTokenEntry fstTok) {
        return files.get(fstTok.fileId).getTokenEntry(fstTok.indexInFile);
    }

    public TokenEntry getEndToken(TokenEntry token, Match match) {
        TokenFile tokenFile = files.get(token.getFileIdInternal());
        return tokenFile.getTokenEntry(token.getIndex() + match.getTokenCount() - 1);
    }

    TokenFile tokenize(CpdLexer cpdLexer, TextDocument textDocument) throws IOException {
        int fileId = this.files.size();
        try (TokenFactory tf = this.factoryForFile(textDocument)) {
            cpdLexer.tokenize(textDocument, tf);
            return this.files.get(fileId);
        } catch (IOException | LexException e) {
            this.files.remove(fileId);
            throw e;
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
    TokenFactory factoryForFile(TextDocument file) {
        return new TokenFactory() {
            final FileId fileId = file.getFileId();
            final TokenFile tokenFile = openFile(fileId);

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
        };
    }

    static final class TokenFile {
        private final int internalId;
        private final FileId fileId;
        private int size = 0;
        private int[] identifiers = new int[256];
        private int[] hashCodes = new int[256];

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

        void computeHashes(final int tileSize, final int mod, final int lastMod, Map<Integer, Object> markGroups) {
            if (size < tileSize) {
                // nothing to do, the file does not contain a full tile
                return;
            }

            final int size = this.size;
            int hash = 0;

            int last = size - tileSize;
            for (int i = size - 1; i >= last; i--) {
                int id = this.identifiers[i];
                hash = mod * hash + id;
                this.hashCodes[i] = hash;
            }

            for (int i = last - 1; i >= 0; i--) {
                int thisId = identifiers[i];
                int lastId = identifiers[i + tileSize];
                hash = mod * hash + thisId - lastMod * lastId;
                this.hashCodes[i] = hash;
            }

            for (int i = 0; i < size; i++) {
                int h = hashCodes[i];
                SmallTokenEntry thisEntry = new SmallTokenEntry(this.internalId, i);
                markGroups.merge(h, thisEntry, (old, thisEntry2) -> {
                    List<SmallTokenEntry> arr;
                    if (old instanceof SmallTokenEntry) {
                        arr = new ArrayList<>(2);
                        SmallTokenEntry fstTok = (SmallTokenEntry) old;
                        arr.add(fstTok);
                    } else {
                        arr = (ArrayList<SmallTokenEntry>) old;
                    }
                    arr.add(thisEntry);
                    return arr;
                });
            }
        }

        private void grow() {
            int newLength = identifiers.length * 2;
            this.identifiers = Arrays.copyOf(identifiers, newLength);
            this.hashCodes = Arrays.copyOf(hashCodes, newLength);
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
            trimToSize();
        }

        private void trimToSize() {
            if (size < identifiers.length) {
                identifiers = Arrays.copyOf(identifiers, size);
                hashCodes = Arrays.copyOf(hashCodes, size);
                coordinates = Arrays.copyOf(coordinates, size * 4);
            }
        }
    }

    static final class SmallTokenEntry {
        final int fileId;
        final int indexInFile;

        SmallTokenEntry(int fileId, int indexInFile) {
            this.fileId = fileId;
            this.indexInFile = indexInFile;
        }

    }
}

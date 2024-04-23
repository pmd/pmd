/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.FileId;

class MatchAlgorithm {

    private static final int MOD = 37;
    private int lastMod = 1;

    private final Tokens tokens;
    private final TokenFileSet tokenFileSet;
    private final int minTileSize;

    MatchAlgorithm(Tokens tokens, int minTileSize) {
        this.tokens = tokens;
        this.tokenFileSet = tokens.tokenFileSet;
        this.minTileSize = minTileSize;
        for (int i = 0; i < minTileSize; i++) {
            lastMod *= MOD;
        }
    }


    boolean tokensMatch(SmallTokenEntry fst, SmallTokenEntry snd, int offset) {
       return tokenFileSet.countDupTokens(fst, snd, offset, 1) == 1;
    }
    int countDupTokens(SmallTokenEntry fst, SmallTokenEntry snd) {
       return tokenFileSet.countDupTokens(fst, snd, 0, Integer.MAX_VALUE);
    }

    public int getMinimumTileSize() {
        return this.minTileSize;
    }

    public List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager) {
        MatchCollector matchCollector = new MatchCollector(this);
        {
            cpdListener.phaseUpdate(CPDListener.HASH);
            Map<?, Object> markGroups = tokenFileSet.hashAll(minTileSize, lastMod);

            cpdListener.phaseUpdate(CPDListener.MATCH);
            markGroups.values()
                      .stream()
                      .filter(it -> it instanceof List)
                      .forEach(it -> {
                          @SuppressWarnings("unchecked")
                          List<SmallTokenEntry> l = (List<SmallTokenEntry>) it;
                          Collections.reverse(l);
                          matchCollector.collect(l);
                      });
            // put markGroups out of scope
        }

        cpdListener.phaseUpdate(CPDListener.GROUPING);
        List<Match> matches = matchCollector.getMatches();
        matches.sort(Comparator.naturalOrder());

        for (Match match : matches) {
            for (Mark mark : match) {
                TokenEntry token = mark.getToken();
                TokenEntry endToken = tokenFileSet.getEndToken(token, match);

                mark.setEndToken(endToken);
            }
        }
        cpdListener.phaseUpdate(CPDListener.DONE);
        return matches;
    }

     TokenEntry toTokenEntry(SmallTokenEntry entry) {
        return tokenFileSet.toTokenEntry(entry);
    }

    static final class TokenFileSet {
        private final List<TokenFile> files = new ArrayList<>();


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

        Map<Integer, Object> hashAll(int minTileSize, int lastMod) {
            Map<Integer, Object> markGroups = new HashMap<>();
            for (TokenFile file : files) {
                file.computeHashes(minTileSize, lastMod, markGroups);
            }
            return markGroups;
        }

        public TokenFile openFile(FileId fileId) {
            TokenFile tokenFile = new TokenFile(this, fileId, files.size());
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
            return tokenFile.getTokenEntry(token.getIndex() + match.getTokenCount());
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

    static final class TokenFile {
        private final int internalId;
        private final TokenFileSet tokenFileSet;
        private final FileId fileId;
        int size = 0;
        private int[] identifiers = new int[256];
        private int[] hashCodes = new int[256];

        /**
         * Token coordinates are stored contiguously in this array to place
         * them off the hot path and optimize cache loads.
         */
        private int[] coordinates = new int[256 * 4];

        TokenFile(TokenFileSet tokenFileSet, FileId fileId, int internalId) {
            this.tokenFileSet = tokenFileSet;
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

        void computeHashes(final int tileSize, final int lastMod, Map<Integer, Object> markGroups) {
            if (size < tileSize) {
                // nothing to do, the file does not contain a full tile
                return;
            }

            final int size = this.size;
            int hash = 0;

            int last = size - tileSize;
            for (int i = size - 1; i >= last; i--) {
                int id = this.identifiers[i];
                hash = MOD * hash + id;
                this.hashCodes[i] = hash;
            }

            for (int i = last - 1; i >= 0; i--) {
                int thisId = identifiers[i];
                int lastId = identifiers[i + tileSize];
                hash = MOD * hash + thisId - lastMod * lastId;
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
    }

}

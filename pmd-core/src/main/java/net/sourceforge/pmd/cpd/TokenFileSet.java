package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

final class TokenFileSet {
    private final List<MatchAlgorithm.TokenFile> files = new ArrayList<>();


    int countDupTokens(MatchAlgorithm.SmallTokenEntry fst, MatchAlgorithm.SmallTokenEntry snd, int offset, int stopAfter) {
        MatchAlgorithm.TokenFile f1 = files.get(fst.fileId);
        MatchAlgorithm.TokenFile f2 = files.get(snd.fileId);
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
        for (MatchAlgorithm.TokenFile file : files) {
            file.computeHashes(minTileSize, lastMod, markGroups);
        }
        return markGroups;
    }

    public MatchAlgorithm.TokenFile openFile(FileId fileId) {
        MatchAlgorithm.TokenFile tokenFile = new MatchAlgorithm.TokenFile(this, fileId, files.size());
        files.add(tokenFile);
        return tokenFile;
    }

    void deleteFile(MatchAlgorithm.TokenFile tokenFile) {
        files.remove(tokenFile.internalId);
    }

    public TokenEntry toTokenEntry(MatchAlgorithm.SmallTokenEntry fstTok) {
        return files.get(fstTok.fileId).getTokenEntry(fstTok.indexInFile);
    }

    public TokenEntry getEndToken(TokenEntry token, Match match) {
        MatchAlgorithm.TokenFile tokenFile = files.get(token.getFileIdInternal());
        return tokenFile.getTokenEntry(token.getIndex() + match.getTokenCount());
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
            final MatchAlgorithm.TokenFile tokenFile = openFile(fileId);

            @Override
            public void recordToken(@NonNull String image, int startLine, int startCol, int endLine, int endCol) {
                tokenFile.addToken(tokens.getImageId(image), startLine, startCol, endLine, endCol);
            }

            @Override
            public void setImage(TokenEntry entry, @NonNull String newImage) {
                if (!entry.getFileId().equals(fileId)) {
                    throw new IllegalArgumentException("Cannot operate on token for different file");
                }
                int indexInFile = entry.getIndex();
                tokenFile.setImage(indexInFile, tokens.getImageId(newImage));
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
                // todo we probably don't need that anymore
                if (false) {
                    TokenEntry tok = peekLastToken();
                    if (tok == null) {
                        tokens.addEof(fileId, 1, 1);
                    } else {
                        tokens.addEof(fileId, tok.getEndLine(), tok.getEndColumn());
                    }
                }
            }
        };
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Global token collector for CPD. This is populated by lexing all files,
 * after which the match algorithm proceeds.
 */
public class Tokens {

    // This stores all the token entries recorded during the run.
    private final List<TokenEntry> tokens = new ArrayList<>();
    final MatchAlgorithm.TokenFileSet tokenFileSet = new MatchAlgorithm.TokenFileSet();
    private final Map<String, Integer> images = new HashMap<>();

    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;

    /**
     * Create a new instance.
     *
     * @apiNote  Internal API
     */
    Tokens() {
        // constructor is package private
    }

    private void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    private void addEof(FileId filePathId, int line, int column) {
        add(new TokenEntry(filePathId, line, column));
    }

    private int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Entry::getKey).orElse(null);
    }


    private TokenEntry getToken(int index) {
        return tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    TokenEntry getEndToken(TokenEntry mark, Match match) {
        return getToken(mark.getIndex() + match.getTokenCount() - 1);
    }

    public List<TokenEntry> getTokens() {
        return tokens;
    }

    TokenEntry addToken(String image, FileId fileName, int startLine, int startCol, int endLine, int endCol) {
        TokenEntry newToken = new TokenEntry(getImageId(image), fileName, startLine, startCol, endLine, endCol, tokens.size(), 0);
        add(newToken);
        return newToken;
    }

    State savePoint() {
        return new State(this);
    }


    /**
     * Creates a token factory to process the given file with
     * {@link CpdLexer#tokenize(TextDocument, TokenFactory)}.
     * Tokens are accumulated in the {@link Tokens} parameter.
     *
     * @param file   Document for the file to process
     * @param tokens Token sink
     *
     * @return A new token factory
     */
    static TokenFactory factoryForFile(TextDocument file, Tokens tokens) {
        return new TokenFactory() {
            final FileId fileId = file.getFileId();
            final MatchAlgorithm.TokenFile tokenFile = tokens.tokenFileSet.openFile(fileId);

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
                if (false){
                TokenEntry tok = peekLastToken();
                if (tok == null) {
                    tokens.addEof(fileId, 1, 1);
                } else {
                    tokens.addEof(fileId, tok.getEndLine(), tok.getEndColumn());
                }}
            }
        };
    }

    /**
     * Helper class to preserve and restore the current state of the token
     * entries.
     */
    static final class State {
        // fixme restoration doesn't work anymore

        private final int tokenCount;
        private final int curImageId;

        State(Tokens tokens) {
            this.tokenCount = tokens.tokens.size();
            this.curImageId = tokens.curImageId;
        }

        public void restore(Tokens tokens) {
            tokens.images.entrySet().removeIf(e -> e.getValue() >= curImageId);
            tokens.curImageId = this.curImageId;

            final List<TokenEntry> entries = tokens.getTokens();
            entries.subList(tokenCount, entries.size()).clear();
        }
    }

}

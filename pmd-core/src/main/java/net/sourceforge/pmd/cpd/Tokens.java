/*
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
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

/**
 * Global token collector for CPD. This is populated by lexing all files,
 * after which the match algorithm proceeds. Note that this is not thread-safe.
 */
public class Tokens {

    // This stores all the token entries recorded during the run.
    private final List<TokenEntry> tokens = new ArrayList<>();
    private final Map<String, Integer> images = new HashMap<>();
    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;


    private void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    private void addEof(FileId filePathId, int line, int column) {
        add(new TokenEntry(filePathId, line, column));
    }

    private void setImage(TokenEntry entry, String newImage) {
        int i = getImageId(newImage);
        entry.setImageIdentifier(i);
    }

    private int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Entry::getKey).orElse(null);
    }

    private TokenEntry peekLastToken() {
        return tokens.isEmpty() ? null : getToken(size() - 1);
    }

    private TokenEntry getToken(int index) {
        return tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public List<TokenEntry> getTokens() {
        return tokens;
    }

    TokenEntry addToken(String image, FileId fileName, int startLine, int startCol, int endLine, int endCol) {
        TokenEntry newToken = new TokenEntry(getImageId(image), fileName, startLine, startCol, endLine, endCol, tokens.size(), -1);
        add(newToken);
        return newToken;
    }

    /**
     * Creates a token factory to process the given file with
     * {@link CpdLexer#tokenize(TextDocument, TokenFactory)}.
     * Tokens are accumulated in the {@link Tokens} parameter.
     *
     * @param file   Document for the file to process
     * @return A new token factory
     */
    public TokenFactory factoryForFile(TextDocument file) {
        return new TokenFactory() {
            final FileId fileId = file.getFileId();
            final int firstToken = size();

            @Override
            public void recordToken(@NonNull String image, int startLine, int startCol, int endLine, int endCol) {
                addToken(image, fileId, startLine, startCol, endLine, endCol);
            }

            @Override
            public void recordToken(@NonNull String image, int startOffset, int endOffset) {
                FileLocation loc = file.toLocation(TextRegion.fromBothOffsets(startOffset, endOffset));
                recordToken(image, loc);
            }

            @Override
            public void setImage(TokenEntry entry, @NonNull String newImage) {
                Tokens.this.setImage(entry, newImage);
            }

            @Override
            public LexException makeLexException(int line, int column, String message, @Nullable Throwable cause) {
                return new LexException(line, column, fileId, message, cause);
            }

            @Override
            public @Nullable TokenEntry peekLastToken() {
                if (size() <= firstToken) {
                    return null; // no token has been added yet in this file
                }
                return Tokens.this.peekLastToken();
            }

            @Override
            public void close() {
                TokenEntry tok = peekLastToken();
                if (tok == null) {
                    addEof(fileId, 1, 1);
                } else {
                    addEof(fileId, tok.getEndLine(), tok.getEndColumn());
                }
            }
        };
    }
}

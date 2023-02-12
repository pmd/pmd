/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Tokens {

    private final List<TokenEntry> tokens = new ArrayList<>();
    private final Map<String, Integer> images = new HashMap<>();
    // the first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;

    private void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    void addEof(String fileName) {
        if (tokens.isEmpty()) {
            add(new TokenEntry(fileName, 1, 1));
            return;
        }

        TokenEntry tok = peekLastToken();
        add(new TokenEntry(fileName, tok.getEndLine(), tok.getEndColumn()));
    }

    void setImage(TokenEntry entry, String newImage) {
        int i = getImageId(newImage);
        entry.setImageIdentifier(i);
    }

    private int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> curImageId++);
    }

    String imageFromId(int i) {
        return images.entrySet().stream().filter(it -> it.getValue() == i).findFirst().map(Entry::getKey).orElse(null);
    }

    TokenEntry peekLastToken() {
        return getToken(size() - 1);
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

    TokenEntry addToken(String image, String fileName, int startLine, int startCol, int endLine, int endCol) {
        TokenEntry newToken = new TokenEntry(getImageId(image), fileName, startLine, startCol, endLine, endCol, tokens.size());
        add(newToken);
        return newToken;
    }

    State savePoint() {
        return new State(this);
    }

    /**
     * Helper class to preserve and restore the current state of the token
     * entries.
     */
    static final class State {

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

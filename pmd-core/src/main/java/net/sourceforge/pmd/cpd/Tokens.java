/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Tokens {

    private final List<TokenEntry> tokens = new ArrayList<>();
    private final Map<String, Integer> images = new HashMap<>();

    private void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    void addEof() {
        add(TokenEntry.EOF);
    }

    void setImage(TokenEntry entry, String newImage) {
        int i = getImageId(newImage);
        entry.setImageIdentifier(i);
    }

    private int getImageId(String newImage) {
        return images.computeIfAbsent(newImage, k -> images.size() + 1);
    }

    public TokenEntry peekLastToken() {
        return get(size() - 1);
    }

    public Iterator<TokenEntry> iterator() {
        return tokens.iterator();
    }

    private TokenEntry get(int index) {
        return tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public TokenEntry getEndToken(TokenEntry mark, Match match) {
        return get(mark.getIndex() + match.getTokenCount() - 1);
    }

    public int getLineCount(TokenEntry mark, Match match) {
        TokenEntry endTok = getEndToken(mark, match);
        if (TokenEntry.EOF.equals(endTok)) {
            endTok = get(mark.getIndex() + match.getTokenCount() - 2);
        }
        return endTok.getBeginLine() - mark.getBeginLine() + 1;
    }

    public List<TokenEntry> getTokens() {
        return tokens;
    }

    TokenEntry addToken(String image, String fileName, int startLine, int startCol, int endLine, int endCol) {
        TokenEntry newToken = new TokenEntry(getImageId(image), fileName,
                                        startLine, startCol,
                                        endLine, endCol,
                                        tokens.size());
        add(newToken);
        return newToken;
    }

    public State savePoint() {
        return new State(this);
    }

    /**
     * Helper class to preserve and restore the current state of the token
     * entries.
     */
    static final class State {

        private final int tokenCount;
        private final int tokensMapSize;

        State(Tokens tokens) {
            this.tokenCount = tokens.tokens.size();
            this.tokensMapSize = tokens.images.size();
        }

        public void restore(Tokens tokens) {
            tokens.images.entrySet().removeIf(e -> e.getValue() > tokensMapSize);

            final List<TokenEntry> entries = tokens.getTokens();
            entries.subList(tokenCount, entries.size()).clear();
        }
    }

}

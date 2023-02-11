/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Tokens {

    private final List<TokenEntry> tokens = new ArrayList<>();

    private static final ThreadLocal<Map<String, Integer>> TOKENS = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };
    private static final ThreadLocal<AtomicInteger> TOKEN_COUNT = new ThreadLocal<AtomicInteger>() {
        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger(0);
        }
    };

    private void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    void addEof() {
        add(TokenEntry.EOF);
    }

    void setImage(TokenEntry entry, String newImage) {
        Integer i = TOKENS.get().get(newImage);
        if (i == null) {
            i = TOKENS.get().size() + 1;
            TOKENS.get().put(newImage, i);
        }
        entry.setImageIdentifier(i);
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

     void addToken(String image, String fileName, int startLine, int startCol, int endLine, int endCol) {

    }
}
